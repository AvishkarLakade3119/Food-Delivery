package com.fooddelivery.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.gateway.discovery.locator.enabled=false",
        "spring.cloud.discovery.reactive.enabled=false"
})
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // without any configuration errors or missing dependencies
    }
}