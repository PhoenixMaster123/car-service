package springboot.bg.harisauto.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequest.java - DTO class for user login requests.
 *
 * @author Kristian Popov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Invalid email format")
  String email;

  @NotBlank(message = "Password cannot be empty")
  String password;
}