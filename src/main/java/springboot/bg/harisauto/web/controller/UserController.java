package springboot.bg.harisauto.web.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.bg.harisauto.booking.dto.response.BookingResponse;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.service.VehicleService;
import springboot.bg.harisauto.web.dto.ChangeProfileInfoRequest;
import springboot.bg.harisauto.web.dto.ChangeUserPasswordRequest;
import springboot.bg.harisauto.web.dto.CreateVehicleRequest;
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
  private final VehicleService vehicleService;
  private final BookingService bookingService;

  @Autowired
  public UserController(UserService userService, VehicleService vehicleService, BookingService bookingService) {
    this.userService = userService;
    this.vehicleService = vehicleService;
    this.bookingService = bookingService;
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
    List<Vehicle> vehicles = vehicleService.getVehiclesByUser(user);

    List<BookingResponse> bookings = bookingService.getBookingsByUser(user.getId());
    Map<UUID, List<BookingResponse>> historyByVehicle = bookings.stream()
        .filter(b -> b.getVehicleId() != null)
        .sorted(Comparator.comparing(BookingResponse::getBookingDate,
            Comparator.nullsLast(Comparator.reverseOrder())))
        .collect(Collectors.groupingBy(BookingResponse::getVehicleId));

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/dashboard");
    modelAndView.addObject("user", user);
    modelAndView.addObject("vehicles", vehicles);
    modelAndView.addObject("historyByVehicle", historyByVehicle);

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
    modelAndView.setViewName("account/vehicles");
    modelAndView.addObject("user", user);
    modelAndView.addObject("createVehicleRequest", new CreateVehicleRequest());
    modelAndView.addObject("vehicles", vehicleService.getVehiclesByUser(user));

    return modelAndView;
  }

  /**
   * Shows the bookings page for the authenticated user.
   *
   * @param metaData The authentication metadata.
   * @return The bookings page.
   */
  @GetMapping("/my-bookings")
  public ModelAndView showBookings(@AuthenticationPrincipal AuthenticationMetaData metaData) {

    User user = userService.getById(metaData.getUserId());
    List<BookingResponse> allBookings = bookingService.getBookingsByUser(user.getId());
    LocalDateTime now = LocalDateTime.now();

    List<BookingResponse> upcoming = allBookings.stream()
        .filter(b -> b.getBookingDate().isAfter(now))
        .sorted(Comparator.comparing(BookingResponse::getBookingDate))
        .toList();

    List<BookingResponse> past = allBookings.stream()
        .filter(b -> b.getBookingDate().isBefore(now))
        .sorted((b1, b2) -> b2.getBookingDate().compareTo(b1.getBookingDate()))
        .toList();

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("account/bookings");
    modelAndView.addObject("user", user);

    modelAndView.addObject("upcomingBookings", upcoming);
    modelAndView.addObject("pastBookings", past);

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
   * Creates a new vehicle for the authenticated user.
   *
   * @param metaData user authentication metadata
   * @param request vehicle creation request
   * @param bindingResult binding result
   * @return redirect to vehicles page
   */
  @PostMapping("/new-vehicle")
  public ModelAndView createVehicle(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid CreateVehicleRequest request, BindingResult bindingResult) {

    User user = userService.getById(metaData.getUserId());

    if (bindingResult.hasErrors()) {

      ModelAndView modelAndView = new ModelAndView("account/vehicles");
      modelAndView.addObject("user", user);
      modelAndView.addObject("vehicles", vehicleService.getVehiclesByUser(user));
      return modelAndView;
    }

    vehicleService.createVehicle(user, request);

    return new ModelAndView("redirect:/users/vehicles");
  }

  /**
   * Deletes a vehicle by id.
   *
   * @param id vehicle id
   * @param metaData user authentication metadata
   * @return redirect to vehicles page
   */
  @DeleteMapping("/vehicle")
  public String deleteVehicle(@RequestParam("id") UUID id,
      @AuthenticationPrincipal AuthenticationMetaData metaData) {
    User user = userService.getById(metaData.getUserId());

    vehicleService.deleteVehicle(user, id);

    return "redirect:/users/vehicles";
  }

  /**
   * Updates the user's profile.
   *
   * @param metaData The authentication metadata.
   * @param changeProfileInfoRequest The request containing the new profile information.
   * @param bindingResult The binding result.
   * @return The updated settings page.
   */
  @PutMapping("/profile")
  public ModelAndView updateProfile(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid ChangeProfileInfoRequest changeProfileInfoRequest, BindingResult bindingResult) {

    User user = userService.getById(metaData.getUserId());

    if (bindingResult.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("account/settings");
      modelAndView.addObject("user", user);
      modelAndView.addObject("changeProfileInfoRequest", changeProfileInfoRequest);
      modelAndView.addObject("changePasswordRequest", new ChangeUserPasswordRequest());

      return modelAndView;
    }

    userService.updateUserDetails(user, changeProfileInfoRequest);

    return new ModelAndView("redirect:/users/dashboard");
  }

  /**
   * Changes the user's password.
   *
   * @param metaData The authentication metadata.
   * @param changeUserPasswordRequest The request containing the new password.
   * @param result The binding result.
   * @param redirectAttributes The redirect attributes.
   * @return The updated settings page.
   */
  @PutMapping("/password")
  public ModelAndView changePassword(@AuthenticationPrincipal AuthenticationMetaData metaData,
      @Valid ChangeUserPasswordRequest changeUserPasswordRequest, BindingResult result,
      RedirectAttributes redirectAttributes) {

    User user = userService.getById(metaData.getUserId());

    if (result.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("account/settings");
      modelAndView.addObject("user", user);
      modelAndView.addObject("changePasswordRequest", changeUserPasswordRequest);
      modelAndView.addObject("changeProfileInfoRequest", DtoMapper.fromUser(user));

      modelAndView.addObject("errorMessage", "Password update failed.");

      return modelAndView;
    }

    userService.changeUserPassword(user, changeUserPasswordRequest);

    redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
    return new ModelAndView("redirect:/users/settings#status-message");

  }
}