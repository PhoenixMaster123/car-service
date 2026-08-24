package springboot.bg.harisauto.common.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig.java - Maps absolute file paths for external uploads.
 *
 * @author Kristian Popov
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // External uploads directory (outside classpath, persists across rebuilds)
    Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
    String uploadPath = uploadDir.toUri().toString();

    registry.addResourceHandler("/uploads/**")
        .addResourceLocations(uploadPath);
  }
}
