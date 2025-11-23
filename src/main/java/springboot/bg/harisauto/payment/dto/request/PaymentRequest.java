package springboot.bg.harisauto.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment Request - DTO class for payment request.
 *
 * @author Kristian Popov
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentRequest {

  private Long amount;
  private String currency;
  private String description;
  private String paymentMethod;
}