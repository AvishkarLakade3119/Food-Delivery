package com.fooddelivery.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for Config Server Application
 * Verifies that the Spring context loads successfully
 */
@SpringBootTest
@ActiveProfiles("test")
class ConfigServerApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // with all required beans and configurations
    }
}