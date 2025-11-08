package springboot.bg.harisauto.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.bg.harisauto.user.repository.UserRepository;

/**
 * UserService.java - Service class for managing user-related operations.
 *
 * @author Kristian Popov
 */
@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
}
