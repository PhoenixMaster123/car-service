package springboot.bg.harisauto.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.vehicle.service.VehicleService;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

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
        .andExpect(view().name("/account/dashboard"))
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
        .andExpect(view().name("/account/vehicles"))
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
        .andExpect(view().name("/account/bookings"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser));
  }

  @Test
  @DisplayName("GET /users/invoices - Authenticated User - Should Return OK")
  void getRequestInvoicesPage_ShouldReturnOk() throws Exception {

    MockHttpServletRequestBuilder request = get("/users/invoices")
        .with(user(principal))
        .with(csrf());

    this.mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(view().name("/account/invoices"))
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
        .andExpect(view().name("/account/settings"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser));
  }
}