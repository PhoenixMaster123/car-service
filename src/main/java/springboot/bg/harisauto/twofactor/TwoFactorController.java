package springboot.bg.harisauto.twofactor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;

/**
 * TwoFactorController.java - The second step of a password sign-in.
 *
 * <p>Reached only when {@link TwoFactorAuthenticationSuccessHandler} has parked a pending
 * user on the session. The session gains no authority until a correct code is submitted
 * here.</p>
 *
 * @author Kristian Popov
 */
@Slf4j
@Controller
public class TwoFactorController {

  private final TwoFactorService twoFactorService;
  private final UserService userService;
  private final SecurityContextRepository securityContextRepository =
      new HttpSessionSecurityContextRepository();

  /** Constructor. */
  public TwoFactorController(TwoFactorService twoFactorService, UserService userService) {
    this.twoFactorService = twoFactorService;
    this.userService = userService;
  }

  /**
   * Shows the code entry form.
   *
   * @param session The current session.
   * @return The verification page, or the login page if no sign-in is pending.
   */
  @GetMapping("/login/verify")
  public ModelAndView showVerifyPage(HttpSession session) {

    if (session.getAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_USER_ID) == null) {
      return new ModelAndView("redirect:/login");
    }

    return new ModelAndView("auth/verify-2fa");
  }

  /**
   * Checks the submitted code and, if it is correct, signs the user in.
   *
   * @param code The six-digit code from the email.
   * @param session The current session.
   * @param request The current request.
   * @param response The current response.
   * @return Redirect to the home page on success, back to the form otherwise.
   */
  @PostMapping("/login/verify")
  public ModelAndView verify(@RequestParam("code") String code, HttpSession session,
      HttpServletRequest request, HttpServletResponse response) {

    Object pending = session.getAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_USER_ID);
    if (!(pending instanceof UUID userId)) {
      return new ModelAndView("redirect:/login");
    }

    TwoFactorService.VerificationResult result = twoFactorService.verify(userId, code);

    if (result != TwoFactorService.VerificationResult.SUCCESS) {
      // A discarded code means sign-in has to start again, so the pending state goes with it.
      if (result == TwoFactorService.VerificationResult.TOO_MANY_ATTEMPTS
          || result == TwoFactorService.VerificationResult.EXPIRED
          || result == TwoFactorService.VerificationResult.NO_CODE) {
        session.removeAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_USER_ID);
        return new ModelAndView("redirect:/login?error=2fa");
      }

      ModelAndView modelAndView = new ModelAndView("auth/verify-2fa");
      modelAndView.addObject("errorMessage", "That code was not correct. Please try again.");
      return modelAndView;
    }

    establishAuthentication(userId, session, request, response);

    return new ModelAndView("redirect:/home");
  }

  /**
   * Signs the user in for real, now that the code has been accepted.
   *
   * @param userId The verified user.
   * @param session The current session.
   * @param request The current request.
   * @param response The current response.
   */
  private void establishAuthentication(UUID userId, HttpSession session,
      HttpServletRequest request, HttpServletResponse response) {

    User user = userService.getById(userId);
    AuthenticationMetaData principal = new AuthenticationMetaData(
        user.getId(), user.getEmail(), user.getPassword(), user.getRole(), true);

    Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
        principal, null, principal.getAuthorities());

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    securityContextRepository.saveContext(context, request, response);

    session.removeAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_USER_ID);

    log.info("Two-factor sign-in completed for user {}", userId);
  }
}
