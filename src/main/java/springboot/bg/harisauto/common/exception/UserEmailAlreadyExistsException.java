package springboot.bg.harisauto.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserEmailAlreadyExistsException.java - Custom exception thrown when a user email already exists.
 *
 * @author Kristian Popov
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailAlreadyExistsException extends RuntimeException {
  public UserEmailAlreadyExistsException(String message) {
    super(message);
  }
}
