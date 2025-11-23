package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.bg.harisauto.validation.annotations.FieldsShouldNotMatch;
import springboot.bg.harisauto.validation.annotations.PasswordsMatch;

/**
 * AdminChangePasswordRequest.java - DTO class for changing admin password.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordsMatch(
    first = "newPassword",
    second = "confirmNewPassword",
    message = "The new passwords do not match."
)
@FieldsShouldNotMatch(
    firstField = "currentPassword",
    secondField = "newPassword",
    message = "New password cannot be the same as the current password."
)
public class AdminChangePasswordRequest {

  @NotBlank(message = "Current password is required")
  private String currentPassword;

  @NotBlank(message = "New password is required")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
      message = "Password must be at least 6 characters long "
                + "and include at least one uppercase letter, "
                + "one lowercase letter, one number, and one special character"
  )
  private String newPassword;

  @NotBlank(message = "Confirm password is required")
  private String confirmNewPassword;
}