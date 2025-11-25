package springboot.bg.harisauto.vehicle.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.vehicle.model.Vehicle;

/**
 * VehicleRepository.java - Repository interface for Vehicle entity operations.
 *
 * @author Kristian Popov
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
  List<Vehicle> findByOwner(User user);
}