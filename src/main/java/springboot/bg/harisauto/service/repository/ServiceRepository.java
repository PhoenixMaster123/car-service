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
  boolean existsByName(String name);
}