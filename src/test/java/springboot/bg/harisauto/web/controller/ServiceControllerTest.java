package springboot.bg.harisauto.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.model.ServiceCategory;
import springboot.bg.harisauto.service.service.CatalogService;

@WebMvcTest(ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CatalogService catalogService;

  @MockitoBean
  private ShoppingCart shoppingCart;

  private List<CarService> services;

  @BeforeEach
  void setup() {

    ServiceCategory catA = new ServiceCategory();
    catA.setName("Engine");

    ServiceCategory catB = new ServiceCategory();
    catB.setName("Tires");

    CarService s1 = CarService.builder()
        .id(UUID.randomUUID())
        .name("Oil Change")
        .description("Change engine oil")
        .basePrice(new BigDecimal("49.99"))
        .estimatedDurationInMinutes(30)
        .category(catA)
        .build();

    CarService s2 = CarService.builder()
        .id(UUID.randomUUID())
        .name("Tire Rotation")
        .description("Rotate tires")
        .basePrice(new BigDecimal("19.99"))
        .estimatedDurationInMinutes(20)
        .category(catB)
        .build();

    services = List.of(s1, s2);
  }

  @Test
  @DisplayName("GET /services should return services view with model attributes")
  void showServicesPage_shouldReturnViewAndModel() throws Exception {
    when(catalogService.findAll()).thenReturn(services);

    mockMvc.perform(get("/services"))
        .andExpect(status().isOk())
        .andExpect(view().name("public/services"))
        .andExpect(model().attributeExists("servicesByCategory"))
        .andExpect(model().attributeExists("cart"));
  }
}
