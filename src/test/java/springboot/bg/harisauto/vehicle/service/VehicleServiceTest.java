package springboot.bg.harisauto.vehicle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import springboot.bg.harisauto.common.exception.VehicleBusinessException;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.vehicle.model.Vehicle;
import springboot.bg.harisauto.vehicle.repository.VehicleRepository;
import springboot.bg.harisauto.web.dto.CreateVehicleRequest;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    private VehicleRepository vehicleRepository;
    private VehicleService vehicleService;

    @BeforeEach
    void setup() {
        vehicleRepository = mock(VehicleRepository.class);
        vehicleService = new VehicleService(vehicleRepository);
    }

    private CreateVehicleRequest req(String vin) {
        return CreateVehicleRequest.builder()
                .make("Toyota")
                .model("Corolla")
                .manufacturingYear(Year.of(2020))
                .licensePlate("ABC123")
                .vin(vin)
                .color("Blue")
                .build();
    }

    @Test
    void createVehicle_whenMoreThan3Vehicles_throws() {
        User user = new User();
        user.setEmail("u@x.com");
        when(vehicleRepository.findByOwner(user))
                .thenReturn(List.of(new Vehicle(), new Vehicle(), new Vehicle()));

        assertThatThrownBy(() -> vehicleService.createVehicle(user, req("VINVINVINVINVINV1")))
                .isInstanceOf(VehicleBusinessException.class)
                .hasMessageContaining("cannot add more than 3 vehicles");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_whenDuplicateVin_throws() {
        User user = new User();
        user.setEmail("u@x.com");
        Vehicle v = Vehicle.builder().vin("DUPLICATEVIN123456").build();
        when(vehicleRepository.findByOwner(user)).thenReturn(List.of(v));

        assertThatThrownBy(() -> vehicleService.createVehicle(user, req("DUPLICATEVIN123456")))
                .isInstanceOf(VehicleBusinessException.class)
                .hasMessageContaining("already registered");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_happyPath_savesToRepository() {
        User user = new User();
        user.setEmail("u@x.com");
        when(vehicleRepository.findByOwner(user)).thenReturn(List.of());

        vehicleService.createVehicle(user, req("UNIQUEVIN12345678"));

        // The saved row is the contract now; the entity collection is no longer
        // mutated, since the User reaching this method is detached.
        ArgumentCaptor<Vehicle> saved = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository, times(1)).save(saved.capture());
        assertThat(saved.getValue().getVin()).isEqualTo("UNIQUEVIN12345678");
        assertThat(saved.getValue().getOwner()).isSameAs(user);
    }

    @Test
    void getById_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vehicleService.getById(id))
                .isInstanceOf(VehicleBusinessException.class)
                .hasMessageContaining("Vehicle not found");
    }

    @Test
    void findById_returnsEmptyWhenMissing() {
        UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());
        assertThat(vehicleService.findById(id)).isEmpty();
    }

    @Test
    void deleteVehicle_checksOwnerAndDeletes() {
        UUID id = UUID.randomUUID();
        User owner = new User();
        owner.setId(UUID.randomUUID());
        Vehicle v = new Vehicle();
        v.setOwner(owner);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(v));

        vehicleService.deleteVehicle(owner, id);
        verify(vehicleRepository, times(1)).delete(v);
    }

    @Test
    void deleteVehicle_whenNotOwner_throws() {
        UUID id = UUID.randomUUID();
        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setVehicles(new ArrayList<>());

        User other = new User();
        other.setId(UUID.randomUUID());

        Vehicle v = new Vehicle();
        v.setOwner(other);
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> vehicleService.deleteVehicle(owner, id))
                .isInstanceOf(VehicleBusinessException.class)
                .hasMessageContaining("not authorized");
        verify(vehicleRepository, never()).delete(any());
    }

    @Test
    void deleteVehicle_whenMissing_throws() {
        UUID id = UUID.randomUUID();
        User owner = new User();
        owner.setId(UUID.randomUUID());
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vehicleService.deleteVehicle(owner, id))
                .isInstanceOf(springboot.bg.harisauto.common.exception.VehicleBusinessException.class)
                .hasMessageContaining("Vehicle not found");
    }
}
