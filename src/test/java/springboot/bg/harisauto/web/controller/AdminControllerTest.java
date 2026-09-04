package springboot.bg.harisauto.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.booking.dto.response.BookingResponse;
import springboot.bg.harisauto.booking.service.BookingService;
import springboot.bg.harisauto.service.service.CatalogService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.web.dto.*;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CatalogService catalogService;

    @MockitoBean
    private BookingService bookingService;

    private AuthenticationMetaData principal;
    private UUID adminId;

    @BeforeEach
    public void setUp() {

        this.adminId = UUID.randomUUID();

        this.principal = new AuthenticationMetaData(
                adminId,
                "user@gmail.com",
                "Qwer4321!",
                UserRole.ADMIN,
                true
        );

        User mockUser = User.builder()
                .id(adminId)
                .firstName("TestFirstName")
                .lastName("TestLastName")
                .email("user@gmail.com")
                .build();

        // default stubs used by many tests
        when(this.userService.getById(adminId)).thenReturn(mockUser);
        when(this.userService.getAllUsers()).thenReturn(Collections.emptyList());
        when(this.catalogService.findAll()).thenReturn(Collections.emptyList());
        when(this.catalogService.getAllCategories()).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("GET /admin/dashboard - Should return dashboard view")
    void getDashboardPage_ShouldReturnView() throws Exception {

        MockHttpServletRequestBuilder request = get("/admin/dashboard")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-dashboard"))
                .andExpect(model().attributeExists("allUsers"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("GET /admin/users - Should return users view")
    void getUsersPage_ShouldReturnView() throws Exception {

        MockHttpServletRequestBuilder request = get("/admin/users")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-users"))
                .andExpect(model().attributeExists("allUsers"))
                .andExpect(model().attributeExists("registerNewUserRequest"))
                .andExpect(model().attributeExists("updateUserRequest"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("GET /admin/services - Should return services view")
    void getServicesPage_ShouldReturnView() throws Exception {

        MockHttpServletRequestBuilder request = get("/admin/services")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-services"))
                .andExpect(model().attributeExists("allServices"))
                .andExpect(model().attributeExists("allCategories"))
                .andExpect(model().attributeExists("createServiceRequest"))
                .andExpect(model().attributeExists("updateServiceRequest"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("GET /admin/bookings - Should return bookings view")
    void getBookingsPage_ShouldReturnView() throws Exception {

        MockHttpServletRequestBuilder request = get("/admin/bookings")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-bookings"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("GET /admin/repairs - Should return repairs view")
    void getRepairsPage_ShouldReturnView() throws Exception {

        MockHttpServletRequestBuilder request = get("/admin/repairs")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-repairs"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("GET /admin/reports - Should return reports view")
    void getReportsPage_ShouldReturnView() throws Exception {

        MockHttpServletRequestBuilder request = get("/admin/reports")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-reports"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("GET /admin/settings - Should return settings view")
    void getSettingsPage_ShouldReturnView() throws Exception {

        User mockUser = User.builder()
                .id(adminId)
                .firstName("TestFirstName")
                .lastName("TestLastName")
                .email("user@gmail.com")
                .build();

        when(this.userService.getById(adminId)).thenReturn(mockUser);

        MockHttpServletRequestBuilder request = get("/admin/settings")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-settings"))
                .andExpect(model().attributeExists("adminChangeEmailRequest"))
                .andExpect(model().attributeExists("adminChangePasswordRequest"))
                .andExpect(model().attribute("user", mockUser));
    }

    @Test
    @DisplayName("DELETE /admin/delete-user/{id} deletes user and redirects")
    void deleteUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/admin/delete-user/{userId}", userId)
                        .with(csrf())
                        .with(user(principal))) // Fixed: Use correct principal
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).deleteUserById(userId);
    }

    // --- Settings Tests ---

    @Test
    @DisplayName("PUT /admin/settings/email updates email")
    void changeAdminEmail_Success() throws Exception {
        // Ensure getById returns the user for the current principal
        User mockAdmin = User.builder().id(adminId).build();
        when(userService.getById(adminId)).thenReturn(mockAdmin);

        mockMvc.perform(put("/admin/settings/email")
                        .with(csrf())
                        .with(user(principal)) // Fixed: Use correct principal
                        .param("newEmail", "admin@newdomain.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/settings"));

        verify(userService).changeAdminEmail(any(User.class), any(AdminChangeEmailRequest.class));
    }

    @Test
    @DisplayName("PUT /admin/settings/password updates password")
    void changeAdminPassword_Success() throws Exception {
        User mockAdmin = User.builder().id(adminId).build();
        when(userService.getById(adminId)).thenReturn(mockAdmin);

        mockMvc.perform(put("/admin/settings/password")
                        .with(csrf())
                        .with(user(principal))
                        .param("currentPassword", "Qwer4321!")
                        .param("newPassword", "NewPass123!")
                        .param("confirmNewPassword", "NewPass123!")
                 )
                 .andExpect(status().is3xxRedirection())
                 .andExpect(redirectedUrl("/admin/settings"));

        verify(userService).changeAdminPassword(any(User.class), any(AdminChangePasswordRequest.class));
    }

    @Test
    @DisplayName("DELETE /admin/delete-service/{id} deletes service and redirects")
    void deleteService_Success() throws Exception {
        UUID serviceId = UUID.randomUUID();

        mockMvc.perform(delete("/admin/delete-service/{serviceId}", serviceId)
                        .with(csrf())
                        .with(user(principal))) // Fixed: Use correct principal
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));

        verify(catalogService).deleteService(serviceId);
    }

    // Additional tests for validation and success paths

    @Test
    @DisplayName("POST /admin/new-user with validation errors returns users view")
    void createNewUser_WithValidationErrors_ShouldReturnView() throws Exception {
        mockMvc.perform(post("/admin/new-user")
                        .with(csrf())
                        .with(user(principal))
                        // missing required fields to trigger validation errors
                        .param("firstName", "")
                        .param("lastName", "L")
                        .param("email", "invalid-email")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-users"))
                .andExpect(model().attributeExists("registerNewUserRequest"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("POST /admin/new-user success redirects to users")
    void createNewUser_Success_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/admin/new-user")
                        .with(csrf())
                        .with(user(principal))
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Pass1234")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).registerNewUser(any(RegisterNewUserRequest.class));
    }

    @Test
    @DisplayName("PUT /admin/update-user with validation errors returns users view")
    void updateUser_WithValidationErrors_ShouldReturnView() throws Exception {
        mockMvc.perform(put("/admin/update-user")
                        .with(csrf())
                        .with(user(principal))
                        .param("firstName", "A") // too short
                        .param("lastName", "B")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-users"))
                .andExpect(model().attributeExists("updateUserRequest"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("PUT /admin/update-user success redirects to users")
    void updateUser_Success_ShouldRedirect() throws Exception {
        mockMvc.perform(put("/admin/update-user")
                        .with(csrf())
                        .with(user(principal))
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("email", "jane.doe@example.com")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).updateUser(any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("PUT /admin/settings/email with validation errors returns settings view")
    void changeAdminEmail_WithValidationErrors_ShouldReturnView() throws Exception {
        mockMvc.perform(put("/admin/settings/email")
                        .with(csrf())
                        .with(user(principal))
                        .param("newEmail", "")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-settings"))
                .andExpect(model().attributeExists("adminChangeEmailRequest"))
                .andExpect(model().attribute("user", userService.getById(adminId)));
    }

    @Test
    @DisplayName("PUT /admin/settings/password with validation errors returns settings view")
    void changeAdminPassword_WithValidationErrors_ShouldReturnView() throws Exception {
        mockMvc.perform(put("/admin/settings/password")
                        .with(csrf())
                        .with(user(principal))
                        .param("currentPassword", "")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-settings"))
                .andExpect(model().attributeExists("adminChangePasswordRequest"))
                .andExpect(model().attribute("user", userService.getById(adminId)));
    }

    @Test
    @DisplayName("POST /admin/new-service with validation errors returns services view")
    void createService_WithValidationErrors_ShouldReturnView() throws Exception {
        mockMvc.perform(post("/admin/new-service")
                        .with(csrf())
                        .with(user(principal))
                        .param("name", "A") // too short
                        .param("description", "short")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-services"))
                .andExpect(model().attributeExists("createServiceRequest"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("POST /admin/new-service success redirects to services")
    void createService_Success_ShouldRedirect() throws Exception {
        UUID categoryId = UUID.randomUUID();

        mockMvc.perform(post("/admin/new-service")
                        .with(csrf())
                        .with(user(principal))
                        .param("name", "Oil Change")
                        .param("description", "Full oil change including filter replacement")
                        .param("basePrice", "29.99")
                        .param("estimatedDurationInMinutes", "30")
                        .param("categoryId", categoryId.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));

        verify(catalogService).createService(any(CreateServiceRequest.class));
    }

    @Test
    @DisplayName("PUT /admin/update-service with validation errors returns services view")
    void updateService_WithValidationErrors_ShouldReturnView() throws Exception {
        mockMvc.perform(put("/admin/update-service")
                        .with(csrf())
                        .with(user(principal))
                        .param("name", "A")
                        .param("description", "short")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-services"))
                .andExpect(model().attributeExists("updateServiceRequest"))
                .andExpect(model().attribute("user", adminId));
    }

    @Test
    @DisplayName("PUT /admin/update-service success redirects to services")
    void updateService_Success_ShouldRedirect() throws Exception {
        UUID serviceId = UUID.randomUUID();

        mockMvc.perform(put("/admin/update-service")
                        .with(csrf())
                        .with(user(principal))
                        .param("id", serviceId.toString())
                        .param("name", "Tire Rotation")
                        .param("description", "Rotate tires to extend tread life and improve wear")
                        .param("basePrice", "19.99")
                        .param("estimatedDurationInMinutes", "20")
                        .param("categoryId", UUID.randomUUID().toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services"));

        verify(catalogService).updateService(any(UpdateServiceRequest.class));
    }

    @Test
    @DisplayName("GET /admin/bookings - renders the bookings table")
    void showBookingPage_rendersBookings() throws Exception {

        BookingResponse booking = BookingResponse.builder()
                .id(java.util.UUID.randomUUID())
                .status("PENDING")
                .bookingDate(java.time.LocalDateTime.now().plusDays(1))
                .vehicleDescription("Audi A4 (AB-123)")
                .serviceNames("Oil change")
                .phoneNumber("+49111")
                .paymentMethod("CARD")
                .totalPrice(new java.math.BigDecimal("40.00"))
                .build();
        when(bookingService.getAllBookings()).thenReturn(java.util.List.of(booking));

        mockMvc.perform(get("/admin/bookings").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/admin-bookings"))
                .andExpect(content().string(containsString("Audi A4 (AB-123)")))
                .andExpect(content().string(containsString("Oil change")))
                .andExpect(content().string(containsString("PENDING")));
    }

    @Test
    @DisplayName("GET /admin/bookings - shows the error when booking-service is unreachable")
    void showBookingPage_whenBookingServiceDown_showsError() throws Exception {

        when(bookingService.getAllBookings())
                .thenThrow(new IllegalStateException("Booking service is unreachable."));

        mockMvc.perform(get("/admin/bookings").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Booking service is unreachable.")));
    }

    @Test
    @DisplayName("POST /admin/bookings/{id}/cancel - cancels and redirects")
    void cancelBooking_redirects() throws Exception {

        java.util.UUID id = java.util.UUID.randomUUID();

        mockMvc.perform(post("/admin/bookings/" + id + "/cancel")
                        .with(user(principal)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bookings"));

        verify(bookingService).cancelBooking(id);
    }
}
