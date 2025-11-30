package springboot.bg.harisauto.web.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.service.VehicleService;
import springboot.bg.harisauto.web.dto.BookingFormRequest;
import springboot.bg.harisauto.web.dto.PendingBookingSessionRequest;

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
  private final BookingService bookingService;

  /** Constructor. */
  public BookingController(UserService userService,
                           VehicleService vehicleService, ShoppingCart shoppingCart, BookingService bookingService) {
    this.userService = userService;
    this.vehicleService = vehicleService;
    this.shoppingCart = shoppingCart;
    this.bookingService = bookingService;
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
    modelAndView.addObject("bookingFormRequest", new BookingFormRequest());
    modelAndView.addObject("vehicles", vehicleService.getVehiclesByUser(user));

    return modelAndView;
  }

  /**
   * Create a booking.
   *
   * @param metaData Authentication metadata
   * @param request  Booking request
   * @param result Binding result
   * @param session Session
   * @return Redirect to bookings page
   */
  @PostMapping
  public ModelAndView createBooking(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid BookingFormRequest request, BindingResult result, HttpSession session) {
    ModelAndView modelAndView = new ModelAndView();

    if (result.hasErrors()) {
      modelAndView.setViewName("/public/booking");
      modelAndView.addObject("cart", shoppingCart);
      modelAndView.addObject("vehicles", vehicleService.getVehiclesByUser(userService.getById(metaData.getUserId())));

      return modelAndView;
    }

    List<UUID> services = shoppingCart.getItems().stream()
        .map(CarService::getId)
        .toList();

    if (services.isEmpty()) {
      modelAndView.setViewName("/public/booking");
      modelAndView.addObject("error", "Your cart is empty.");
      return modelAndView;
    }

    if ("CASH".equals(request.getPaymentMethod())) {

      BigDecimal totalPrice = shoppingCart.getTotal();

      bookingService.createBooking(
          metaData.getUserId(),
          request.getBookingDate(),
          services,
          request.getVehicleId(),
          request.getAdditionalNotes(),
          request.getPaymentMethod(),
          request.getPhoneNumber(),
          totalPrice,
          "PENDING"
      );

      shoppingCart.clear();
      return new ModelAndView("redirect:/users/my-bookings");

    } else {

      PendingBookingSessionRequest pending = new PendingBookingSessionRequest(metaData.getUserId(), request, services);

      session.setAttribute("PENDING_BOOKING", pending);

      return new ModelAndView("redirect:/checkout");
    }
  }
}