package springboot.bg.harisauto.service.service;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.bg.harisauto.common.config.cache.CacheConfig;
import springboot.bg.harisauto.common.exception.ResourceNotFoundException;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.model.ServiceCategory;
import springboot.bg.harisauto.service.repository.ServiceCategoryRepository;
import springboot.bg.harisauto.service.repository.ServiceRepository;
import springboot.bg.harisauto.web.dto.CreateServiceRequest;
import springboot.bg.harisauto.web.dto.UpdateServiceRequest;

/**
 * CatalogService.java - Service class for managing the catalog of car services.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class CatalogService {

  private final ServiceRepository serviceRepository;
  private final ServiceCategoryRepository categoryRepository;

  @Autowired
  public CatalogService(ServiceRepository serviceRepository,
                         ServiceCategoryRepository categoryRepository) {
    this.serviceRepository = serviceRepository;
    this.categoryRepository = categoryRepository;
  }

  /**
   * Find all services.
   *
   * @return The list of services.
   */
  @Cacheable(CacheConfig.SERVICES)
  public List<CarService> findAll() {
    // Copied so the cached value cannot be mutated by a caller holding the list.
    return List.copyOf(serviceRepository.findAll());
  }

  /**
   * Gets a service by its id.
   *
   * @param id The service id.
   * @return The service.
   */
  public CarService getById(UUID id) {
    return serviceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
  }

  /**
   * Gets all categories.
   *
   * @return The list of categories.
   */
  @Cacheable(CacheConfig.SERVICE_CATEGORIES)
  public List<ServiceCategory> getAllCategories() {
    return List.copyOf(categoryRepository.findAll());
  }

  /**
   * Creates a new service.
   *
   * @param request The create service request.
   */
  @Transactional
  @CacheEvict(value = CacheConfig.SERVICES, allEntries = true)
  public void createService(CreateServiceRequest request) {

    if (serviceRepository.existsByName(request.getName())) {
      throw new IllegalArgumentException(
          "Service with name '" + request.getName() + "' already exists");
    }

    ServiceCategory category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Category not found with id: " + request.getCategoryId()));

    CarService service = CarService.builder()
        .name(request.getName())
        .description(request.getDescription())
        .basePrice(request.getBasePrice())
        .estimatedDurationInMinutes(request.getEstimatedDurationInMinutes())
        .category(category)
        .build();

    log.info("Creating new service: {}", request.getName());

    serviceRepository.save(service);
  }

  /**
   * Updates an existing service.
   *
   * @param request The update service request.
   */
  @Transactional
  @CacheEvict(value = CacheConfig.SERVICES, allEntries = true)
  public void updateService(UpdateServiceRequest request) {
    CarService service = serviceRepository.findById(request.getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Service not found with id: " + request.getId()));

    // Check if name is being changed and if new name already exists
    if (!service.getName().equals(request.getName())
        && serviceRepository.existsByName(request.getName())) {
      throw new IllegalArgumentException("Service with name '"
              + request.getName() + "' already exists");
    }

    ServiceCategory category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Category not found with id: " + request.getCategoryId()));

    service.setName(request.getName());
    service.setDescription(request.getDescription());
    service.setBasePrice(request.getBasePrice());
    service.setEstimatedDurationInMinutes(request.getEstimatedDurationInMinutes());
    service.setCategory(category);

    log.info("Updating service: {}", request.getName());

    serviceRepository.save(service);
  }

  /**
   * Deletes a service by its id.
   *
   * @param id The service id.
   */
  @Transactional
  @CacheEvict(value = CacheConfig.SERVICES, allEntries = true)
  public void deleteService(UUID id) {
    if (!serviceRepository.existsById(id)) {
      throw new ResourceNotFoundException("Service not found with id: " + id);
    }

    serviceRepository.deleteById(id);
    log.info("Service deleted with id: {}", id);
  }
}