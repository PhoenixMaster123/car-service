package springboot.bg.harisauto.common.config.rest;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.TimeZone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import springboot.bg.harisauto.common.config.interceptor.LoggingRequestInterceptor;

/**
 * RestConfig.java - Configuration class for RestTemplate setup.
 *
 * @author Kristian Popov
 */
@Configuration
public class RestConfig {

  /**
   * Returns a new RestTemplate instance to be used for HTTP communication.
   *
   * @param httpTimeoutProperties HttpTimeoutProperties
   *
   * @return RestTemplate
   */
  @Bean
  public RestTemplate restTemplate(HttpTimeoutProperties httpTimeoutProperties) {

    int httpConnection = httpTimeoutProperties.getConnect();
    int readTimeout = httpTimeoutProperties.getRead();

    HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(httpConnection))
        .build();

    JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(client);
    requestFactory.setReadTimeout(Duration.ofMillis(readTimeout));

    RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(jacksonBuilder().build());

    restTemplate.setMessageConverters(List.of(converter));

    restTemplate.setInterceptors(List.of(new LoggingRequestInterceptor()));

    return restTemplate;
  }

  /**
   * Jackson2ObjectMapperBuilder bean with a custom date format and timezone.
   *
   * @return Jackson2ObjectMapperBuilder
   */
  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {

    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    builder.dateFormat(new StdDateFormat()
        .withTimeZone(TimeZone.getTimeZone("Europe/Berlin"))
        .withColonInTimeZone(true));

    return builder;
  }
}