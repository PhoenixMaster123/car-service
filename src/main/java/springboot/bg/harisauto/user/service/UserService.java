package springboot.bg.harisauto.user.service;

import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.bg.harisauto.common.config.security.AuthenticationMetaData;
import springboot.bg.harisauto.common.exception.UserDoesNotExistException;
import springboot.bg.harisauto.common.exception.UserEmailAlreadyExistsException;
import springboot.bg.harisauto.event.UserRegisteredEvent;
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
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;

  /** Constructor. */
  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                     ApplicationEventPublisher eventPublisher) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.eventPublisher = eventPublisher;
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

    UserRegisteredEvent event = UserRegisteredEvent.builder()
        .userId(newUser.getId())
        .firstName(newUser.getFirstName())
        .lastName(newUser.getLastName())
        .email(newUser.getEmail())
        .build();
    eventPublisher.publishEvent(event);

    userRepository.save(newUser);

    log.info("New user registered: {}", newUser.getEmail());
  }

  /**
   * Gets a user by its id.
   *
   * @param id The user id.
   * @return The user.
   */
  public User getById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserDoesNotExistException("User not found with id: " + id));
  }

  /**
   * Loads user details by email.
   *
   * @param email The user email.
   * @return The user details.
   * @throws UsernameNotFoundException If the user is not found.
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email).orElseThrow(() ->
        new UsernameNotFoundException("User not found"));

    return new AuthenticationMetaData(user.getId(), email,
        user.getPassword(), user.getRole(), true);
  }
}