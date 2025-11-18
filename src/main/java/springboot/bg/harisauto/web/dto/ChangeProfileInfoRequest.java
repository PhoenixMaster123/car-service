package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.bg.harisauto.user.model.Country;

/**
 * ChangeProfileInfoRequest.java - DTO class for changing user profile information.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeProfileInfoRequest {

  @Size(min = 2, max = 30)
  @NotBlank
  private String firstName;

  @Size(min = 2, max = 30)
  @NotBlank
  private String lastName;

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

  @NotNull
  private Country country;
}