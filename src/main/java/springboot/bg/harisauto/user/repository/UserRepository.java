package springboot.bg.harisauto.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.bg.harisauto.user.model.User;

/**
 * UserRepository.java - Repository interface for User entity.
 *
 * @author Kristian Popov
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Find user by email.
   *
   * @param email - user email
   * @return user
   */
  Optional<User> findByEmail(String email);
}