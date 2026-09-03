package springboot.bg.harisauto.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * PaymentVerificationProperties.java - Controls whether a payment is confirmed against
 * payment-service before an invoice is marked PAID.
 *
 * <p>Defaults to {@code false} because {@code payment-service} does not yet expose
 * {@code GET /api/payment/{id}}. While it is off, a returning customer gets a PENDING
 * invoice that has to be reconciled, rather than a PAID one taken on trust from the
 * browser's redirect. Turn it on as soon as that endpoint exists.</p>
 *
 * @author Kristian Popov
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment.verification")
public class PaymentVerificationProperties {

  /** When true, the payment intent is read back and must report {@code succeeded}. */
  private boolean enabled = false;
}
