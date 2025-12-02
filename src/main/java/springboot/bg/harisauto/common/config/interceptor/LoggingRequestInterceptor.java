package springboot.bg.harisauto.common.config.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * RestConfig.java - Configuration class for RestTemplate setup.
 *
 * @author Kristian Popov
 */
@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    traceRequest(request, body);

    ClientHttpResponse response = execution.execute(request, body);

    traceResponse(response);

    return response;
  }

  private void traceRequest(HttpRequest request, byte[] body) {

    if (log.isDebugEnabled()) {
      log.debug("===========================request begin================================================");
      log.debug("URI         : {}", request.getURI());
      log.debug("Method      : {}", request.getMethod());
      log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
      log.debug("==========================request end================================================");
    }
  }

  private void traceResponse(ClientHttpResponse response) throws IOException {

    if (log.isDebugEnabled()) {

      String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);

      log.debug("============================response begin==========================================");
      log.debug("Status code  : {}", response.getStatusCode());
      log.debug("Status text  : {}", response.getStatusText());
      log.debug("Headers      : {}", response.getHeaders());
      log.debug("Response body: {}", responseBody);
      log.debug("=======================response end=================================================");
    }
  }
}