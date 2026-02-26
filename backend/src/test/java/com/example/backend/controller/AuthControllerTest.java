package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should register user successfully and return CREATED status with auth response")
    void register_ValidRequest_ReturnsCreatedWithAuthResponse() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("Test User");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("test-token");
        expectedResponse.setExpiresIn(3600L);

        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600L));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when register request is invalid")
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("short");
        request.setName("");

        // Act & Assert
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login user successfully and return OK status with auth response")
    void login_ValidRequest_ReturnsOkWithAuthResponse() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("test-token");
        expectedResponse.setExpiresIn(3600L);

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600L));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when login request is invalid")
    void login_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email");
        request.setPassword("");

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when login credentials are invalid")
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong-password");

        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return INTERNAL_SERVER_ERROR when service throws unexpected exception during register")
    void register_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("Test User");

        when(authService.register(any(RegisterRequest.class))).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return INTERNAL_SERVER_ERROR when service throws unexpected exception during login")
    void login_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return correct response entity for successful registration")
    void register_ReturnsCorrectResponseEntity() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("Test User");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("test-token");
        expectedResponse.setExpiresIn(3600L);

        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should return correct response entity for successful login")
    void login_ReturnsCorrectResponseEntity() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("test-token");
        expectedResponse.setExpiresIn(3600L);

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }
}