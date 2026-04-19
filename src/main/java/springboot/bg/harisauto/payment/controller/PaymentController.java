package springboot.bg.harisauto.payment.controller;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
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
import springboot.bg.harisauto.invoice.model.Invoice;
import springboot.bg.harisauto.invoice.model.InvoiceStatus;
import springboot.bg.harisauto.invoice.service.InvoiceService;
import springboot.bg.harisauto.payment.client.PaymentClient;
import springboot.bg.harisauto.payment.dto.request.PaymentRequest;
import springboot.bg.harisauto.payment.dto.response.PaymentResponse;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.service.VehicleService;
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
  private final InvoiceService invoiceService;
  private final UserService userService;
  private final VehicleService vehicleService;

  @Value("${stripe.public.key}")
  private String stripePublicKey;

  @Autowired
  public PaymentController(PaymentClient client,
                           ShoppingCart shoppingCart,
                           BookingService bookingService,
                           InvoiceService invoiceService,
                           UserService userService,
                           VehicleService vehicleService) {
    this.client = client;
    this.shoppingCart = shoppingCart;
    this.bookingService = bookingService;
    this.invoiceService = invoiceService;
    this.userService = userService;
    this.vehicleService = vehicleService;
  }

  /**
   * This endpoint is called by the checkout.html JavaScript.
   * It uses Feign to call the *actual* payment-gateway app.
   */
  @PostMapping("/payment/api/create-payment-intent")
  public PaymentResponse proxyCreatePaymentIntent(@RequestBody PaymentRequest request) {
    return client.createPaymentIntent(request);
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

    return modelAndView;
  }

  private Invoice completePaidBooking(PendingBookingSessionRequest pending) {

    BigDecimal totalPrice = shoppingCart.getTotal();
    List<CarService> cartItems = List.copyOf(shoppingCart.getItems());

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

    User user = userService.getById(pending.getUserId());
    Vehicle vehicle = vehicleService.getById(pending.getFormRequest().getVehicleId());

    return invoiceService.generate(
        user,
        vehicle,
        cartItems,
        pending.getFormRequest().getBookingDate(),
        pending.getFormRequest().getPaymentMethod(),
        InvoiceStatus.PAID,
        null
    );
  }

  /** Process payment after Stripe redirect. */
  @GetMapping("/process-payment")
  public ModelAndView processPayment(@RequestParam(name = "redirect_status", required = false) String status,
      RedirectAttributes redirectAttributes, HttpSession session) {

    if ("succeeded".equals(status)) {

      PendingBookingSessionRequest pending = (PendingBookingSessionRequest) session.getAttribute("PENDING_BOOKING");

      if (pending != null) {

        Invoice invoice = completePaidBooking(pending);
        session.removeAttribute("PENDING_BOOKING");
        shoppingCart.clear();

        redirectAttributes.addFlashAttribute("success",
            "Booking confirmed. Invoice " + invoice.getInvoiceNumber() + " is ready.");
        return new ModelAndView("redirect:/users/invoices");

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
