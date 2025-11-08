package springboot.bg.harisauto.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UserRole.java - Enum for defining user roles.
 *
 * @author Kristian Popov
 */
@Getter
@AllArgsConstructor
public enum UserRole {

  USER("User"),
  ADMIN("Admin");

  private final String name;
}
