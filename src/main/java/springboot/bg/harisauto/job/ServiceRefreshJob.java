package springboot.bg.harisauto.job;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.service.CatalogService;

/**
 * ScheduledTasksService.java - Service class for scheduled tasks.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class ServiceRefreshJob {

  private final CatalogService catalogService;

  /** Constructor. */
  @Autowired
  public ServiceRefreshJob(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  /**
   * Refreshes services cache every 5 seconds.
   * This ensures new services added from admin page are immediately available.
   */
  @Scheduled(fixedRate = 5000)
  public void refreshServices() {
    log.debug("Refreshing services cache at {}", LocalDateTime.now());
    
    // Force refresh by querying the database
    List<CarService> services = catalogService.findAll();
    
    log.debug("Services cache refreshed. Total services: {}", services.size());
  }
}