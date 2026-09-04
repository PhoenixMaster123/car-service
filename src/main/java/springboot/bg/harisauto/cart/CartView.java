package springboot.bg.harisauto.cart;

import java.math.BigDecimal;
import java.util.List;
import springboot.bg.harisauto.service.model.CarService;

/**
 * CartView.java - Immutable snapshot of a {@link ShoppingCart}, safe to render.
 *
 * <p>The cart itself is a session-scoped proxy. Touching it from a template can force
 * the container to create an HTTP session part-way through rendering, which fails once
 * the response has been committed and truncates the page. Reading the cart into this
 * snapshot inside the controller keeps that work before the response is written.</p>
 *
 * @author Kristian Popov
 */
public final class CartView {

  private final List<CarService> items;
  private final int count;
  private final BigDecimal total;

  private CartView(List<CarService> items, int count, BigDecimal total) {
    this.items = items;
    this.count = count;
    this.total = total;
  }

  /**
   * Takes a snapshot of the given cart.
   *
   * @param cart The session-scoped cart.
   * @return A detached view of its contents.
   */
  public static CartView of(ShoppingCart cart) {
    return new CartView(List.copyOf(cart.getItems()), cart.getCount(), cart.getTotal());
  }

  public List<CarService> getItems() {
    return items;
  }

  public int getCount() {
    return count;
  }

  public BigDecimal getTotal() {
    return total;
  }
}
