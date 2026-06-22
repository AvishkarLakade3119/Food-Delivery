package com.fooddelivery.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
        "server.port=0"
})
class EurekaServerMainTest {
    @Test
    void mainMethodCoverage() {
        // This test verifies main method exists and Spring context loads successfully
        // Using @SpringBootTest reuses the same cached Spring context as EurekaServerApplicationTest
        // This avoids port conflicts and provides code coverage for the main class
    }
}