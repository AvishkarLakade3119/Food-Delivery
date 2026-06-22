package com.fooddelivery.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Spring Cloud Config Server Application
 * 
 * This service provides centralized configuration management for all microservices
 * in the Food Delivery platform. It serves configuration files from a Git repository
 * and integrates with Eureka for service discovery.
 * 
 * @author Food Delivery Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}