package springboot.bg.harisauto.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.common.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;
import springboot.bg.harisauto.web.dto.LoginRequest;
import springboot.bg.harisauto.web.dto.RegisterRequest;

/**
 * AuthController.java - Controller for handling authentication-related web requests.
 *
 * @author Kristian Popov
 */
@Controller
public class AuthController {

  private final UserService userService;

  @Autowired
  public AuthController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Shows the index page.
   *
   * @return The index page.
   */
  @GetMapping("/")
  public String showIndexPage() {
    return "/public/index";
  }

  /**
   * Shows the login form.
   *
   * @return The login form.
   */
  @GetMapping("/login")
  public ModelAndView showLoginForm() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/auth/login");
    modelAndView.addObject("loginRequest", new LoginRequest());

    return modelAndView;
  }

  /**
   * Shows the registration form.
   *
   * @return The registration form.
   */
  @GetMapping("/register")
  public ModelAndView showRegistrationPage() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/auth/register");
    modelAndView.addObject("registerRequest", new RegisterRequest());

    return modelAndView;
  }

  /**
   * Registers a new user.
   *
   * @param request Registration request.
   * @param bindingResult Binding result.
   * @return The home page.
   */
  @PostMapping("/register")
  public ModelAndView registerNewUser(@Valid RegisterRequest request, BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return new ModelAndView("/auth/register");
    }

    userService.register(request);

    return new ModelAndView("redirect:/login");
  }

  /**
   * Shows the home page.
   *
   * @param metaData Authentication metadata.
   * @return The home page.
   */
  @GetMapping("/home")
  public ModelAndView showHomePage(@AuthenticationPrincipal AuthenticationMetaData metaData) {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getById(metaData.getUserId());
    modelAndView.addObject("user", user);

    modelAndView.setViewName("/public/index");

    return modelAndView;
  }
}