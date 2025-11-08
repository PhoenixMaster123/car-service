package springboot.bg.harisauto.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.bg.harisauto.user.model.User;
import java.util.Optional;
import java.util.UUID;

/**
 * UserRepository.java - Repository interface for User entity.
 *
 * @author Kristian Popov
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
}
