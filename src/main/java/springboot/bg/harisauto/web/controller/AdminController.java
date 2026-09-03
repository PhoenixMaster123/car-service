package springboot.bg.harisauto.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.service.service.CatalogService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.web.dto.AdminChangeEmailRequest;
import springboot.bg.harisauto.web.dto.AdminChangePasswordRequest;
import springboot.bg.harisauto.web.dto.CreateServiceRequest;
import springboot.bg.harisauto.web.dto.RegisterNewUserRequest;
import springboot.bg.harisauto.web.dto.UpdateServiceRequest;
import springboot.bg.harisauto.web.dto.UpdateUserRequest;

/**
 * AdminController.java - Controller for handling admin-related web requests.
 *
 * @author Kristian Popov
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserService userService;
  private final CatalogService catalogService;
  private final BookingService bookingService;

  /** Constructor. */
  @Autowired
  public AdminController(UserService userService, CatalogService catalogService,
      BookingService bookingService) {
    this.userService = userService;
    this.catalogService = catalogService;
    this.bookingService = bookingService;
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
  public ModelAndView showServicePage(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    ModelAndView modelAndView = new ModelAndView();

    modelAndView.setViewName("account/admin/admin-services");

    modelAndView.addObject("user", metaData.getUserId());
    modelAndView.addObject("allServices", catalogService.findAll());
    modelAndView.addObject("allCategories", catalogService.getAllCategories());
    modelAndView.addObject("createServiceRequest", new CreateServiceRequest());
    modelAndView.addObject("updateServiceRequest", new UpdateServiceRequest());

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

    try {
      modelAndView.addObject("bookings", bookingService.getAllBookings());
    } catch (IllegalStateException ex) {
      // The page is still useful with the error shown, rather than failing outright.
      modelAndView.addObject("bookings", List.of());
      modelAndView.addObject("errorMessage", ex.getMessage());
    }

    return modelAndView;
  }

  /**
   * Cancels a booking.
   *
   * @param id The booking id.
   * @param attributes Flash attributes for the redirect.
   * @return Redirect back to the bookings page.
   */
  @PostMapping("/bookings/{id}/cancel")
  public String cancelBooking(@PathVariable("id") UUID id, RedirectAttributes attributes) {

    try {
      bookingService.cancelBooking(id);
      attributes.addFlashAttribute("successMessage", "Booking cancelled.");
    } catch (IllegalStateException ex) {
      attributes.addFlashAttribute("errorMessage", ex.getMessage());
    }

    return "redirect:/admin/bookings";
  }

  /**
   * Archives a booking.
   *
   * @param id The booking id.
   * @param attributes Flash attributes for the redirect.
   * @return Redirect back to the bookings page.
   */
  @PostMapping("/bookings/{id}/archive")
  public String archiveBooking(@PathVariable("id") UUID id, RedirectAttributes attributes) {

    try {
      bookingService.archiveBooking(id);
      attributes.addFlashAttribute("successMessage", "Booking archived.");
    } catch (IllegalStateException ex) {
      attributes.addFlashAttribute("errorMessage", ex.getMessage());
    }

    return "redirect:/admin/bookings";
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

    User adminUser = userService.getById(metaData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/admin/admin-settings");
    modelAndView.addObject("user", adminUser);
    modelAndView.addObject("adminChangeEmailRequest", new AdminChangeEmailRequest());
    modelAndView.addObject("adminChangePasswordRequest", new AdminChangePasswordRequest());

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
      // template requires updateUserRequest as well
      modelAndView.addObject("updateUserRequest", new UpdateUserRequest());
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
      // template requires registerNewUserRequest as well
      modelAndView.addObject("registerNewUserRequest", new RegisterNewUserRequest());
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
  public ModelAndView deleteUser(@PathVariable("userId") UUID userId) {

    userService.deleteUserById(userId);

    return new ModelAndView("redirect:/admin/users");
  }

  /**
   * Changes the admin's email.
   *
   * @param metaData The authentication metadata.
   * @param request The change email request.
   * @param result The binding result.
   * @return The settings page.
   */
  @PutMapping("/settings/email")
  public ModelAndView changeAdminEmail(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid AdminChangeEmailRequest request, BindingResult result) {

    User adminUser = userService.getById(metaData.getUserId());

    if (result.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("account/admin/admin-settings");
      modelAndView.addObject("user", adminUser);
      modelAndView.addObject("adminChangeEmailRequest", request);
      modelAndView.addObject("adminChangePasswordRequest", new AdminChangePasswordRequest());

      return modelAndView;
    }

    userService.changeAdminEmail(adminUser, request);

    return new ModelAndView("redirect:/admin/settings");
  }

  /**
   * Changes the admin's password.
   *
   * @param metaData The authentication metadata.
   * @param request The change password request.
   * @param result The binding result.
   * @return The settings page.
   */
  @PutMapping("/settings/password")
  public ModelAndView changeAdminPassword(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid AdminChangePasswordRequest request, BindingResult result) {

    User adminUser = userService.getById(metaData.getUserId());

    if (result.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("account/admin/admin-settings");
      modelAndView.addObject("user", adminUser);
      modelAndView.addObject("adminChangeEmailRequest", new AdminChangeEmailRequest());
      modelAndView.addObject("adminChangePasswordRequest", request);

      return modelAndView;
    }

    userService.changeAdminPassword(adminUser, request);

    return new ModelAndView("redirect:/admin/settings");
  }

  /**
   * Creates a new service.
   *
   * @param metaData The authentication metadata.
   * @param request The create service request.
   * @param result The binding result.
   * @return The services page.
   */
  @PostMapping("/new-service")
  public ModelAndView createService(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid CreateServiceRequest request, BindingResult result) {

    ModelAndView modelAndView = new ModelAndView();

    if (result.hasErrors()) {
      modelAndView.setViewName("account/admin/admin-services");
      modelAndView.addObject("user", metaData.getUserId());
      modelAndView.addObject("allServices", catalogService.findAll());
      modelAndView.addObject("allCategories", catalogService.getAllCategories());
      modelAndView.addObject("createServiceRequest", request);
      modelAndView.addObject("updateServiceRequest", new UpdateServiceRequest());

      return modelAndView;
    }

    catalogService.createService(request);

    return new ModelAndView("redirect:/admin/services");

  }

  /**
   * Updates an existing service.
   *
   * @param metaData The authentication metadata.
   * @param request The update service request.
   * @param result The binding result.
   * @return The services page.
   */
  @PutMapping("/update-service")
  public ModelAndView updateService(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid UpdateServiceRequest request, BindingResult result) {

    ModelAndView modelAndView = new ModelAndView();

    if (result.hasErrors()) {
      modelAndView.setViewName("account/admin/admin-services");
      modelAndView.addObject("user", metaData.getUserId());
      modelAndView.addObject("allServices", catalogService.findAll());
      modelAndView.addObject("allCategories", catalogService.getAllCategories());
      modelAndView.addObject("createServiceRequest", new CreateServiceRequest());
      modelAndView.addObject("updateServiceRequest", request);

      return modelAndView;
    }

    catalogService.updateService(request);

    return new ModelAndView("redirect:/admin/services");
  }

  /**
   * Deletes a service by id.
   *
   * @param serviceId The service id.
   */
  @DeleteMapping("/delete-service/{serviceId}")
  public ModelAndView deleteService(@PathVariable("serviceId") UUID serviceId) {

    catalogService.deleteService(serviceId);

    return new ModelAndView("redirect:/admin/services");
  }
}

