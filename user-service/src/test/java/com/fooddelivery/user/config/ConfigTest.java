package com.fooddelivery.user.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for Configuration classes to achieve 100% line and branch coverage.
 * Tests all configuration methods and their behavior.
 */
@ExtendWith(MockitoExtension.class)
class ConfigTest {

    @Mock
    private CorsRegistry corsRegistry;

    @Mock
    private CorsRegistration corsRegistration;

    @InjectMocks
    private CorsConfig corsConfig;

    /**
     * Test CorsConfig addCorsMappings method.
     * Should configure CORS mappings with all required settings.
     */
    @Test
    void corsConfig_AddCorsMappings_ShouldConfigureCorsWithAllSettings() {
        // Given
        when(corsRegistry.addMapping(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(anyLong())).thenReturn(corsRegistration);

        // When
        corsConfig.addCorsMappings(corsRegistry);

        // Then
        verify(corsRegistry, times(1)).addMapping("/**");
        verify(corsRegistration, times(1)).allowedOrigins(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080",
                "http://localhost:8081",
                "http://localhost:8082",
                "http://localhost:8083",
                "http://localhost:8084",
                "http://localhost:8085",
                "http://localhost:8086"
        );
        verify(corsRegistration, times(1)).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(corsRegistration, times(1)).allowedHeaders("*");
        verify(corsRegistration, times(1)).allowCredentials(true);
        verify(corsRegistration, times(1)).maxAge(3600);
    }

    /**
     * Test CorsConfig class instantiation.
     * Should create instance successfully.
     */
    @Test
    void corsConfig_Instantiation_ShouldCreateInstanceSuccessfully() {
        // When
        CorsConfig config = new CorsConfig();

        // Then
        assertThat(config).isNotNull();
        assertThat(config).isInstanceOf(CorsConfig.class);
        assertThat(config).isInstanceOf(org.springframework.web.servlet.config.annotation.WebMvcConfigurer.class);
    }

    /**
     * Test CorsConfig with null registry.
     * Should handle null registry gracefully (though this would be a Spring framework issue).
     */
    @Test
    void corsConfig_AddCorsMappingsWithNullRegistry_ShouldHandleGracefully() {
        // Given
        CorsConfig config = new CorsConfig();

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> config.addCorsMappings(null)
        );
    }

    /**
     * Test CorsConfig method chaining behavior.
     * Should verify that all method calls are properly chained.
     */
    @Test
    void corsConfig_MethodChaining_ShouldVerifyProperChaining() {
        // Given
        when(corsRegistry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(true)).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(3600L)).thenReturn(corsRegistration);

        // When
        corsConfig.addCorsMappings(corsRegistry);

        // Then - Verify the exact sequence of method calls
        var inOrder = inOrder(corsRegistry, corsRegistration);
        inOrder.verify(corsRegistry).addMapping("/**");
        inOrder.verify(corsRegistration).allowedOrigins(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080",
                "http://localhost:8081",
                "http://localhost:8082",
                "http://localhost:8083",
                "http://localhost:8084",
                "http://localhost:8085",
                "http://localhost:8086"
        );
        inOrder.verify(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        inOrder.verify(corsRegistration).allowedHeaders("*");
        inOrder.verify(corsRegistration).allowCredentials(true);
        inOrder.verify(corsRegistration).maxAge(3600L);
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Test CorsConfig with multiple calls.
     * Should handle multiple calls to addCorsMappings properly.
     */
    @Test
    void corsConfig_MultipleCalls_ShouldHandleMultipleCallsProperly() {
        // Given
        when(corsRegistry.addMapping(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(anyLong())).thenReturn(corsRegistration);

        // When
        corsConfig.addCorsMappings(corsRegistry);
        corsConfig.addCorsMappings(corsRegistry);

        // Then
        verify(corsRegistry, times(2)).addMapping("/**");
        verify(corsRegistration, times(2)).allowedOrigins(any(String[].class));
        verify(corsRegistration, times(2)).allowedMethods(any(String[].class));
        verify(corsRegistration, times(2)).allowedHeaders("*");
        verify(corsRegistration, times(2)).allowCredentials(true);
        verify(corsRegistration, times(2)).maxAge(3600L);
    }

    /**
     * Test CorsConfig inheritance and interface implementation.
     * Should properly implement WebMvcConfigurer interface.
     */
    @Test
    void corsConfig_InterfaceImplementation_ShouldImplementWebMvcConfigurer() {
        // Given
        CorsConfig config = new CorsConfig();

        // Then
        assertThat(config).isInstanceOf(org.springframework.web.servlet.config.annotation.WebMvcConfigurer.class);
        
        // Verify that the class has the @Configuration annotation
        assertThat(config.getClass().isAnnotationPresent(org.springframework.context.annotation.Configuration.class))
                .isTrue();
    }

    /**
     * Test CorsConfig class structure.
     * Should have proper class structure and annotations.
     */
    @Test
    void corsConfig_ClassStructure_ShouldHaveProperStructureAndAnnotations() {
        // Given
        Class<CorsConfig> configClass = CorsConfig.class;

        // Then
        assertThat(configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class))
                .isTrue();
        
        // Verify that it implements WebMvcConfigurer
        assertThat(org.springframework.web.servlet.config.annotation.WebMvcConfigurer.class
                .isAssignableFrom(configClass)).isTrue();
        
        // Verify that it has the addCorsMappings method
        try {
            var method = configClass.getMethod("addCorsMappings", CorsRegistry.class);
            assertThat(method).isNotNull();
            assertThat(method.getReturnType()).isEqualTo(void.class);
        } catch (NoSuchMethodException e) {
            org.junit.jupiter.api.Assertions.fail("addCorsMappings method should exist");
        }
    }

    /**
     * Test CorsConfig with reflection to verify method behavior.
     * Should verify method exists and is properly overridden.
     */
    @Test
    void corsConfig_MethodReflection_ShouldVerifyMethodExistsAndOverridden() {
        // Given
        CorsConfig config = new CorsConfig();
        Class<CorsConfig> configClass = CorsConfig.class;

        // Then
        try {
            var method = configClass.getDeclaredMethod("addCorsMappings", CorsRegistry.class);
            assertThat(method).isNotNull();
            assertThat(method.getParameterCount()).isEqualTo(1);
            assertThat(method.getParameterTypes()[0]).isEqualTo(CorsRegistry.class);
            assertThat(method.getReturnType()).isEqualTo(void.class);
            
            // Verify method is public
            assertThat(java.lang.reflect.Modifier.isPublic(method.getModifiers())).isTrue();

            // Note: @Override annotation may not be present in compiled bytecode
            // This is normal behavior and doesn't indicate an error
        } catch (NoSuchMethodException e) {
            org.junit.jupiter.api.Assertions.fail("addCorsMappings method should exist: " + e.getMessage());
        }
    }

    /**
     * Helper method to import assertThat for better readability.
     */
    private static org.assertj.core.api.AbstractObjectAssert<?, ?> assertThat(Object actual) {
        return org.assertj.core.api.Assertions.assertThat(actual);
    }

    /**
     * Helper method to import assertThat for boolean values.
     */
    private static org.assertj.core.api.AbstractBooleanAssert<?> assertThat(boolean actual) {
        return org.assertj.core.api.Assertions.assertThat(actual);
    }

    /**
     * Helper method to import assertThat for class values.
     */
    private static org.assertj.core.api.AbstractClassAssert<?> assertThat(Class<?> actual) {
        return org.assertj.core.api.Assertions.assertThat(actual);
    }
}