package springboot.bg.harisauto.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.common.config.security.CustomAccessDeniedHandler;
import springboot.bg.harisauto.common.config.security.CustomOAuth2UserService;
import springboot.bg.harisauto.common.config.security.SecurityConfig;
import springboot.bg.harisauto.twofactor.TwoFactorAuthenticationSuccessHandler;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.web.dto.RegisterRequest;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TwoFactorAuthenticationSuccessHandler twoFactorSuccessHandler;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private CustomOAuth2UserService customOAuth2Service;

  @MockitoBean
  private CustomAccessDeniedHandler customAccessDeniedHandler;

  @Captor
  private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;

  @Test
  @DisplayName("GET / - Should return 200 OK and Index View")
  void showIndexPage_ShouldReturnIndexView() throws Exception {

    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("/public/index"));
  }

  @Test
  @DisplayName("GET /login - Should return Login View and Model Attribute")
  void showLoginForm_ShouldReturnLoginView() throws Exception {

    mockMvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(view().name("/auth/login"))
        .andExpect(model().attributeExists("loginRequest"));
    }

  @Test
  @DisplayName("GET /register - Should return Register View and Model Attribute")
  void showRegistrationPage_ShouldReturnRegisterView() throws Exception {

    mockMvc.perform(get("/register"))
        .andExpect(status().isOk())
        .andExpect(view().name("/auth/register"))
        .andExpect(model().attributeExists("registerRequest"));
  }

  @Test
  @DisplayName("POST /register - Valid Input - Should Register User and Redirect to /login")
  void registerNewUser_ValidInput_ShouldRedirectToLogin() throws Exception {

    mockMvc.perform(post("/register")
        .with(csrf())
        .formField("firstName", "John")
        .formField("lastName", "Doe")
        .formField("email", "user@gmail.com")
        .formField("password", "Qwer4321!")
        .formField("confirmPassword", "Qwer4321!"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPath("/login"));

    verify(userService).register(registerRequestArgumentCaptor.capture());

    RegisterRequest request = registerRequestArgumentCaptor.getValue();
    assertEquals("user@gmail.com", request.getEmail());
    assertEquals("John", request.getFirstName());
    assertEquals("Doe", request.getLastName());
    assertEquals("Qwer4321!", request.getPassword());
    assertEquals("Qwer4321!", request.getConfirmPassword());

    verify(userService, times(1)).register(any(RegisterRequest.class));
  }

  @Test
  @DisplayName("POST /register - Invalid Input - Should Return to Register View with Errors")
  void registerNewUser_InvalidInput_ShouldReturnRegisterView() throws Exception {

    mockMvc.perform(post("/register")
        .with(csrf())
        .formField("firstName", "")
        .formField("lastName", "")
        .formField("email", "")
        .formField("password", "weak")
        .formField("confirmPassword", "weak"))
        .andExpect(status().isOk())
        .andExpect(view().name("/auth/register"))
        .andExpect(model().attributeHasFieldErrors("registerRequest", "email", "firstName", "lastName", "password"));

    verify(userService, never()).register(any());
  }

  @Test
  @DisplayName("GET /home - Authenticated User - Should Return Index View with User Model")
  void showHomePage_Authenticated_ShouldReturnViewWithUser() throws Exception {

    UUID userId = UUID.randomUUID();
    AuthenticationMetaData mockMetaData = mock(AuthenticationMetaData.class);
    when(mockMetaData.getUserId()).thenReturn(userId);

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(mockMetaData, "password", Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    User mockUser = new User();
    mockUser.setId(userId);
    mockUser.setEmail("test@test.com");
    when(userService.getById(userId)).thenReturn(mockUser);

    mockMvc.perform(get("/home").principal(auth))
        .andExpect(status().isOk())
        .andExpect(view().name("/public/index"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attribute("user", mockUser));
  }

  private static ResultMatcher redirectedUrlPath(String expectedPath) {
    return result -> {
       String fullRedirectUrl = result.getResponse().getRedirectedUrl();
       if (fullRedirectUrl == null) {
         throw new AssertionError("Redirect URL was null");
       }
       String actualPath = new URI(fullRedirectUrl).getPath();
       assertEquals(expectedPath, actualPath, "Expected redirect path " + expectedPath + " but got " + actualPath);
    };
  }
}