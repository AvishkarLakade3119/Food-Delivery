package com.fooddelivery.payment.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void testCorsConfig() {
        // Use a REAL CorsRegistry instead of mocking it
        CorsConfig corsConfig = new CorsConfig();
        CorsRegistry registry = new CorsRegistry();

        // Test that addCorsMappings doesn't throw an exception
        corsConfig.addCorsMappings(registry);

        // Just verify it doesn't throw - the registration is internal
        assertNotNull(registry);
    }

    @Test
    void testCorsConfigInstantiation() {
        // Test that the config can be instantiated
        CorsConfig corsConfig = new CorsConfig();
        assertNotNull(corsConfig);
    }
}
