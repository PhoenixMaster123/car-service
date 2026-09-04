package springboot.bg.harisauto.common.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CacheConfig.java - In-process caching for the service catalogue.
 *
 * <p>The catalogue is read on almost every page and changes only when an administrator
 * edits it, so it is the one thing here worth caching. Writes evict explicitly, so a
 * change made on the admin page is visible immediately; the time limit is only a
 * backstop against a stale entry outliving a restart of a sibling instance.</p>
 *
 * @author Kristian Popov
 */
@Configuration
@EnableCaching
public class CacheConfig {

  /** Cache holding the full service catalogue. */
  public static final String SERVICES = "services";

  /** Cache holding the service categories. */
  public static final String SERVICE_CATEGORIES = "serviceCategories";

  /**
   * Builds the cache manager.
   *
   * <p>Caffeine keeps this in-process. That suits a single instance; running more than one
   * would need a shared cache, which is why the eviction below is explicit rather than
   * relying on expiry alone.</p>
   *
   * @return The configured cache manager.
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(SERVICES, SERVICE_CATEGORIES);
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(30, TimeUnit.MINUTES));
    return cacheManager;
  }
}
