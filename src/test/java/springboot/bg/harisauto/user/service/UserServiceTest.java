package springboot.bg.harisauto.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import springboot.bg.harisauto.common.exception.UserPasswordDoesNotMatchException;
import springboot.bg.harisauto.event.UserRegisteredEvent;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.user.model.UserRole;
import springboot.bg.harisauto.user.repository.UserRepository;
import springboot.bg.harisauto.web.dto.ChangeUserPasswordRequest;
import springboot.bg.harisauto.web.dto.RegisterRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ApplicationEventPublisher publisher;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        publisher = mock(ApplicationEventPublisher.class);
        userService = new UserService(userRepository, passwordEncoder, publisher);
    }

    @Test
    void register_publishesEvent_andSaves_whenEmailNotExists() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Pass123!")).thenReturn("ENC");

        RegisterRequest req = new RegisterRequest();
        req.setEmail("a@b.com");
        req.setFirstName("A");
        req.setLastName("B");
        req.setPassword("Pass123!");
        req.setConfirmPassword("Pass123!");

        userService.register(req);

        verify(publisher, times(1)).publishEvent(any(UserRegisteredEvent.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void loadUserByUsername_whenMissing_throws() {
        when(userRepository.findByEmail("missing@x.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.loadUserByUsername("missing@x.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void changeUserPassword_withWrongCurrent_throws() {
        User u = new User();
        u.setEmail("x@y.com");
        u.setPassword("ENC");

        ChangeUserPasswordRequest req = new ChangeUserPasswordRequest();
        req.setCurrentPassword("wrong");
        req.setNewPassword("New123!");

        when(passwordEncoder.matches("wrong", "ENC")).thenReturn(false);

        assertThatThrownBy(() -> userService.changeUserPassword(u, req))
                .isInstanceOf(UserPasswordDoesNotMatchException.class)
                .hasMessageContaining("Invalid current password");
    }

    @Test
    void changeUserPassword_withSameNewPassword_throws() {
        User u = new User();
        u.setEmail("x@y.com");
        u.setPassword("ENC");

        ChangeUserPasswordRequest req = new ChangeUserPasswordRequest();
        req.setCurrentPassword("old");
        req.setNewPassword("same");

        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("same", "ENC")).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUserPassword(u, req))
                .isInstanceOf(UserPasswordDoesNotMatchException.class)
                .hasMessageContaining("New password cannot be the same");
    }
}
