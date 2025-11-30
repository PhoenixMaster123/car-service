package springboot.bg.harisauto.vehicle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Year;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springboot.bg.harisauto.user.model.User;

/**
 * Vehicle.java - Entity class for storing vehicle information.
 *
 * @author Kristian Popov
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String make;

  @Column(nullable = false)
  private String model;

  @Column(nullable = false)
  private Year manufacturingYear;

  @Column(nullable = false, unique = true)
  private String vin;

  @Column(nullable = false)
  private String licensePlate;

  @Column
  private String color;

  @Column(name = "engine_type")
  private String engineType;

  @Column(name = "body_type")
  private String bodyType;

  @ManyToOne
  private User owner;
}