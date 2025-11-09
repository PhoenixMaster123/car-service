package springboot.bg.harisauto.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import springboot.bg.harisauto.common.exception.UserEmailAlreadyExistsException;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.repository.UserRepository;
import springboot.bg.harisauto.web.dto.RegisterRequest;

/**
 * UserService.java - Service class for managing user-related operations.
 *
 * @author Kristian Popov
 */
@Slf4j
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Registers a new user.
   *
   * @param request The registration request.
   */
  @Transactional
  public void register(RegisterRequest request) {

    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

    if (existingUser.isPresent()) {
      throw new UserEmailAlreadyExistsException("Email already in use: " + request.getEmail());
    }

    User newUser = User.builder()
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(UserRole.USER)
        .build();

    userRepository.save(newUser);

    log.info("New user registered: {}", newUser.getEmail());
  }
}