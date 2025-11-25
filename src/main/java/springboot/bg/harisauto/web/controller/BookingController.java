package springboot.bg.harisauto.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.booking.dto.BookingRequest;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.service.VehicleService;

/**
 * BookingController.java - Controller for handling booking-related web requests.
 *
 * @author Kristian Popov
 */
@Controller
@RequestMapping("/bookings")
public class BookingController {

  private final UserService userService;
  private final VehicleService vehicleService;
  private final ShoppingCart shoppingCart;

  @Value("${stripe.public.key}")
  private String stripePublicKey;

  /** Constructor. */
  public BookingController(UserService userService,
      VehicleService vehicleService, ShoppingCart shoppingCart) {
    this.userService = userService;
    this.vehicleService = vehicleService;
    this.shoppingCart = shoppingCart;
  }

  /**
   * Show booking page.
   *
   * @return The booking page.
   */
  @GetMapping
  public ModelAndView showBookingPage(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/public/booking");
    modelAndView.addObject("cart", shoppingCart);
    modelAndView.addObject("bookingRequest", new BookingRequest());
    modelAndView.addObject("vehicles", vehicleService.getVehiclesByUser(user));

    return modelAndView;
  }

  /**
   * Show checkout page.
   *
   * @return The checkout page.
   */
  @GetMapping("/checkout")
  public ModelAndView showCheckoutPage() {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/public/checkout");
    modelAndView.addObject("cart", shoppingCart);
    modelAndView.addObject("bookingRequest", new BookingRequest());
    modelAndView.addObject("stripePublicKey", stripePublicKey);

    return modelAndView;
  }

  /**
   * Process payment.
   *
   * @param paymentIntentId paymentIntentId
   * @param status status
   * @param redirectAttributes redirectAttributes
   * @return The booking page.
   */
  @GetMapping("/process-payment")
  public String processPayment(
      @RequestParam(name = "payment_intent", required = false) String paymentIntentId,
      @RequestParam(name = "redirect_status", required = false) String status,
      RedirectAttributes redirectAttributes) {

    if ("succeeded".equals(status)) {
      shoppingCart.clear();
      redirectAttributes.addAttribute("success", "payment_completed");

      return "redirect:/users/my-bookings";
    } else {
      redirectAttributes.addAttribute("error", "payment_failed");
      return "redirect:/bookings/checkout";
    }
  }
}