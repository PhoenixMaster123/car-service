package springboot.bg.harisauto.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.Collections;
import java.util.UUID;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.service.VehicleService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private VehicleService vehicleService;

  @MockitoBean
  private BookingService bookingService;

  @Autowired
  private MockMvc mockMvc;

  private User mockUser;
  private AuthenticationMetaData principal;

  @BeforeEach
  public void setUp() {

    UUID userId = UUID.randomUUID();

    this.principal = new AuthenticationMetaData(
      userId,
      "user@gmail.com",
      "Qwer4321!",
      UserRole.USER,
      true
    );

    this.mockUser = new User();
    this.mockUser.setId(userId);
    this.mockUser.setFirstName("TestFirstName");
    this.mockUser.setEmail("user@gmail.com");

    when(this.userService.getById(userId)).thenReturn(mockUser);
    when(this.vehicleService.getVehiclesByUser(mockUser)).thenReturn(Collections.emptyList());
  }

  @Test
  @DisplayName("GET /users/dashboard - Authenticated User - Should Return OK")
  void getRequestToDashboard_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/users/dashboard")
        .with(user(principal))
        .with(csrf());

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("account/dashboard"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser));
  }

  @Test
  @DisplayName("GET /users/vehicles - Authenticated User - Should Return OK")
  void getRequestToVehiclePage_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/users/vehicles")
        .with(user(principal))
        .with(csrf());

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("account/vehicles"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser));
  }

  @Test
  @DisplayName("GET /users/my-bookings - Authenticated User - Should Return OK")
  void getRequestToMyBookingsPage_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/users/my-bookings")
      .with(user(principal))
      .with(csrf());

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("account/bookings"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser));
  }

  @Test
  @DisplayName("GET /users/settings - Authenticated User - Should Return OK")
  void getRequestSettingsPage_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/users/settings")
        .with(user(principal))
        .with(csrf());

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("account/settings"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser));
  }

  @Test
  @DisplayName("POST /users/new-vehicle - Authenticated User - Should Return Redirect")
  void postRequestCreateVehicle_ShouldReturnRedirect() throws Exception {

    MockHttpServletRequestBuilder request = post("/users/new-vehicle")
        .with(user(principal))
        .with(csrf())
        .param("make", "Toyota")
        .param("model", "Corolla")
        .param("manufacturingYear", "2020")
        .param("licensePlate", "ABC123")
        .param("vin", "1HGCM82633A123456")
        .param("color", "Blue");

    this.mockMvc.perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/vehicles"));
  }

  @Test
  @DisplayName("POST /users/new-vehicle - Authenticated User - With Validation Errors - Should Return Vehicle Page")
  void postRequestCreateVehicle_WithValidationErrors_ShouldReturnVehiclePage() throws Exception {

    MockHttpServletRequestBuilder request = post("/users/new-vehicle")
        .with(user(principal))
        .with(csrf())
        .param("make", "")
        .param("model", "Corolla")
        .param("manufacturingYear", "2020")
        .param("licensePlate", "ABC123")
        .param("vin", "1HGCM82633A123456")
        .param("color", "Blue");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("account/vehicles"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser))
        .andExpect(model().attributeHasFieldErrors("createVehicleRequest", "make"));
  }

  @Test
  @DisplayName("DELETE /users/vehicle - Valid ID - Should Redirect")
  void deleteVehicle_ShouldReturnRedirect() throws Exception {
    UUID vehicleId = UUID.randomUUID();

    MockHttpServletRequestBuilder request = delete("/users/vehicle")
        .with(user(principal))
        .with(csrf())
        .param("id", vehicleId.toString());

    this.mockMvc.perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/vehicles"));
  }

  @Test
  @DisplayName("PUT /users/profile - Valid Data - Should Redirect to Dashboard")
  void updateProfile_ValidData_ShouldRedirect() throws Exception {
    MockHttpServletRequestBuilder request = put("/users/profile")
        .with(user(principal))
        .with(csrf())
        .param("firstName", "NewName")
        .param("lastName", "NewLast")
        .param("email", "newemail@gmail.com")
        .param("phoneNumber", "0888123456")
        .param("country", "UNITED_STATES");

    this.mockMvc.perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/dashboard"));
  }

  @Test
  @DisplayName("PUT /users/profile - Invalid Data - Should Return Settings View with Errors")
  void updateProfile_InvalidData_ShouldReturnSettingsView() throws Exception {

    MockHttpServletRequestBuilder request = put("/users/profile")
        .with(user(principal))
        .with(csrf())
        .param("firstName", "")
        .param("email", "invalid-email-format");

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/settings"))
                .andExpect(model().attributeExists("user", "changeProfileInfoRequest"))
                .andExpect(model().attributeHasFieldErrors("changeProfileInfoRequest", "firstName", "email"));
  }

  @Test
  @DisplayName("PUT /users/password - Valid Data - Should Redirect with Flash Message")
  void changePassword_ValidData_ShouldRedirect() throws Exception {

    MockHttpServletRequestBuilder request = put("/users/password")
        .with(user(principal))
        .with(csrf())
        .param("currentPassword", "OldPass123!")
        .param("newPassword", "NewPass123!")
        .param("confirmNewPassword", "NewPass123!");

    this.mockMvc.perform(request)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/settings#status-message"))
        .andExpect(flash().attributeExists("successMessage"));
  }

  @Test
  @DisplayName("PUT /users/password - Invalid Data - Should Return Settings View with Error")
  void changePassword_InvalidData_ShouldReturnSettingsView() throws Exception {

    MockHttpServletRequestBuilder request = put("/users/password")
        .with(user(principal))
        .with(csrf())
        .param("currentPassword", "")
        .param("newPassword", "short");

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("account/settings"))
        .andExpect(model().attributeExists("user", "changeUserPasswordRequest", "errorMessage"))
        .andExpect(model().attributeHasFieldErrors("changeUserPasswordRequest", "currentPassword", "newPassword"));
  }
}