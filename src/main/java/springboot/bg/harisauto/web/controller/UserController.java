package springboot.bg.harisauto.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;

/**
 * UserController.java - Controller for handling user-related web requests.
 *
 * @author Kristian Popov
 */
@Controller
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Shows the dashboard page for the authenticated user.
   *
   * @param metaData The authentication metadata.
   * @return The dashboard page.
   */
  @GetMapping("/dashboard")
  public ModelAndView showDashboard(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/account/dashboard");
    modelAndView.addObject("user", user);

    return modelAndView;
  }

  /**
   * Shows the vehicle page.
   *
   * @return The vehicle page.
   */
  @GetMapping("/vehicles")
  public ModelAndView showVehiclePage(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/account/vehicles");
    modelAndView.addObject("user", user);

    return modelAndView;
  }

  /**
   * Shows the bookings page for the authenticated user.
   *
   * @param metaData The authentication metadata.
   * @return The bookings page.
   */
  @GetMapping("/bookings")
  public ModelAndView showBookings(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/account/bookings");
    modelAndView.addObject("user", user);

    return modelAndView;
  }

  /**
   * Shows the settings page for the authenticated user.
   *
   * @param metaData The authentication metadata.
   * @return The settings page.
   */
  @GetMapping("/settings")
  public ModelAndView showSettings(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());

    ModelAndView modelAndView = new ModelAndView("account/settings");
    modelAndView.addObject("user", user);

    return modelAndView;
  }

  /**
   * Shows the invoices page for the authenticated user.
   *
   * @param metaData The authentication metadata.
   * @return The invoices page.
   */
  @GetMapping("/invoices")
  public ModelAndView showInvoices(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());

    ModelAndView modelAndView = new ModelAndView("account/invoices");
    modelAndView.addObject("user", user);

    return modelAndView;
  }
}