package springboot.bg.harisauto.common.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * CustomErrorController.java - Renders the branded error pages for container-level errors.
 *
 * <p>{@code @ControllerAdvice} only sees exceptions thrown out of a handler. Anything the
 * container raises on its own - an unmapped URL, a request to a static path that does not
 * exist, an error forwarded after the response has begun - lands on {@code /error} instead,
 * which without this controller renders Spring Boot's whitelabel page.</p>
 *
 * @author Kristian Popov
 */
@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

  /**
   * Renders an error page appropriate to the status the container recorded.
   *
   * @param request The failed request.
   * @return The 404 view for missing pages, the generic error view otherwise.
   */
  @RequestMapping("/error")
  public ModelAndView handleError(HttpServletRequest request) {

    Object rawStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    int statusCode = rawStatus == null
        ? HttpStatus.INTERNAL_SERVER_ERROR.value()
        : Integer.parseInt(rawStatus.toString());

    Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

    if (statusCode == HttpStatus.NOT_FOUND.value()) {
      log.info("404 for {}", path);
      return new ModelAndView("error/404-page", "statusCode", statusCode);
    }

    log.warn("Error {} for {}", statusCode, path);

    ModelAndView modelAndView = new ModelAndView("error/error-page");
    modelAndView.setStatus(HttpStatus.valueOf(statusCode));
    modelAndView.addObject("statusCode", statusCode);
    return modelAndView;
  }
}
