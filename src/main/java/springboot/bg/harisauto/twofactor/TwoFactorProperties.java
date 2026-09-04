package springboot.bg.harisauto.twofactor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TwoFactorProperties.java - Settings for email two-factor authentication.
 *
 * <p>Disabled by default on purpose. Two-factor sign-in depends on outbound email working:
 * if SMTP is misconfigured when this is switched on, nobody can complete a sign-in. Turn it
 * on deliberately, once mail delivery has been confirmed in the target environment.</p>
 *
 * @author Kristian Popov
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.two-factor")
public class TwoFactorProperties {

  /** Whether a password sign-in must be confirmed with an emailed code. */
  private boolean enabled = false;

  /** How long an issued code stays valid, in minutes. */
  private int expiryMinutes = 10;

  /** Failed attempts allowed before the code is discarded and sign-in must restart. */
  private int maxAttempts = 5;
}
