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
    @DisplayName("Should register user successfully and return CREATED status")
    void register_ValidRequest_ReturnsCreatedWithAuthResponse() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test User");
        AuthResponse expectedResponse = new AuthResponse("token123", "test@example.com", "Test User");

        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when register request is invalid")
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest("", "short", "");

        // Act & Assert
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login user successfully and return OK status")
    void login_ValidRequest_ReturnsOkWithAuthResponse() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResponse expectedResponse = new AuthResponse("token123", "test@example.com", "Test User");

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when login request is invalid")
    void login_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest("", "short");

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when login credentials are invalid")
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("wrong@example.com", "wrongpassword");
        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return INTERNAL_SERVER_ERROR when service throws exception during registration")
    void register_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test User");
        when(authService.register(any(RegisterRequest.class))).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return INTERNAL_SERVER_ERROR when service throws exception during login")
    void login_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return correct status and response for register when using direct method call")
    void register_DirectMethodCall_ReturnsCorrectResponse() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test User");
        AuthResponse expectedResponse = new AuthResponse("token123", "test@example.com", "Test User");

        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should return correct status and response for login when using direct method call")
    void login_DirectMethodCall_ReturnsCorrectResponse() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResponse expectedResponse = new AuthResponse("token123", "test@example.com", "Test User");

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }
}