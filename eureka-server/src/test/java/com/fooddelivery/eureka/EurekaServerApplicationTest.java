package com.fooddelivery.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false",
    "server.port=0"
})
class EurekaServerApplicationTest {
    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }
}