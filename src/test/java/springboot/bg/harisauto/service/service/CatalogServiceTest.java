package springboot.bg.harisauto.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springboot.bg.harisauto.common.exception.ResourceNotFoundException;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.model.ServiceCategory;
import springboot.bg.harisauto.service.repository.ServiceCategoryRepository;
import springboot.bg.harisauto.service.repository.ServiceRepository;
import springboot.bg.harisauto.web.dto.CreateServiceRequest;
import springboot.bg.harisauto.web.dto.UpdateServiceRequest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CatalogServiceTest {

    private ServiceRepository serviceRepository;
    private ServiceCategoryRepository categoryRepository;
    private CatalogService catalogService;

    @BeforeEach
    void setup() {
        serviceRepository = mock(ServiceRepository.class);
        categoryRepository = mock(ServiceCategoryRepository.class);
        catalogService = new CatalogService(serviceRepository, categoryRepository);
    }

    @Test
    void getById_whenMissing_throws() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> catalogService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createService_whenDuplicateName_throws() {
        when(serviceRepository.existsByName("Oil Change")).thenReturn(true);

        CreateServiceRequest req = CreateServiceRequest.builder()
                .name("Oil Change")
                .description("Desc long enough")
                .basePrice(new BigDecimal("9.99"))
                .estimatedDurationInMinutes(10)
                .categoryId(UUID.randomUUID())
                .build();

        assertThatThrownBy(() -> catalogService.createService(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void createService_whenCategoryMissing_throws() {
        when(serviceRepository.existsByName("New Svc")).thenReturn(false);
        UUID catId = UUID.randomUUID();
        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        CreateServiceRequest req = CreateServiceRequest.builder()
                .name("New Svc")
                .description("Long description here")
                .basePrice(new BigDecimal("19.99"))
                .estimatedDurationInMinutes(30)
                .categoryId(catId)
                .build();

        assertThatThrownBy(() -> catalogService.createService(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void deleteService_whenMissing_throws() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> catalogService.deleteService(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Service not found");
    }

    @Test
    void updateService_whenNameTaken_throws() {
        UUID id = UUID.randomUUID();
        ServiceCategory cat = new ServiceCategory();
        cat.setName("Engine");
        CarService existing = CarService.builder()
                .id(id)
                .name("Old Name")
                .description("d")
                .basePrice(new BigDecimal("1.00"))
                .estimatedDurationInMinutes(5)
                .category(cat)
                .build();
        when(serviceRepository.findById(id)).thenReturn(Optional.of(existing));
        when(serviceRepository.existsByName("Taken")).thenReturn(true);

        UpdateServiceRequest req = UpdateServiceRequest.builder()
                .id(id)
                .name("Taken")
                .description("Changed")
                .basePrice(new BigDecimal("2.00"))
                .estimatedDurationInMinutes(10)
                .categoryId(UUID.randomUUID())
                .build();

        assertThatThrownBy(() -> catalogService.updateService(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }
}
