package springboot.bg.harisauto.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentResponse.class - DTO class for payment response.
 *
 * @author Kristian Popov
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentResponse {

  private String clientSecret;
}