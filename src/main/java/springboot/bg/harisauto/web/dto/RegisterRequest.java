package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.bg.harisauto.validation.annotations.PasswordsMatch;

/**
 * RegisterRequest.java - DTO class for registering a new user.
 *
 * @author Kristian Popov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordsMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
public class RegisterRequest {

  @NotNull
  @Email(
      message = "Please provide a valid email address",
      regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
  )
  private String email;

  @NotBlank
  @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
  private String firstName;

  @NotBlank
  @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
  private String lastName;

  @NotBlank
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
           message = "Password must be at least 6 characters "
                   + "long and include at least one uppercase letter, "
                   + "one lowercase letter, one number, and one special character")
  private String password;

  @NotBlank
  private String confirmPassword;
}