package com.example.backend.config;

import com.example.backend.security.AuthTokenFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig instance;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private AuthTokenFilter authTokenFilter;

    @Test
    @DisplayName("Should return password encoder successfully")
    void testPasswordEncoder() {
        PasswordEncoder result = instance.passwordEncoder();
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should create security filter chain successfully")
    void testSecurityFilterChain() throws Exception {
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(AuthTokenFilter.class), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(httpSecurity);

        DefaultSecurityFilterChain mockChain = mock(DefaultSecurityFilterChain.class);
        when(httpSecurity.build()).thenReturn(mockChain);

        SecurityFilterChain result = instance.securityFilterChain(httpSecurity);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should return cors configuration source successfully")
    void testCorsConfigurationSource() {
        CorsConfigurationSource result = instance.corsConfigurationSource();
        assertThat(result).isNotNull();
    }
}