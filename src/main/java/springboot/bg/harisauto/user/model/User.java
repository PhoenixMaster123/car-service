package springboot.bg.harisauto.user.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import springboot.bg.harisauto.vehicle.model.Vehicle;

/**
 * User.java - Entity class for storing user information.
 *
 * @author Kristian Popov
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  private Country country;

  private String password;

  @Enumerated(EnumType.STRING)
  private LoginProvider authProvider;

  private String profilePicture;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdOn;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedOn;

  // LAZY: no page renders this collection - the screens that show vehicles query
  // VehicleService instead - so eager loading only added a join to every user read,
  // including the one on every authentication.
  @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Vehicle> vehicles;

  /**
   * Identity-based equality.
   *
   * <p>Deriving equality from the fields, as {@code @Data} did, is unsafe for an entity:
   * two distinct rows holding the same values compared equal, the hash changed as the
   * instance was mutated, and the comparison reached into the vehicles collection - which
   * now would trigger a lazy load.</p>
   *
   * @param other The object to compare with.
   * @return true when both are users with the same, non-null id.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof User user)) {
      return false;
    }
    return id != null && id.equals(user.id);
  }

  /**
   * Constant hash, so an instance keeps the same bucket before and after it is persisted.
   *
   * @return A stable hash code.
   */
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
