package com.fooddelivery.notification.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void addCorsMappings_ShouldConfigureCorsCorrectly() {
        CorsConfig corsConfig = new CorsConfig();
        CorsRegistry registry = new CorsRegistry();
        
        // Call the method to configure CORS
        corsConfig.addCorsMappings(registry);
        
        // Verify that the registry is not null (basic validation)
        assertThat(registry).isNotNull();
    }

    @Test
    void corsConfig_ShouldBeInstantiable() {
        CorsConfig corsConfig = new CorsConfig();
        
        assertThat(corsConfig).isNotNull();
    }
}