package springboot.bg.harisauto.common.init;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import springboot.bg.harisauto.service.model.CarService;
import springboot.bg.harisauto.service.model.ServiceCategory;
import springboot.bg.harisauto.service.repository.ServiceCategoryRepository;
import springboot.bg.harisauto.service.repository.ServiceRepository;

/**
 * DataInitializer.java - Initializes the database with default service categories and services.
 *
 * @author Kristian Popov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final ServiceCategoryRepository categoryRepository;
  private final ServiceRepository serviceRepository;

  @Override
  public void run(String... args) {
    if (categoryRepository.count() == 0) {
      seedData();
    }
  }

  /**
   * Seeds the database with default service categories and services.
   */
  private void seedData() {

    ServiceCategory washCat = createCategory("WASH_AND_DETAIL", "Car Wash & Detailing");

    // Wash Services
    createService("Express Wash",
            "Wash & Detailing",
            new BigDecimal("19.99"),
            30,
            washCat);

    createService("Standard Wash",
            "Wash & Detailing",
            new BigDecimal("29.99"),
            45,
            washCat);

    createService("Interior Cleaning",
            "Wash & Detailing",
            new BigDecimal("49.99"),
            60,
            washCat);

    createService("Full-Service Wash",
            "Wash & Detailing",
            new BigDecimal("59.99"),
            90,
            washCat);

    ServiceCategory maintCat = createCategory("MAINTENANCE_AND_REPAIR", "Maintenance & Repair");

    // Maintenance Services
    createService("Oil and Filter Change",
            "Oil & Filter Change",
            new BigDecimal("89.99"),
            60,
            maintCat);

    createService("Tire Rotation",
            "Tire Rotation",
            new BigDecimal("24.99"),
            30,
            maintCat);

    createService("Tire Swap",
            "Tire Swap",
            new BigDecimal("79.99"),
            45,
            maintCat);

    createService("Tire Puncture Repair",
            "Tire Puncture Repair",
            new BigDecimal("34.99"),
            30,
            maintCat);

    createService("Brake Pad & Disc Replacement",
            "Brake Pad & Disc Replacement",
            new BigDecimal("199.99"),
            120,
            maintCat);

    createService("Brake Fluid Change",
            "Brake Fluid Change",
            new BigDecimal("69.99"),
            45,
            maintCat);

    createService("Fluid Checks & Top-Ups",
            "Fluid Checks & Top-Ups",
            new BigDecimal("14.99"),
            15,
            maintCat);

    createService("Battery Replacement",
            "Battery Replacement",
            new BigDecimal("129.99"),
            30,
            maintCat);

    createService("Wiper Blade Replacement",
            "Wiper Blade Replacement",
            new BigDecimal("29.99"),
            10,
            maintCat);

    ServiceCategory diagCat = createCategory("DIAGNOSTICS_AND_INSPECTION",
            "Diagnostics & Inspection");

    // Diagnostic Services
    createService("Vehicle Health Check",
            "Vehicle Health Check",
            new BigDecimal("49.99"),
            45,
            diagCat);

    createService("On-Board Diagnostics (OBD) Scan",
            "OBD Scan",
            new BigDecimal("39.99"),
            20,
            diagCat);

    createService("Seasonal Inspection",
            "Seasonal Inspection",
            new BigDecimal("59.99"),
            60,
            diagCat);

    createService("TUV Pre-Inspection",
            "TUV Pre-Inspection",
            new BigDecimal("79.99"),
            60,
            diagCat);

    log.info("--- Database Initialized with default Services and Categories ---");
  }

  /**
   * Creates a service category.
   *
   * @param name The category name.
   * @param displayName The category display name.
   * @return The created category.
   */
  private ServiceCategory createCategory(String name, String displayName) {
    return categoryRepository.save(ServiceCategory.builder()
        .name(name)
        .displayName(displayName)
        .build());
  }

  /**
   * Creates a service.
   *
   * @param name The service name.
   * @param description The service description.
   * @param price The service price.
   * @param duration The estimated duration in minutes.
   * @param category The service category.
   */
  private void createService(String name,
      String description, BigDecimal price, Integer duration, ServiceCategory category) {
    if (!serviceRepository.existsByName(name)) {
      serviceRepository.save(CarService.builder()
          .name(name)
          .description(description)
          .basePrice(price)
          .estimatedDurationInMinutes(duration)
          .category(category)
          .build());
    }
  }
}