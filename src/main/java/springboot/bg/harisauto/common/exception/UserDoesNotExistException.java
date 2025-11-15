package springboot.bg.harisauto.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserDoesNotExistException.java - Custom exception thrown when a user does not exist.
 *
 * @author Kristian Popov
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserDoesNotExistException extends RuntimeException {
  public UserDoesNotExistException(String message) {
    super(message);
  }
}