package springboot.bg.harisauto.common.config.i18n;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * CurrentUriAdvice.java - Exposes the current request path to every view.
 *
 * <p>The language switcher needs to link back to the page the visitor is on, with only the
 * {@code lang} parameter changed. Thymeleaf 3.1 removed the {@code #request} object, so the
 * path has to come from the model instead.</p>
 *
 * @author Kristian Popov
 */
@ControllerAdvice
public class CurrentUriAdvice {

  /**
   * The path of the current request, for building same-page links.
   *
   * @param request The current request.
   * @return The request URI, or "/" when it cannot be determined.
   */
  @ModelAttribute("currentUri")
  public String currentUri(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri == null || uri.isBlank() ? "/" : uri;
  }
}
