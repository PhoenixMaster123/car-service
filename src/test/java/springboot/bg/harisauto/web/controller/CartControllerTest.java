package springboot.bg.harisauto.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.math.BigDecimal;
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

@WebMvcTest(CartController.class)
class CartControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ShoppingCart shoppingCart;

  @MockitoBean
  private CatalogService catalogService;

  private CarService sampleService;

  @BeforeEach
  void setup() {
    ServiceCategory cat = new ServiceCategory();
    cat.setName("Engine");

    sampleService = CarService.builder()
        .id(UUID.randomUUID())
        .name("Oil Change")
        .description("Change engine oil")
        .basePrice(new BigDecimal("49.99"))
        .estimatedDurationInMinutes(30)
        .category(cat)
        .build();
  }

  @Test
  @DisplayName("POST /cart/add/{id} should add item and redirect to /services")
  void addToCart_shouldRedirect() throws Exception {
    when(catalogService.getById(sampleService.getId())).thenReturn(sampleService);

    mockMvc.perform(post("/cart/add/" + sampleService.getId()).with(csrf()).with(user("user")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/services"));
  }

  @Test
  @DisplayName("POST /cart/remove/{id} should remove item and redirect to /services")
  void removeFromCart_shouldRedirect() throws Exception {
    mockMvc.perform(post("/cart/remove/" + sampleService.getId()).with(csrf()).with(user("user")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/services"));
  }

  @Test
  @DisplayName("POST /cart/clear should clear cart and redirect to /services")
  void clearCart_shouldRedirect() throws Exception {
    mockMvc.perform(post("/cart/clear").with(csrf()).with(user("user")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/services"));
  }

  @Test
  @DisplayName("GET /cart/checkout with empty cart should redirect to services with error")
  void checkout_emptyCart_shouldRedirectToServicesWithError() throws Exception {
    when(shoppingCart.getCount()).thenReturn(0);

    mockMvc.perform(get("/cart/checkout").with(user("user")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/services?error=empty"));
  }

  @Test
  @DisplayName("GET /cart/checkout with items should redirect to bookings")
  void checkout_withItems_shouldRedirectToBookings() throws Exception {
    when(shoppingCart.getCount()).thenReturn(1);

    mockMvc.perform(get("/cart/checkout").with(user("user")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/bookings"));
  }
}
