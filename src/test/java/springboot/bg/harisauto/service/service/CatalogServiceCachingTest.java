package springboot.bg.harisauto.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import springboot.bg.harisauto.chatbot.service.GeminiService;
import springboot.bg.harisauto.common.config.cache.CacheConfig;
import springboot.bg.harisauto.email.EmailService;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.model.ServiceCategory;
import springboot.bg.harisauto.service.repository.ServiceCategoryRepository;
import springboot.bg.harisauto.service.repository.ServiceRepository;
import springboot.bg.harisauto.web.dto.CreateServiceRequest;

/**
 * Proves the catalogue cache both caches and evicts. Eviction is the half that matters:
 * a stale catalogue after an admin edit would be a visible bug.
 */
@SpringBootTest
@ActiveProfiles("test")
class CatalogServiceCachingTest {

    @MockitoBean
    private ServiceRepository serviceRepository;

    @MockitoBean
    private ServiceCategoryRepository categoryRepository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private GeminiService geminiService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CacheManager cacheManager;

    private ServiceCategory category;

    @BeforeEach
    void setup() {
        cacheManager.getCache(CacheConfig.SERVICES).clear();

        category = ServiceCategory.builder()
                .id(UUID.randomUUID()).name("WASH").displayName("Wash").build();

        CarService service = CarService.builder()
                .id(UUID.randomUUID())
                .name("Express Wash")
                .basePrice(new BigDecimal("19.99"))
                .category(category)
                .build();

        when(serviceRepository.findAll()).thenReturn(List.of(service));
    }

    @Test
    void findAll_isServedFromCacheOnRepeatedCalls() {
        assertThat(catalogService.findAll()).hasSize(1);
        catalogService.findAll();
        catalogService.findAll();

        verify(serviceRepository, times(1)).findAll();
    }

    @Test
    void createService_evictsTheCacheSoTheNextReadHitsTheDatabase() {
        catalogService.findAll();
        verify(serviceRepository, times(1)).findAll();

        when(serviceRepository.existsByName(any())).thenReturn(false);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(serviceRepository.save(any(CarService.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateServiceRequest request = new CreateServiceRequest();
        request.setName("Standard Wash");
        request.setDescription("desc");
        request.setBasePrice(new BigDecimal("29.99"));
        request.setEstimatedDurationInMinutes(45);
        request.setCategoryId(category.getId());

        catalogService.createService(request);

        catalogService.findAll();
        verify(serviceRepository, times(2)).findAll();
    }
}
