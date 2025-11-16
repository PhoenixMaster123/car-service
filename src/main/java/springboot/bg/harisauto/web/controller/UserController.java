package springboot.bg.harisauto.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.web.dto.ChangeProfileInfoRequest;
import springboot.bg.harisauto.web.dto.ChangeUserPasswordRequest;
import springboot.bg.harisauto.web.mapper.DtoMapper;

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

  /**
   * Shows the settings page for the authenticated user.
   *
   * @param metaData The authentication metadata.
   * @return The settings page.
   */
  @GetMapping("/settings")
  public ModelAndView showSettings(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());
    ChangeProfileInfoRequest changeProfileInfoRequest = DtoMapper.fromUser(user);

    ModelAndView modelAndView = new ModelAndView("account/settings");
    modelAndView.addObject("user", user);
    modelAndView.addObject("changeProfileInfoRequest", changeProfileInfoRequest);
    modelAndView.addObject("changePasswordRequest", new ChangeUserPasswordRequest());

    return modelAndView;
  }

  /**
   * Updates the user's profile.
   *
   * @param metaData The authentication metadata.
   * @param request The request containing the new profile information.
   * @param bindingResult The binding result.
   * @return The updated settings page.
   */
  @PutMapping("/profile")
  public ModelAndView updateProfile(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid ChangeProfileInfoRequest request, BindingResult bindingResult) {

    User user = userService.getById(metaData.getUserId());

    if (bindingResult.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("account/settings");
      modelAndView.addObject("user", user);
      modelAndView.addObject("changeProfileInfoRequest", request);

      return modelAndView;
    }

    userService.updateUserDetails(user, request);

    return new ModelAndView("redirect:/users/dashboard");
  }

  /**
   * Changes the user's password.
   *
   * @param metaData The authentication metadata.
   * @param request The request containing the new password.
   * @param result The binding result.
   * @param redirectAttributes The redirect attributes.
   * @return The updated settings page.
   */
  @PutMapping("/password")
  public ModelAndView changePassword(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid ChangeUserPasswordRequest request, BindingResult result,
      RedirectAttributes redirectAttributes) {

    User user = userService.getById(metaData.getUserId());

    if (result.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("account/settings");
      modelAndView.addObject("user", user);
      modelAndView.addObject("changePasswordRequest", request);
      modelAndView.addObject("changeProfileInfoRequest", DtoMapper.fromUser(user));

      modelAndView.addObject("errorMessage", "Password update failed.");

      return modelAndView;
    }

    userService.changeUserPassword(user, request);

    redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
    return new ModelAndView("redirect:/users/settings#status-message");

  }
}