package springboot.bg.harisauto.service.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.repository.ServiceRepository;

/**
 * CatalogService.java - Service class for managing the catalog of car services.
 *
 * @author Kristian Popov
 */
@Service
public class CatalogService {

  private final ServiceRepository serviceRepository;

  @Autowired
  public CatalogService(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  /**
   * Find all services.
   *
   * @return The list of services.
   */
  public List<CarService> findAll() {
    return serviceRepository.findAll();
  }
}