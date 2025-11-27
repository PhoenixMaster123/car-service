package springboot.bg.harisauto.vehicle.service;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.bg.harisauto.common.exception.VehicleBusinessException;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.repository.VehicleRepository;
import springboot.bg.harisauto.web.dto.CreateVehicleRequest;

/**
 * VehicleService.class - Service class for managing vehicles.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class VehicleService {

  private final VehicleRepository vehicleRepository;

  @Autowired
  public VehicleService(VehicleRepository vehicleRepository) {
    this.vehicleRepository = vehicleRepository;
  }

  /**
   * Creates a new vehicle.
   *
   * @param user The user who owns the vehicle.
   * @param request The vehicle creation request.
   */
  @Transactional
  public void createVehicle(User user, CreateVehicleRequest request) {

    if (user.getVehicles().size() >= 3) {
      throw new VehicleBusinessException("You cannot add more than 3 vehicles.");
    }

    boolean exists = user.getVehicles().stream()
        .anyMatch(v -> v.getVin().equalsIgnoreCase(request.getVin()));

    if (exists) {
      throw new VehicleBusinessException("This VIN is already registered to your account.");
    }

    Vehicle vehicle = Vehicle.builder()
            .make(request.getMake())
            .model(request.getModel())
            .manufacturingYear(request.getManufacturingYear())
            .licensePlate(request.getLicensePlate())
            .vin(request.getVin())
            .color(request.getColor())
            .owner(user)
            .build();

    log.info("New vehicle created for user: {}", user.getEmail());

    user.getVehicles().add(vehicle);
    vehicleRepository.save(vehicle);
  }

  /**
   * Get vehicles by user.
   *
   * @param user - user
   * @return list of vehicles
   */
  public List<Vehicle> getVehiclesByUser(User user) {
    return vehicleRepository.findByOwner(user);
  }

  /**
   * Deletes a vehicle.
   *
   * @param user The user who owns the vehicle.
   * @param vehicleId The vehicle id.
   */
  @Transactional
  public void deleteVehicle(User user, UUID vehicleId) {
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
        .orElseThrow(() -> new VehicleBusinessException("Vehicle not found"));

    if (!vehicle.getOwner().getId().equals(user.getId())) {
      throw new VehicleBusinessException("You are not authorized to delete this vehicle.");
    }

    user.getVehicles().remove(vehicle);
    vehicleRepository.delete(vehicle);
  }
}