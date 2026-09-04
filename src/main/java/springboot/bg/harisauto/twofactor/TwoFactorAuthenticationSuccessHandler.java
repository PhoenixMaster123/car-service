package springboot.bg.harisauto.twofactor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.service.UserService;

/**
 * TwoFactorAuthenticationSuccessHandler.java - Holds a password sign-in back until an
 * emailed code is confirmed.
 *
 * <p>The password check has already passed by the time this runs. Rather than letting the
 * session become authenticated, the security context is cleared and the user id is parked
 * on the session under {@link #PENDING_USER_ID}. Only {@code TwoFactorController} can turn
 * that into a real authentication, and only against a correct code - so a half-finished
 * sign-in carries no authority anywhere in the application.</p>
 *
 * @author Kristian Popov
 */
@Slf4j
@Component
public class TwoFactorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  /** Session attribute holding the user awaiting code verification. */
  public static final String PENDING_USER_ID = "TWO_FACTOR_PENDING_USER_ID";

  private final TwoFactorService twoFactorService;
  private final TwoFactorProperties properties;
  private final UserService userService;

  /** Constructor. */
  public TwoFactorAuthenticationSuccessHandler(TwoFactorService twoFactorService,
                                               TwoFactorProperties properties,
                                               UserService userService) {
    this.twoFactorService = twoFactorService;
    this.properties = properties;
    this.userService = userService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    if (!properties.isEnabled()
        || !(authentication.getPrincipal() instanceof AuthenticationMetaData metaData)) {
      // Two-factor is off, or this is an OAuth2 sign-in, where the provider has already
      // applied its own verification. Complete normally.
      response.sendRedirect(request.getContextPath() + "/home");
      return;
    }

    User user = userService.getById(metaData.getUserId());

    try {
      twoFactorService.issueCode(user);
    } catch (RuntimeException ex) {
      // If the code cannot be sent, refuse the sign-in rather than waving it through:
      // failing open would silently disable the second factor.
      log.error("Could not issue a two-factor code for user {}", user.getId(), ex);
      SecurityContextHolder.clearContext();
      request.getSession().invalidate();
      response.sendRedirect(request.getContextPath() + "/login?error=2fa");
      return;
    }

    // Drop the authenticated context: the session is not signed in until the code is checked.
    SecurityContextHolder.clearContext();
    request.getSession().removeAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    request.getSession().setAttribute(PENDING_USER_ID, user.getId());

    response.sendRedirect(request.getContextPath() + "/login/verify");
  }
}
