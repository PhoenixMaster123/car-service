package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.bg.harisauto.user.model.Country;
import springboot.bg.harisauto.user.model.UserRole;

/**
 * RegisterNewUserRequest.java - DTO class for registering a new user.
 *
 * @author Kristian Popov
 */
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class RegisterNewUserRequest {

  @NotBlank
  @Size(min = 2, max = 30)
  private String firstName;

  @NotBlank
  @Size(min = 2, max = 30)
  private String lastName;

  @NotBlank
  @Email(
      regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
      message = "Please provide a valid email address"
  )
  private String email;

  @Pattern(
      regexp = "^$|(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$",
      message = "Invalid phone number format."
  )
  private String phoneNumber;

  private Country country;

  private UserRole role;

  @NotBlank
  private String password;
}