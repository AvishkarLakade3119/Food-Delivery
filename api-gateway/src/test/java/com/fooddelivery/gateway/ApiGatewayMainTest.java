package com.fooddelivery.gateway;

import org.junit.jupiter.api.Test;

class ApiGatewayMainTest {
    @Test
    void mainMethodCoverage() {
        ApiGatewayApplication.main(new String[]{
            "--eureka.client.enabled=false",
            "--spring.cloud.discovery.enabled=false",
            "--spring.cloud.gateway.discovery.locator.enabled=false",
            "--spring.cloud.discovery.reactive.enabled=false",
            "--server.port=0"
        });
    }
}