package springboot.bg.harisauto.common.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for the web layer.
 *
 * <p>Business exceptions are turned into a flash message on the page the user came from,
 * so a wrong password or a duplicate email reads as a form error rather than a whitelabel
 * 500 page.</p>
 *
 * @author Kristian Popov
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles vehicle-related exceptions.
   *
   * @param e - VehicleBusinessException
   * @param attributes - RedirectAttributes
   * @return String
   */
  @ExceptionHandler(VehicleBusinessException.class)
  public String handleVehicleExceptions(VehicleBusinessException e, RedirectAttributes attributes) {

    attributes.addFlashAttribute("errorMessage", e.getMessage());

    return "redirect:/users/vehicles";
  }

  /**
   * Handles a failed password change or a duplicate email on the settings screen.
   *
   * @param e The business exception.
   * @param attributes Flash attributes for the redirect.
   * @return Redirect back to the settings page.
   */
  @ExceptionHandler({UserPasswordDoesNotMatchException.class, UserEmailAlreadyExistsException.class})
  public String handleAccountExceptions(RuntimeException e, RedirectAttributes attributes) {

    log.info("Account update rejected: {}", e.getMessage());
    attributes.addFlashAttribute("errorMessage", e.getMessage());

    return "redirect:/users/settings#status-message";
  }

  /**
   * Handles lookups for users, services and categories that do not exist.
   *
   * @param e The not-found exception.
   * @param request The request that triggered it.
   * @return The 404 view.
   */
  @ExceptionHandler({UserDoesNotExistException.class, ResourceNotFoundException.class,
      EntityNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ModelAndView handleNotFound(RuntimeException e, HttpServletRequest request) {

    log.warn("Not found for {}: {}", request.getRequestURI(), e.getMessage());

    ModelAndView modelAndView = new ModelAndView("error/404-page");
    modelAndView.addObject("errorMessage", e.getMessage());

    return modelAndView;
  }

  /**
   * Handles an attempt to read a resource that belongs to somebody else.
   *
   * @param e The access-denied exception.
   * @param request The request that triggered it.
   * @return The 404 view, so the resource's existence is not disclosed.
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ModelAndView handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {

    log.warn("Access denied for {}: {}", request.getRequestURI(), e.getMessage());

    return new ModelAndView("error/404-page");
  }
}
