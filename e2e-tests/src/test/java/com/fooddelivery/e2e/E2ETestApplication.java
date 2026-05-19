package com.fooddelivery.e2e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Test Application for End-to-End Tests
 * 
 * This application provides the necessary Spring Boot context for running
 * comprehensive E2E tests for the Food Delivery microservices platform.
 */
@SpringBootApplication
public class E2ETestApplication {

    public static void main(String[] args) {
        SpringApplication.run(E2ETestApplication.class, args);
    }

    @TestConfiguration
    static class E2ETestConfig {
        
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}