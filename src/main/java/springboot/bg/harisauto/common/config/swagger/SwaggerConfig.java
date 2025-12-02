package springboot.bg.harisauto.common.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig.java - Configuration class for Swagger settings.
 *
 * @author Kristian Popov
 */
@Configuration
public class SwaggerConfig {

  /**
   * Customizer to sort the controller tags alphabetically in the Swagger UI.
   *
   * @return OpenApiCustomizer
   */
  @Bean
  public OpenApiCustomizer sortTagsAlphabetically() {
    return openApi -> {
      if (openApi.getTags() != null) {
        openApi.setTags(
            openApi.getTags()
              .stream()
                .sorted(Comparator.comparing(tag -> stripAccents(tag.getName())))
                .collect(Collectors.toList())
        );
      }
    };
  }

  /** Custom OpenAPI configuration. */
  @Bean
  public OpenAPI customOpenApi() {

    Contact contact = new Contact();
    contact.setName("Car Wash Service Haris");
    contact.setEmail("harisauto@gmail.com");
    contact.setUrl("http://localhost:8080");

    License license = new License()
        .name("MIT License")
        .url("https://opensource.org/licenses/MIT");

    Info applicationInfo = new Info()
        .title("Car Service")
        .version("1.0")
        .description(
            """
            Welcome to the Car Service REST-API documentation v1.\
            \s

            Most endpoints can be executed by using the Try out function of swagger.\s
            You can find this function on each endpoint specification.\s
            The request can be configured by changing the available parameter values. \
            Please be extremely carefully with this function on production environments.
            \s
            Currently, there is no authentification for the endpoints necessary.
           \s"""
        )
        .contact(contact)
        .termsOfService("https://www.harisauto.com/terms-of-service")
        .license(license);

    return new OpenAPI().info(applicationInfo);
  }

  /**
   * Helper method to remove accents from a string for proper sorting.
   */
  private String stripAccents(String str) {
    if (str == null) {
      return null;
    }
    String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
    return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
  }
}