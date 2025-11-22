package springboot.bg.harisauto.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import springboot.bg.harisauto.service.model.CarService;

/**
 * Shopping cart stored in the user's session.
 *
 * @author Kristian Popov
 */
@Getter
@Component
@SessionScope
public class ShoppingCart {

  private final List<CarService> items = new ArrayList<>();

  /**
   * Add item to the cart.
   *
   * @param service The service to add.
   */
  public void addItem(CarService service) {

    boolean exists = items.stream()
        .anyMatch(i -> i.getId().equals(service.getId()));

    if (!exists) {
      items.add(service);
    }
  }

  /**
   * Remove item from the cart.
   *
   * @param serviceId The service id.
   */
  public void removeItem(UUID serviceId) {
    items.removeIf(i -> i.getId().equals(serviceId));
  }

  /**
   * Clear the cart.
   */
  public void clear() {
    items.clear();
  }

  /**
   * Get the number of items in the cart.
   *
   * @return The number of items.
   */
  public int getCount() {
    return items.size();
  }

  /**
   * Calculate the total price of the items in the cart.
   *
   * @return The total price.
   */
  public BigDecimal getTotal() {
    return items.stream()
        .map(CarService::getBasePrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}