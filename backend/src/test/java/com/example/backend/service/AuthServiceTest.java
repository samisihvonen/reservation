package com.example.backend.service;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.AuthResponse;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        JwtUtils jwtUtils = new JwtUtils() {
            @Override
            public String generateToken(String email) {
                return "token-for-" + email;
            }
        };

        authService = new AuthService(userRepository, passwordEncoder, jwtUtils);
    }

    @Test
    @DisplayName("Should register successfully")
    void testRegister() {
        RegisterRequest request = new RegisterRequest("test@example.com", "Test User", "password123");
        User savedUser = new User("test@example.com", "encoded-password", "Test User");
        savedUser.setId(1L);

        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        AuthResponse result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getDisplayName()).isEqualTo("Test User");
        assertThat(result.getToken()).isEqualTo("token-for-test@example.com");
    }

    @Test
    @DisplayName("Should login successfully")
    void testLogin() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = new User("test@example.com", "encoded-password", "Test User");
        user.setId(1L);

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encoded-password")).willReturn(true);

        AuthResponse result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getToken()).isEqualTo("token-for-test@example.com");
    }
}
