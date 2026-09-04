package springboot.bg.harisauto.payment.controller;

import feign.FeignException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.booking.dto.request.BookingRequest;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.dev.DevModeProperties;
import lombok.extern.slf4j.Slf4j;
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.model.InvoiceStatus;
import springboot.bg.harisauto.payment.client.PaymentClient;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
import springboot.bg.harisauto.payment.PaymentVerificationProperties;
import springboot.bg.harisauto.payment.dto.response.PaymentIntentResponse;
import springboot.bg.harisauto.payment.dto.response.PaymentResponse;
import springboot.bg.harisauto.payment.service.CheckoutService;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

/**
 * PaymentController acts as a proxy to the actual payment-gateway service.
 *
 * @author Kristian Popov
 */
@Slf4j
@RestController
public class PaymentController {

  private static final String PAYMENT_INTENT_ID = "PAYMENT_INTENT_ID";
  private static final String PAYMENT_INTENT_AMOUNT = "PAYMENT_INTENT_AMOUNT";

  private final PaymentClient client;
  private final ShoppingCart shoppingCart;
  private final CheckoutService checkoutService;
  private final DevModeProperties devModeProperties;
  private final PaymentVerificationProperties verificationProperties;

  @Value("${stripe.public.key}")
  private String stripePublicKey;

  @Autowired
  public PaymentController(PaymentClient client,
                           ShoppingCart shoppingCart,
                           CheckoutService checkoutService,
                           DevModeProperties devModeProperties,
                           PaymentVerificationProperties verificationProperties) {
    this.client = client;
    this.shoppingCart = shoppingCart;
    this.checkoutService = checkoutService;
    this.devModeProperties = devModeProperties;
    this.verificationProperties = verificationProperties;
  }

  /**
   * This endpoint is called by the checkout.html JavaScript.
   * It uses Feign to call the *actual* payment-gateway app.
   */
  @PostMapping("/payment/api/create-payment-intent")
  public PaymentResponse proxyCreatePaymentIntent(@RequestBody PaymentRequest request,
      HttpSession session) {
    PaymentResponse response = client.createPaymentIntent(request);
    // Remember the intent this session is paying, so the return leg cannot be pointed at
    // some other intent, and remember the amount we asked for so it can be compared.
    session.setAttribute(PAYMENT_INTENT_ID, intentIdFrom(response.getClientSecret()));
    session.setAttribute(PAYMENT_INTENT_AMOUNT, request.getAmount());
    return response;
  }

  /** Show checkout page. */
  @GetMapping("/checkout")
  public ModelAndView showCheckoutPage(HttpSession session) {

    if (session.getAttribute("PENDING_BOOKING") == null) {
      return new ModelAndView("redirect:/bookings");
    }

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("public/checkout");
    modelAndView.addObject("cart", shoppingCart);
    modelAndView.addObject("bookingRequest", new BookingRequest());
    modelAndView.addObject("stripePublicKey", stripePublicKey);
    modelAndView.addObject("devMode", devModeProperties.isEnabled());

    return modelAndView;
  }

  /**
   * Handles the browser's return from the payment provider.
   *
   * <p>The {@code redirect_status} parameter is supplied by the user agent and is treated only
   * as "the customer came back" - never as proof that money moved. Whether the invoice is PAID
   * is decided by reading the intent back from payment-service.</p>
   *
   * @param status The provider's redirect status.
   * @param paymentIntentId The intent the provider redirected with.
   * @param redirectAttributes Flash attributes for the redirect.
   * @param session The session holding the pending booking.
   * @return Redirect to the invoices page, or back to checkout on failure.
   */
  @GetMapping("/process-payment")
  public ModelAndView processPayment(
      @RequestParam(name = "redirect_status", required = false) String status,
      @RequestParam(name = "payment_intent", required = false) String paymentIntentId,
      RedirectAttributes redirectAttributes, HttpSession session) {

    if (!"succeeded".equals(status)) {
      redirectAttributes.addFlashAttribute("error", "Payment failed or was cancelled.");
      return new ModelAndView("redirect:/checkout");
    }

    PendingBookingSessionRequest pending =
        (PendingBookingSessionRequest) session.getAttribute("PENDING_BOOKING");
    if (pending == null) {
      redirectAttributes.addFlashAttribute("error",
          "Payment successful, but session expired. Please contact support with your payment receipt.");
      return new ModelAndView("redirect:/contact-us");
    }

    String expectedIntentId = (String) session.getAttribute(PAYMENT_INTENT_ID);
    if (expectedIntentId == null || !expectedIntentId.equals(paymentIntentId)) {
      log.warn("Rejected /process-payment: returned intent {} does not match the one created "
          + "for this session ({})", paymentIntentId, expectedIntentId);
      redirectAttributes.addFlashAttribute("error",
          "We could not match this payment to your checkout. Please try again.");
      return new ModelAndView("redirect:/checkout");
    }

    InvoiceStatus invoiceStatus = resolveInvoiceStatus(paymentIntentId, session);
    if (invoiceStatus == null) {
      redirectAttributes.addFlashAttribute("error",
          "We could not confirm your payment. No charge has been recorded - please try again.");
      return new ModelAndView("redirect:/checkout");
    }

    Invoice invoice;
    try {
      invoice = checkoutService.completeBooking(pending, invoiceStatus, paymentIntentId);
    } catch (IllegalStateException ex) {
      redirectAttributes.addFlashAttribute("error", ex.getMessage());
      return new ModelAndView("redirect:/checkout");
    }

    session.removeAttribute("PENDING_BOOKING");
    session.removeAttribute(PAYMENT_INTENT_ID);
    session.removeAttribute(PAYMENT_INTENT_AMOUNT);
    shoppingCart.clear();

    redirectAttributes.addFlashAttribute("success",
        invoiceStatus == InvoiceStatus.PAID
            ? "Booking confirmed. Invoice " + invoice.getInvoiceNumber() + " is ready."
            : "Booking received. Invoice " + invoice.getInvoiceNumber()
              + " is awaiting payment confirmation.");
    return new ModelAndView("redirect:/users/invoices");
  }

  /**
   * Establishes the invoice status for a returning payment.
   *
   * @param paymentIntentId The intent to confirm.
   * @param session The session holding the requested amount.
   * @return PAID when confirmed, PENDING when verification is switched off, or null when the
   *         payment could not be confirmed and nothing should be created.
   */
  private InvoiceStatus resolveInvoiceStatus(String paymentIntentId, HttpSession session) {

    if (!verificationProperties.isEnabled()) {
      // payment-service cannot be asked yet. Record the booking, but never claim it is paid
      // on the strength of a query parameter - leave it to be reconciled.
      log.warn("Payment verification is disabled; invoice for intent {} is recorded as PENDING. "
          + "Enable payment.verification.enabled once payment-service exposes the intent.",
          paymentIntentId);
      return InvoiceStatus.PENDING;
    }

    try {
      PaymentIntentResponse intent = client.getPaymentIntent(paymentIntentId);
      if (intent == null || !"succeeded".equals(intent.getStatus())) {
        log.warn("Payment intent {} reports status {}", paymentIntentId,
            intent == null ? "unknown" : intent.getStatus());
        return null;
      }
      Long expectedAmount = (Long) session.getAttribute(PAYMENT_INTENT_AMOUNT);
      if (expectedAmount != null && !expectedAmount.equals(intent.getAmountReceived())) {
        log.warn("Payment intent {} received {} but {} was requested", paymentIntentId,
            intent.getAmountReceived(), expectedAmount);
        return null;
      }
      return InvoiceStatus.PAID;
    } catch (FeignException ex) {
      log.error("Could not verify payment intent {}: {}", paymentIntentId, ex.getMessage());
      return null;
    }
  }

  /**
   * Extracts the intent id from a client secret of the form {@code pi_123_secret_abc}.
   *
   * @param clientSecret The secret returned by the payment service.
   * @return The intent id, or null when it cannot be determined.
   */
  private String intentIdFrom(String clientSecret) {
    if (clientSecret == null) {
      return null;
    }
    int marker = clientSecret.indexOf("_secret_");
    return marker > 0 ? clientSecret.substring(0, marker) : clientSecret;
  }
}
