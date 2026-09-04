package springboot.bg.harisauto.web.controller;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import springboot.bg.harisauto.cart.CartView;
import springboot.bg.harisauto.cart.ShoppingCart;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.model.ServiceCategory;
import springboot.bg.harisauto.service.service.CatalogService;

/**
 * ServiceController.java - Controller for handling service-related web requests.
 *
 * @author Kristian Popov
 */
@Controller
public class ServiceController {

  private final CatalogService catalogService;
  private final ShoppingCart shoppingCart;

  @Autowired
  public ServiceController(CatalogService catalogService, ShoppingCart shoppingCart) {
    this.catalogService = catalogService;
    this.shoppingCart = shoppingCart;
  }

  /**
   * Shows the service page.
   *
   * @return The service page.
   */
  @GetMapping("/services")
  public ModelAndView showServicesPage() {

    List<CarService> services = catalogService.findAll();

    Map<ServiceCategory, List<CarService>> servicesByCategory = services.stream()
        .sorted(Comparator.comparing(s -> s.getCategory().getName()))
        .collect(Collectors.groupingBy(
          CarService::getCategory,
          LinkedHashMap::new,
          Collectors.toList()
        ));

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("public/services");
    modelAndView.addObject("servicesByCategory", servicesByCategory);
    modelAndView.addObject("cart", CartView.of(shoppingCart));

    return modelAndView;
  }
}