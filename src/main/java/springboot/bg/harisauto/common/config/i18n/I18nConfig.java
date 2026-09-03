package springboot.bg.harisauto.common.config.i18n;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * I18nConfig.java - Language selection for the web UI.
 *
 * <p>The chosen language is kept in a cookie rather than the session, so it survives a
 * logout and a new session - a visitor who picked German should not be shown English again
 * on their next visit.</p>
 *
 * @author Kristian Popov
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

  /** Languages the UI is translated into. */
  public static final List<Locale> SUPPORTED_LOCALES =
      List.of(Locale.ENGLISH, Locale.GERMAN, Locale.of("bg"));

  /** Query parameter that switches language, e.g. {@code /home?lang=de}. */
  public static final String LANGUAGE_PARAMETER = "lang";

  /**
   * Resolves the locale from a cookie, defaulting to English.
   *
   * @return The locale resolver.
   */
  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver resolver = new CookieLocaleResolver("LOCALE");
    resolver.setDefaultLocale(Locale.ENGLISH);
    resolver.setCookieMaxAge(Duration.ofDays(365));
    resolver.setCookieHttpOnly(true);
    return resolver;
  }

  /**
   * Lets any page switch language with a {@code lang} query parameter.
   *
   * @return The interceptor.
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    interceptor.setParamName(LANGUAGE_PARAMETER);
    // An unrecognised value is ignored rather than throwing, so a hand-edited URL
    // cannot produce a 500.
    interceptor.setIgnoreInvalidLocale(true);
    return interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }
}
