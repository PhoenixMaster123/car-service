package springboot.bg.harisauto.common.config.security;

import java.util.Optional;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import springboot.bg.harisauto.user.model.LoginProvider;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.repository.UserRepository;

/**
 * CustomOAuth2UserService.java - Service to handle OAuth2 user authentication and registration.
 *
 * @author Kristian Popov
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {

    OAuth2User oauthUser = super.loadUser(userRequest);

    String email = oauthUser.getAttribute("email");
    String fullName = oauthUser.getAttribute("name");

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    LoginProvider provider = "google".equalsIgnoreCase(registrationId) ? LoginProvider.GOOGLE :
        "github".equalsIgnoreCase(registrationId) ? LoginProvider.GITHUB : LoginProvider.LOCAL;

    if (email == null) {
      String login = oauthUser.getAttribute("login");
      email = (login != null ? login : "unknown") + "@github.com";
    }

    User user;
    Optional<User> userOptional = userRepository.findByEmail(email);

    if (userOptional.isPresent()) {

      user = userOptional.get();

      if (user.getAuthProvider() == null || user.getAuthProvider() == LoginProvider.LOCAL) {
        user.setAuthProvider(provider);
        userRepository.save(user);
      }
    } else {
      user = new User();
      user.setEmail(email);

      if (fullName != null && fullName.contains(" ")) {
        String[] parts = fullName.split(" ", 2);
        user.setFirstName(parts[0]);
        user.setLastName(parts[1]);
      } else {
        user.setFirstName(fullName != null ? fullName : "User");
        user.setLastName("");
      }

      user.setPassword(null);
      user.setRole(UserRole.USER);
      user.setAuthProvider(provider);

      user = userRepository.save(user);
    }

    return new AuthenticationMetaData(user, oauthUser.getAttributes());
  }
}