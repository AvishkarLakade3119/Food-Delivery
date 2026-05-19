package com.fooddelivery.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:ordertest;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "server.port=0"
        }
)
class OrderServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully with H2
    }

    @Test
    void main() {
        // Test main method with H2 properties to avoid PostgreSQL connection
        OrderServiceApplication.main(new String[]{
                "--spring.datasource.url=jdbc:h2:mem:ordertest2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
                "--spring.datasource.driver-class-name=org.h2.Driver",
                "--spring.datasource.username=sa",
                "--spring.datasource.password=",
                "--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "--spring.jpa.hibernate.ddl-auto=create-drop",
                "--eureka.client.enabled=false",
                "--spring.cloud.discovery.enabled=false",
                "--server.port=0"
        });
    }
}