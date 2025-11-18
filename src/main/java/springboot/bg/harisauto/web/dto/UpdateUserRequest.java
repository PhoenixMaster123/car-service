package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.bg.harisauto.user.model.Country;
import springboot.bg.harisauto.user.model.UserRole;

/**
 * UpdateUserRequest.java - DTO class for updating user information.
 *
 * @author Kristian Popov
 */
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UpdateUserRequest {

  @NotBlank
  @Size(min = 2, max = 30)
  private String firstName;

  @NotBlank
  @Size(min = 2, max = 30)
  private String lastName;

  @Email(
      regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
      message = "Please provide a valid email address"
  )
  private String email;

  private String phoneNumber;

  private Country country;

  private UserRole role;
}