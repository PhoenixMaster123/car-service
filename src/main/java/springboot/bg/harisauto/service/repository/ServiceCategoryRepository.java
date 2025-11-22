package springboot.bg.harisauto.service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.bg.harisauto.service.model.ServiceCategory;

/**
 * ServiceCategoryRepository.java - Repository interface for ServiceCategory entity.
 *
 * @author Kristian Popov
 */
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, UUID> {
}