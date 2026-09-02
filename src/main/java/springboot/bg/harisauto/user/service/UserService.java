package springboot.bg.harisauto.user.service;

import java.util.List;
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
import springboot.bg.harisauto.common.exception.UserPasswordDoesNotMatchException;
import springboot.bg.harisauto.event.UserRegisteredEvent;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.repository.UserRepository;
import springboot.bg.harisauto.web.dto.AdminChangeEmailRequest;
import springboot.bg.harisauto.web.dto.AdminChangePasswordRequest;
import springboot.bg.harisauto.web.dto.ChangeProfileInfoRequest;
import springboot.bg.harisauto.web.dto.ChangeUserPasswordRequest;
import springboot.bg.harisauto.web.dto.RegisterNewUserRequest;
import springboot.bg.harisauto.web.dto.RegisterRequest;
import springboot.bg.harisauto.web.dto.UpdateUserRequest;

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
   * Registers a new user (Public registration).
   *
   * @param request The registration request.
   */
  @Transactional
  public void register(RegisterRequest request) {

    checkEmailExists(request.getEmail());

    User newUser = User.builder()
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(UserRole.USER)
        .build();

    User savedUser = userRepository.save(newUser);

    // Published after the save so the event carries the generated id; the listener is
    // asynchronous, so a failing mail server cannot roll the registration back.
    UserRegisteredEvent event = UserRegisteredEvent.builder()
        .userId(savedUser.getId())
        .firstName(savedUser.getFirstName())
        .lastName(savedUser.getLastName())
        .email(savedUser.getEmail())
        .build();
    eventPublisher.publishEvent(event);

    log.info("New user registered: {}", savedUser.getEmail());
  }


  /**
   * Register new user from admin page.
   *
   * @param request The registration request.
   */
  @Transactional
  public void registerNewUser(RegisterNewUserRequest request) {

    checkEmailExists(request.getEmail());

    User newUser = User.builder()
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .phoneNumber(request.getPhoneNumber())
        .country(request.getCountry())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();

    log.info("New user registered by admin: {}", newUser.getEmail());

    userRepository.save(newUser);
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
   * Gets all users.
   *
   * @return The list of users.
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Updates user details (Self-update profile).
   *
   * @param user The user.
   * @param request The request containing the new details.
   */
  public void updateUserDetails(User user, ChangeProfileInfoRequest request) {

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setCountry(request.getCountry());

    log.info("Updating user profile: {}", user.getEmail());

    userRepository.save(user);
  }

  /**
   * Changes the user's password.
   *
   * @param user The user.
   * @param request The request containing the current and new passwords.
   */
  public void changeUserPassword(User user, ChangeUserPasswordRequest request) {

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new UserPasswordDoesNotMatchException("Invalid current password");
    }

    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
      throw new UserPasswordDoesNotMatchException("New password cannot be the same "
                                                 + "as the current password");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    log.info("Updating user password: {}", user.getEmail());

    userRepository.save(user);
  }

  /**
   * Updates user details.
   *
   * @param request The request containing the new details.
   */
  public void updateUser(UpdateUserRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UserDoesNotExistException("User not found: " + request.getEmail()));

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setCountry(request.getCountry());

    if (request.getRole() != null) {
      user.setRole(request.getRole());
    }

    userRepository.save(user);
    log.info("Admin updated user: {}", user.getEmail());
  }

  /**
   * Deletes a user by its id.
   *
   * @param userId The user id.
   */
  public void deleteUserById(UUID userId) {

    userRepository.deleteById(userId);

    log.info("User deleted with id: {}", userId);
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

  /**
   * Changes the admin's email.
   *
   * @param user The admin user.
   * @param request The request containing the new email.
   */
  @Transactional
  public void changeAdminEmail(User user, AdminChangeEmailRequest request) {

    if (userRepository.findByEmail(request.getNewEmail()).isPresent()
        && !user.getEmail().equals(request.getNewEmail())) {
      throw new UserEmailAlreadyExistsException("Email already in use: " + request.getNewEmail());
    }

    user.setEmail(request.getNewEmail());

    log.info("Admin email updated: {}", request.getNewEmail());

    userRepository.save(user);
  }

  /**
   * Changes the admin's password.
   *
   * @param user The admin user.
   * @param request The request containing the current and new passwords.
   */
  @Transactional
  public void changeAdminPassword(User user, AdminChangePasswordRequest request) {

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new UserPasswordDoesNotMatchException("Invalid current password");
    }

    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
      throw new UserPasswordDoesNotMatchException("New password cannot be the same "
                                                 + "as the current password");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    log.info("Admin password updated: {}", user.getEmail());

    userRepository.save(user);
  }

  /**
   * Check if email already exists.
   *
   * @param email The email to check.
   */
  private void checkEmailExists(String email) {

    if (userRepository.findByEmail(email).isPresent()) {
      throw new UserEmailAlreadyExistsException("Email already in use: " + email);
    }
  }
}