package springboot.bg.harisauto.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for vehicle-related business logic errors.
 *
 * @author Kristian Popov
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VehicleBusinessException extends RuntimeException {
  public VehicleBusinessException(String message) {
    super(message);
  }
}