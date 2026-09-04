package springboot.bg.harisauto.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentIntentResponse.java - The authoritative state of a payment, read back from
 * payment-service rather than taken from the browser redirect.
 *
 * @author Kristian Popov
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentIntentResponse {

  /** The payment intent id, e.g. {@code pi_3Abc...}. */
  private String id;

  /** Stripe status; only {@code succeeded} may be treated as paid. */
  private String status;

  /** Amount actually received, in the currency's smallest unit. */
  private Long amountReceived;

  /** ISO currency code. */
  private String currency;
}
