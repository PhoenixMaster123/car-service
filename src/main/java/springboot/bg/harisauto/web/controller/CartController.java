package springboot.bg.harisauto.web.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.service.service.CatalogService;

/**
 * CartController.java - Controller for handling shopping cart-related web requests.
 *
 * @author Kristian Popov
 */
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

  private final ShoppingCart shoppingCart;
  private final CatalogService catalogService;

  /**
   * Add item to the cart.
   *
   * @param id The service id.
   * @return The services page.
   */
  @PostMapping("/add/{id}")
  public String addToCart(@PathVariable UUID id) {
    shoppingCart.addItem(catalogService.getById(id));
    return "redirect:/services";
  }

  /**
   * Remove item from the cart.
   *
   * @param id The service id.
   * @return The services page.
   */
  @PostMapping("/remove/{id}")
  public String removeFromCart(@PathVariable UUID id) {
    shoppingCart.removeItem(id);
    return "redirect:/services";
  }

  /**
   * Clear the cart.
   *
   * @return The services page.
   */
  @PostMapping("/clear")
  public String clearCart() {
    shoppingCart.clear();
    return "redirect:/services";
  }

  /**
   * Checkout the cart.
   *
   * @return The booking page.
   */
  @GetMapping("/checkout")
  public String checkout() {
    if (shoppingCart.getCount() == 0) {
      return "redirect:/services?error=empty";
    }
    return "redirect:/bookings";
  }
}