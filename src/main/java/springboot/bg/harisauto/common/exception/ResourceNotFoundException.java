package springboot.bg.harisauto.common.exception;

/**
 * Thrown when a requested catalogue resource (service or category) does not exist.
 *
 * <p>Replaces the misuse of {@code UserDoesNotExistException} for non-user lookups.</p>
 *
 * @author Kristian Popov
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
