package springboot.bg.harisauto.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for vehicle-related exceptions.
 *
 * @author Kristian Popov
 */
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
}