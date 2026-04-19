package springboot.bg.harisauto.payment.controller;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
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
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.payment.client.PaymentClient;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
import springboot.bg.harisauto.payment.dto.response.PaymentResponse;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

/**
 * PaymentController acts as a proxy to the actual payment-gateway service.
 *
 * @author Kristian Popov
 */
@RestController
public class PaymentController {

  private final PaymentClient client;
  private final ShoppingCart shoppingCart;
  private final BookingService bookingService;

  @Value("${stripe.public.key}")
  private String stripePublicKey;

  @Autowired
  public PaymentController(PaymentClient client, ShoppingCart shoppingCart, BookingService bookingService) {
    this.client = client;
    this.shoppingCart = shoppingCart;
    this.bookingService = bookingService;
  }

  /**
   * This endpoint is called by the checkout.html JavaScript.
   * It uses Feign to call the *actual* payment-gateway app.
   */
  @PostMapping("/payment/api/create-payment-intent")
  public PaymentResponse proxyCreatePaymentIntent(@RequestBody PaymentRequest request) {
    return client.createPaymentIntent(request);
  }

  /**
   * Show checkout page.
   *
   * @return The checkout page.
   */
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

    return modelAndView;
  }

  /**
   * Process payment.
   *
   * @param status             status
   * @param redirectAttributes redirectAttributes
   * @return The booking page.
   */
  @GetMapping("/process-payment")
  public ModelAndView processPayment(@RequestParam(name = "redirect_status", required = false) String status,
      RedirectAttributes redirectAttributes, HttpSession session) {

    if ("succeeded".equals(status)) {

      PendingBookingSessionRequest pending = (PendingBookingSessionRequest) session.getAttribute("PENDING_BOOKING");

      if (pending != null) {

        BigDecimal totalPrice = shoppingCart.getTotal();

        bookingService.createBooking(
            pending.getUserId(),
            pending.getFormRequest().getBookingDate(),
            pending.getServiceIds(),
            pending.getFormRequest().getVehicleId(),
            pending.getFormRequest().getAdditionalNotes(),
            pending.getFormRequest().getPaymentMethod(),
            pending.getFormRequest().getPhoneNumber(),
            totalPrice,
            "PAID"
        );
        session.removeAttribute("PENDING_BOOKING");
        shoppingCart.clear();

        redirectAttributes.addFlashAttribute("success", "Booking confirmed and payment received!");
        return new ModelAndView("redirect:/users/my-bookings");

      } else {

        redirectAttributes.addFlashAttribute("error",
            "Payment successful, but session expired. Please contact support with your payment receipt.");
        return new ModelAndView("redirect:/contact-us");
      }

    } else {
      redirectAttributes.addFlashAttribute("error", "Payment failed or was cancelled.");
      return new ModelAndView("redirect:/checkout");
    }
  }
}