package springboot.bg.harisauto.service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import springboot.bg.harisauto.service.model.CarService;

/**
 * ServiceRepository.java - Repository interface for Service entity.
 *
 * @author Kristian Popov
 */
public interface ServiceRepository extends JpaRepository<CarService, UUID> {

  /**
   * Check if service with given name exists.
   *
   * @param name - service name
   * @return true if service exists, false otherwise
   */
  boolean existsByName(String name);
}