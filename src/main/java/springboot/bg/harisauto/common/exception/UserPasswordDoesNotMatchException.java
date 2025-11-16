package springboot.bg.harisauto.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserPasswordDoesNotMatchException.java - Exception thrown when a user's password does not match.
 *
 * @author Kristian Popov
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserPasswordDoesNotMatchException extends RuntimeException {
  public UserPasswordDoesNotMatchException(String message) {
    super(message);
  }
}
