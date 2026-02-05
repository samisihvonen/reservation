package com.example.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class AdminServiceTest {

    @Test
    void shouldCreateInstance() {
        AdminService service = new AdminService();
        assertNotNull(service);
    }
}