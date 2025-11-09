package springboot.bg.harisauto.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserRegisteredEvent.java - Event class for user registration event.
 *
 * @author Kristian Popov
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {

  private UUID userId;
  private String firstName;
  private String lastName;
  private String email;
}