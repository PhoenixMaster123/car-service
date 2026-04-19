package springboot.bg.harisauto.dev;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DevModeProperties.java - Toggle for developer-mode behavior such as bypassing the real
 * payment gateway and short-circuiting the booking/invoice flow.
 *
 * Configured via {@code app.dev-mode} (default false). Enabled by application-dev.yml.
 *
 * @author Kristian Popov
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.dev-mode")
public class DevModeProperties {

  /** When true, exposes /dev/* endpoints and shows simulation controls in the UI. */
  private boolean enabled = false;
}
