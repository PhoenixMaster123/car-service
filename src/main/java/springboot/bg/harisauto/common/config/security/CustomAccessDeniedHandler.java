package springboot.bg.harisauto.common.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * CustomAccessDeniedHandler.java - Handles access denied exceptions by redirecting users.
 *
 * @author Kristian Popov
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  /**
   * Redirects users to the home page if they are not authorized to access a resource.
   *
   * @param request that resulted in an <code>AccessDeniedException</code>
   * @param response so that the user agent can be advised of the failure
   * @param exception that caused the invocation
   * @throws IOException IOException
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException exception) throws IOException {

    String referer = request.getHeader("Referer");

    if (referer != null && !referer.isBlank()) {
      response.sendRedirect(referer);
    } else {
      response.sendRedirect("/home");
    }
  }
}