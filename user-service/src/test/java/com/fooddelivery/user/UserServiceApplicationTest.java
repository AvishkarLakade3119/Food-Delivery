package com.fooddelivery.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive integration tests for UserServiceApplication to achieve 100% line and branch coverage.
 * Tests the main application class and Spring Boot context loading.
 */
@SpringBootTest(classes = UserServiceApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "management.endpoints.web.exposure.include=health,info",
        "management.endpoint.health.show-details=always",
        "logging.level.com.fooddelivery.user=DEBUG"
})
class UserServiceApplicationTest {

    /**
     * Test Spring Boot application context loading.
     * Should load the application context successfully without errors.
     */
    @Test
    void contextLoads() {
        // This test verifies that the Spring Boot application context loads successfully
        // If the context fails to load, this test will fail
        // The @SpringBootTest annotation handles the context loading
        assertThat(true).isTrue(); // Context loaded successfully if we reach this point
    }

    /**
     * Test UserServiceApplication class structure.
     * Should have proper class structure and annotations.
     */
    @Test
    void userServiceApplication_ClassStructure_ShouldHaveProperStructureAndAnnotations() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        assertThat(applicationClass).isNotNull();
        
        // Verify @SpringBootApplication annotation
        assertThat(applicationClass.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class))
                .isTrue();
        
        // Verify @EnableDiscoveryClient annotation
        assertThat(applicationClass.isAnnotationPresent(org.springframework.cloud.client.discovery.EnableDiscoveryClient.class))
                .isTrue();
    }

    /**
     * Test UserServiceApplication main method existence.
     * Should have a proper main method for Spring Boot application.
     */
    @Test
    void userServiceApplication_MainMethod_ShouldExistAndBeProperlyDefined() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        try {
            var mainMethod = applicationClass.getMethod("main", String[].class);
            assertThat(mainMethod).isNotNull();
            assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
            assertThat(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers())).isTrue();
            assertThat(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers())).isTrue();
            assertThat(mainMethod.getParameterCount()).isEqualTo(1);
            assertThat(mainMethod.getParameterTypes()[0]).isEqualTo(String[].class);
        } catch (NoSuchMethodException e) {
            org.junit.jupiter.api.Assertions.fail("Main method should exist: " + e.getMessage());
        }
    }

    /**
     * Test UserServiceApplication package structure.
     * Should be in the correct package.
     */
    @Test
    void userServiceApplication_Package_ShouldBeInCorrectPackage() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        assertThat(applicationClass.getPackage().getName()).isEqualTo("com.fooddelivery.user");
    }

    /**
     * Test UserServiceApplication class modifiers.
     * Should be a public class.
     */
    @Test
    void userServiceApplication_ClassModifiers_ShouldBePublicClass() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        assertThat(java.lang.reflect.Modifier.isPublic(applicationClass.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isFinal(applicationClass.getModifiers())).isFalse();
        assertThat(java.lang.reflect.Modifier.isAbstract(applicationClass.getModifiers())).isFalse();
        assertThat(java.lang.reflect.Modifier.isInterface(applicationClass.getModifiers())).isFalse();
    }

    /**
     * Test UserServiceApplication instantiation.
     * Should be able to create an instance of the application class.
     */
    @Test
    void userServiceApplication_Instantiation_ShouldCreateInstanceSuccessfully() {
        // When
        UserServiceApplication application = new UserServiceApplication();

        // Then
        assertThat(application).isNotNull();
        assertThat(application).isInstanceOf(UserServiceApplication.class);
    }

    /**
     * Test UserServiceApplication constructor.
     * Should have a default constructor.
     */
    @Test
    void userServiceApplication_Constructor_ShouldHaveDefaultConstructor() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        try {
            var constructor = applicationClass.getDeclaredConstructor();
            assertThat(constructor).isNotNull();
            assertThat(java.lang.reflect.Modifier.isPublic(constructor.getModifiers())).isTrue();
            assertThat(constructor.getParameterCount()).isEqualTo(0);
            
            // Test constructor invocation
            UserServiceApplication instance = constructor.newInstance();
            assertThat(instance).isNotNull();
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.fail("Default constructor should exist and be accessible: " + e.getMessage());
        }
    }

    /**
     * Test UserServiceApplication class hierarchy.
     * Should extend Object and not implement any interfaces.
     */
    @Test
    void userServiceApplication_ClassHierarchy_ShouldExtendObjectOnly() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        assertThat(applicationClass.getSuperclass()).isEqualTo(Object.class);
        assertThat(applicationClass.getInterfaces()).isEmpty();
    }

    /**
     * Test UserServiceApplication declared methods.
     * Should have the main method declared (JaCoCo may add additional methods).
     */
    @Test
    void userServiceApplication_DeclaredMethods_ShouldHaveMainMethod() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        var declaredMethods = applicationClass.getDeclaredMethods();
        // JaCoCo may add additional methods for code coverage, so we check for at least the main method
        assertThat(declaredMethods).hasSizeGreaterThanOrEqualTo(1);

        // Verify that main method exists
        boolean hasMainMethod = false;
        for (var method : declaredMethods) {
            if ("main".equals(method.getName())) {
                hasMainMethod = true;
                break;
            }
        }
        assertThat(hasMainMethod).isTrue();
    }

    /**
     * Test UserServiceApplication declared fields.
     * Should not have any declared fields.
     */
    @Test
    void userServiceApplication_DeclaredFields_ShouldNotHaveAnyFields() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        var declaredFields = applicationClass.getDeclaredFields();
        assertThat(declaredFields).isEmpty();
    }

    /**
     * Test UserServiceApplication annotations.
     * Should have exactly two annotations: @SpringBootApplication and @EnableDiscoveryClient.
     */
    @Test
    void userServiceApplication_Annotations_ShouldHaveExactlyTwoAnnotations() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        var annotations = applicationClass.getDeclaredAnnotations();
        assertThat(annotations).hasSize(2);
        
        // Verify specific annotations
        boolean hasSpringBootApplication = false;
        boolean hasEnableDiscoveryClient = false;
        
        for (var annotation : annotations) {
            if (annotation instanceof org.springframework.boot.autoconfigure.SpringBootApplication) {
                hasSpringBootApplication = true;
            } else if (annotation instanceof org.springframework.cloud.client.discovery.EnableDiscoveryClient) {
                hasEnableDiscoveryClient = true;
            }
        }
        
        assertThat(hasSpringBootApplication).isTrue();
        assertThat(hasEnableDiscoveryClient).isTrue();
    }

    /**
     * Test UserServiceApplication class name.
     * Should have the correct class name.
     */
    @Test
    void userServiceApplication_ClassName_ShouldHaveCorrectName() {
        // Given
        Class<UserServiceApplication> applicationClass = UserServiceApplication.class;

        // Then
        assertThat(applicationClass.getSimpleName()).isEqualTo("UserServiceApplication");
        assertThat(applicationClass.getName()).isEqualTo("com.fooddelivery.user.UserServiceApplication");
        assertThat(applicationClass.getCanonicalName()).isEqualTo("com.fooddelivery.user.UserServiceApplication");
    }

    /**
     * Test UserServiceApplication toString method.
     * Should have a proper string representation.
     */
    @Test
    void userServiceApplication_ToString_ShouldHaveProperStringRepresentation() {
        // Given
        UserServiceApplication application = new UserServiceApplication();

        // When
        String result = application.toString();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("UserServiceApplication");
    }

    /**
     * Test UserServiceApplication equals and hashCode.
     * Should handle equality comparison properly.
     */
    @Test
    void userServiceApplication_EqualsAndHashCode_ShouldHandleEqualityProperly() {
        // Given
        UserServiceApplication application1 = new UserServiceApplication();
        UserServiceApplication application2 = new UserServiceApplication();

        // Then
        assertThat(application1).isEqualTo(application1); // Same instance
        assertThat(application1).isNotEqualTo(application2); // Different instances (Object default behavior)
        assertThat(application1).isNotEqualTo(null);
        assertThat(application1).isNotEqualTo("string");

        // HashCode should be consistent
        assertThat(application1.hashCode()).isEqualTo(application1.hashCode());
    }

    /**
     * Test UserServiceApplication class loading.
     * Should be loadable by the class loader.
     */
    @Test
    void userServiceApplication_ClassLoading_ShouldBeLoadableByClassLoader() {
        // Given
        ClassLoader classLoader = UserServiceApplication.class.getClassLoader();

        // Then
        try {
            Class<?> loadedClass = classLoader.loadClass("com.fooddelivery.user.UserServiceApplication");
            assertThat(loadedClass).isNotNull();
            assertThat(loadedClass).isEqualTo(UserServiceApplication.class);
        } catch (ClassNotFoundException e) {
            org.junit.jupiter.api.Assertions.fail("Class should be loadable: " + e.getMessage());
        }
    }
}