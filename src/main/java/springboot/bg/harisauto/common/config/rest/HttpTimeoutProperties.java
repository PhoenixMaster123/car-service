package springboot.bg.harisauto.common.config.rest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides the values for http.timeouts of the used httpclient (to other services).
 *
 * @author Kristian Popov
 */
@Data
@Configuration
@ConfigurationProperties("http.timeout")
public class HttpTimeoutProperties {
  private int connect = 2000;
  private int read = 2000;
  private int write = 2000;
}