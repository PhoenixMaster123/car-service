package springboot.bg.harisauto.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CarService.java - Entity class for Car Service.
 *
 * @author Kristian Popov
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "services")
public class CarService {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private BigDecimal basePrice;

  @Column(nullable = false)
  private Integer estimatedDurationInMinutes;

  @ManyToOne(optional = false)
  private ServiceCategory category;
}