package springboot.bg.harisauto.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.booking.dto.BookingRequest;
import springboot.bg.harisauto.cart.ShoppingCart;

/**
 * BookingController.java - Controller for handling booking-related web requests.
 *
 * @author Kristian Popov
 */
@Controller
@RequestMapping("/bookings")
public class BookingController {

  private final ShoppingCart shoppingCart;

  public BookingController(ShoppingCart shoppingCart) {
    this.shoppingCart = shoppingCart;
  }

  /**
   * Show booking page.
   *
   * @return The booking page.
   */
  @GetMapping
  public ModelAndView showBookingPage() {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/public/booking");
    modelAndView.addObject("cart", shoppingCart);
    modelAndView.addObject("bookingRequest", new BookingRequest());

    return modelAndView;
  }
}