package springboot.bg.harisauto.web.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.web.dto.RegisterNewUserRequest;
import springboot.bg.harisauto.web.dto.UpdateUserRequest;

/**
 * AdminController.java - Controller for handling admin-related web requests.
 *
 * @author Kristian Popov
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserService userService;

  @Autowired
  public AdminController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Shows the admin dashboard.
   *
   * @param metaData The authentication metadata.
   * @return The admin dashboard.
   */
  @GetMapping("/dashboard")
  public ModelAndView showDashboard(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-dashboard");
    modelAndView.addObject("user", metaData.getUserId());
    modelAndView.addObject("allUsers", userService.getAllUsers());

    return modelAndView;
  }

  /**
   * Shows the admin users page.
   *
   * @param metaData The authentication metadata.
   * @return The admin users page.
   */
  @GetMapping("/users")
  public ModelAndView showUsers(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-users");
    modelAndView.addObject("user", metaData.getUserId());
    modelAndView.addObject("allUsers", userService.getAllUsers());
    modelAndView.addObject("registerNewUserRequest", new RegisterNewUserRequest());
    modelAndView.addObject("updateUserRequest", new UpdateUserRequest());

    return modelAndView;
  }

  /**
   * Shows the admin services page.
   *
   * @param metaData The authentication metadata.
   * @return The admin services page.
   */
  @GetMapping("/services")
  public ModelAndView showServicePage(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-services");
    modelAndView.addObject("user", metaData.getUserId());

    return modelAndView;
  }

  /**
   * Shows the appointment page.
   *
   * @param metaData The authentication metadata.
   * @return The appointment page.
   */
  @GetMapping("/bookings")
  public ModelAndView showBookingPage(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-bookings");
    modelAndView.addObject("user", metaData.getUserId());

    return modelAndView;
  }

  /**
   * Shows the repair page.
   *
   * @param metaData The authentication metadata.
   * @return The repair page.
   */
  @GetMapping("/repairs")
  public ModelAndView showRepairsPage(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-repairs");
    modelAndView.addObject("user", metaData.getUserId());

    return modelAndView;
  }

  /**
   * Shows the admin reports page.
   *
   * @param metadata The authentication metadata.
   * @return The admin reports page.
   */
  @GetMapping("/reports")
  public ModelAndView showReportsPage(@AuthenticationPrincipal AuthenticationMetaData metadata) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-reports");
    modelAndView.addObject("user", metadata.getUserId());

    return modelAndView;
  }

  /**
   * Shows the admin settings page.
   *
   * @param metaData The authentication metadata.
   * @return The admin settings page.
   */
  @GetMapping("/settings")
  public ModelAndView showSettingsPage(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-settings");
    modelAndView.addObject("user", metaData.getUserId());

    return modelAndView;
  }

  /**
   * Creates a new user account.
   *
   * @param metaData The authentication metadata.
   * @param request The registration request.
   * @param result The binding result.
   * @return The updated users page.
   */
  @PostMapping("/new-user")
  public ModelAndView createNewUser(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid RegisterNewUserRequest request, BindingResult result) {

    ModelAndView modelAndView = new ModelAndView();

    if (result.hasErrors()) {
      modelAndView.setViewName("account/admin/admin-users");
      modelAndView.addObject("user", metaData.getUserId());
      modelAndView.addObject("registerNewUserRequest", request);
      modelAndView.addObject("allUsers", userService.getAllUsers());

      return modelAndView;
    }

    userService.registerNewUser(request);

    return new ModelAndView("redirect:/admin/users");
  }

  /**
   * Updates an existing user account from the admin dashboard.
   *
   * @param metaData The authentication metadata.
   * @param request The update request.
   * @param result The binding result.
   * @return The updated users page.
   */
  @PutMapping("/update-user")
  public ModelAndView updateUser(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid UpdateUserRequest request, BindingResult result) {

    ModelAndView modelAndView = new ModelAndView();

    if (result.hasErrors()) {
      modelAndView.setViewName("account/admin/admin-users");
      modelAndView.addObject("user", metaData.getUserId());
      modelAndView.addObject("updateUserRequest", request);
      modelAndView.addObject("allUsers", userService.getAllUsers());

      return modelAndView;
    }

    userService.updateUser(request);

    return new ModelAndView("redirect:/admin/users");
  }

  /**
   * Delete user by id.
   *
   * @param userId The user id.
   */
  @DeleteMapping("/delete-user/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteUser(@PathVariable("userId") UUID userId) {
    userService.deleteUserById(userId);
  }
}