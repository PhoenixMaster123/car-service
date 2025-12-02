package springboot.bg.harisauto.common.config.swagger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * AuthenticationEntryPoint.java - Custom authentication entry point for handling unauthorized access.
 *
 * @author Kristian Popov
 */
@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

  /**
   * Begins an authentication scheme.
   *
   * @param request that resulted in an <code>AuthenticationException</code>
   * @param response so that the user agent can begin authentication
   * @param authEx that caused the invocation
   * @throws IOException if an I/O error occurs during processing
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authEx) throws IOException {

    response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    PrintWriter writer = response.getWriter();
    writer.println("HTTP Status 401 - " + authEx.getMessage());
  }

  /**
   * Sets the realm name.
   */
  @Override
  public void afterPropertiesSet() {
    setRealmName("HarisAuto");
    super.afterPropertiesSet();
  }
}