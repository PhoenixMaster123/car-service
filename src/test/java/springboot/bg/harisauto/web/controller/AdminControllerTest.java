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
import springboot.bg.harisauto.service.service.CatalogService;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.service.UserService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private CatalogService catalogService;

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

    when(this.userService.getById(adminId)).thenReturn(mockUser);
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

    //    @Test
    //    @DisplayName("GET /admin/users - Should return users management view")
    //    void showUsers_ShouldReturnView() throws Exception {
    //        mockMvc.perform(get("/admin/users").principal(adminAuth))
    //                .andExpect(status().isOk())
    //                .andExpect(view().name("account/admin/admin-users"))
    //                .andExpect(model().attributeExists("registerNewUserRequest", "updateUserRequest"));
    //    }
    //
    //    @Test
    //    @DisplayName("GET /admin/services - Should return services management view")
    //    void showServicePage_ShouldReturnView() throws Exception {
    //        when(catalogService.findAll()).thenReturn(Collections.emptyList());
    //        when(catalogService.getAllCategories()).thenReturn(Collections.emptyList());
    //
    //        mockMvc.perform(get("/admin/services").principal(adminAuth))
    //                .andExpect(status().isOk())
    //                .andExpect(view().name("account/admin/admin-services"))
    //                .andExpect(model().attributeExists("allServices", "allCategories"));
    //    }
    //
    //    @Test
    //    @DisplayName("GET /admin/settings - Should return settings view with current user data")
    //    void showSettingsPage_ShouldReturnView() throws Exception {
    //        User adminUser = new User();
    //        adminUser.setId(adminId);
    //        when(userService.getById(adminId)).thenReturn(adminUser);
    //
    //        mockMvc.perform(get("/admin/settings").principal(adminAuth))
    //                .andExpect(status().isOk())
    //                .andExpect(view().name("account/admin/admin-settings"))
    //                .andExpect(model().attribute("user", adminUser));
    //    }
    //
    //    // ==========================================
    //    // 2. USER MANAGEMENT TESTS
    //    // ==========================================
    //
    //    @Nested
    //    @DisplayName("User Management Operations")
    //    class UserManagementTests {
    //
    //        @Test
    //        @DisplayName("POST /admin/new-user - Valid Input - Should create user and redirect")
    //        void createNewUser_Valid_ShouldRedirect() throws Exception {
    //            mockMvc.perform(post("/admin/new-user")
    //                            .principal(adminAuth)
    //                            .with(csrf())
    //                            .param("firstName", "Alice")
    //                            .param("lastName", "Admin")
    //                            .param("email", "alice@example.com")
    //                            .param("password", "StrongPass1!")
    //                            .param("confirmPassword", "StrongPass1!"))
    //                    .andExpect(status().is3xxRedirection())
    //                    .andExpect(redirectedUrl("/admin/users"));
    //
    //            verify(userService).registerNewUser(any(RegisterNewUserRequest.class));
    //        }
    //
    //        @Test
    //        @DisplayName("POST /admin/new-user - Invalid Input - Should return to view with errors")
    //        void createNewUser_Invalid_ShouldReturnView() throws Exception {
    //            mockMvc.perform(post("/admin/new-user")
    //                            .principal(adminAuth)
    //                            .with(csrf())
    //                            .param("email", "") // Invalid
    //                            .param("password", "123")) // Invalid
    //                    .andExpect(status().isOk())
    //                    .andExpect(view().name("account/admin/admin-users"))
    //                    .andExpect(model().hasErrors());
    //
    //            verify(userService, never()).registerNewUser(any());
    //        }
    //
    //        @Test
    //        @DisplayName("PUT /admin/update-user - Valid Input - Should update and redirect")
    //        void updateUser_Valid_ShouldRedirect() throws Exception {
    //            mockMvc.perform(put("/admin/update-user")
    //                            .principal(adminAuth)
    //                            .with(csrf())
    //                            .param("id", UUID.randomUUID().toString())
    //                            .param("firstName", "Updated")
    //                            .param("lastName", "Name")
    //                            .param("email", "updated@test.com"))
    //                    .andExpect(status().is3xxRedirection())
    //                    .andExpect(redirectedUrl("/admin/users"));
    //
    //            verify(userService).updateUser(any(UpdateUserRequest.class));
    //        }
    //
    //        @Test
    //        @DisplayName("DELETE /admin/delete-user/{id} - Should delete and redirect")
    //        void deleteUser_ShouldRedirect() throws Exception {
    //            UUID targetId = UUID.randomUUID();
    //
    //            mockMvc.perform(delete("/admin/delete-user/{userId}", targetId)
    //                            .principal(adminAuth)
    //                            .with(csrf()))
    //                    .andExpect(status().is3xxRedirection())
    //                    .andExpect(redirectedUrl("/admin/users"));
    //
    //            verify(userService).deleteUserById(targetId);
    //        }
    //    }
    //
    //    // ==========================================
    //    // 3. SERVICE MANAGEMENT TESTS
    //    // ==========================================
    //
    //    @Nested
    //    @DisplayName("Service (Catalog) Operations")
    //    class ServiceManagementTests {
    //
    //        @Test
    //        @DisplayName("POST /admin/new-service - Valid Input - Should create and redirect")
    //        void createService_Valid_ShouldRedirect() throws Exception {
    //            mockMvc.perform(post("/admin/new-service")
    //                            .principal(adminAuth)
    //                            .with(csrf())
    //                            .param("name", "Oil Change")
    //                            .param("price", "50.00")
    //                            .param("description", "Basic oil change")
    //                            .param("categoryId", UUID.randomUUID().toString())) // Assuming DTO fields
    //                    .andExpect(status().is3xxRedirection())
    //                    .andExpect(redirectedUrl("/admin/services"));
    //
    //            verify(catalogService).createService(any(CreateServiceRequest.class));
    //        }
    //
    //        @Test
    //        @DisplayName("POST /admin/new-service - Invalid Input - Should return view with errors")
    //        void createService_Invalid_ShouldReturnView() throws Exception {
    //            mockMvc.perform(post("/admin/new-service")
    //                            .principal(adminAuth)
    //                            .with(csrf())
    //                            .param("name", "")) // Empty name
    //                    .andExpect(status().isOk())
    //                    .andExpect(view().name("account/admin/admin-services"))
    //                    .andExpect(model().attributeHasFieldErrors("createServiceRequest", "name"));
    //
    //            verify(catalogService, never()).createService(any());
    //        }
    //
    //        @Test
    //        @DisplayName("PUT /admin/update-service - Valid Input - Should update and redirect")
    //        void updateService_Valid_ShouldRedirect() throws Exception {
    //            mockMvc.perform(put("/admin/update-service")
    //                            .principal(adminAuth)
    //                            .with(csrf())
    //                            .param("id", UUID.randomUUID().toString())
    //                            .param("name", "New Name")
    //                            .param("price", "100.00"))
    //                    .andExpect(status().is3xxRedirection())
    //                    .andExpect(redirectedUrl("/admin/services"));
    //
    //            verify(catalogService).updateService(any(UpdateServiceRequest.class));
    //        }
    //
    //        @Test
    //        @DisplayName("DELETE /admin/delete-service/{id} - Should delete and redirect")
    //        void deleteService_ShouldRedirect() throws Exception {
    //            UUID serviceId = UUID.randomUUID();
    //
    //            mockMvc.perform(delete("/admin/delete-service/{serviceId}", serviceId)
    //                            .principal(adminAuth)
    //                            .with(csrf()))
    //                    .andExpect(status().is3xxRedirection())
    //                    .andExpect(redirectedUrl("/admin/services"));
    //
    //            verify(catalogService).deleteService(serviceId);
    //        }
    //    }
    //
    //    // ==========================================
    //    // 4. SECURITY & ACCESS CONTROL TESTS
    //    // ==========================================
    //
    //    @Test
    //    @DisplayName("GET /admin/dashboard - Unauthenticated - Should Redirect to Login")
    //    void unauthenticatedAccess_ShouldRedirectToLogin() throws Exception {
    //        mockMvc.perform(get("/admin/dashboard")) // No principal set
    //                .andExpect(status().is3xxRedirection())
    //                .andExpect(redirectedUrlPattern("**/login"));
    //    }
    //
    //    @Test
    //    @DisplayName("GET /admin/dashboard - Authenticated as USER (Not Admin) - Should Return 403 Forbidden")
    //    void nonAdminAccess_ShouldReturnForbidden() throws Exception {
    //        // Create a user with ROLE_USER only
    //        AuthenticationMetaData metaData = mock(AuthenticationMetaData.class);
    //        UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
    //                metaData, "pass", List.of(new SimpleGrantedAuthority("ROLE_USER"))
    //        );
    //
    //        mockMvc.perform(get("/admin/dashboard").principal(userAuth))
    //                .andExpect(status().isForbidden()); // 403
    //    }
}

