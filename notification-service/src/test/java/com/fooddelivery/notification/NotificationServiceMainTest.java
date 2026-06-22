package com.fooddelivery.notification;

import org.junit.jupiter.api.Test;

class NotificationServiceMainTest {
    
    @Test
    void mainMethodCoverage() {
        // Test the main method to achieve 100% coverage for the main class
        // Since notification-service uses JPA/database, we need to configure H2
        NotificationServiceApplication.main(new String[]{
            "--spring.datasource.url=jdbc:h2:mem:notifmain;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
            "--spring.datasource.driver-class-name=org.h2.Driver",
            "--spring.datasource.username=sa",
            "--spring.datasource.password=",
            "--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "--spring.jpa.hibernate.ddl-auto=create-drop",
            "--eureka.client.enabled=false",
            "--spring.cloud.discovery.enabled=false",
            "--server.port=0",
            "--jwt.secret=test-secret-key-for-unit-testing-must-be-long-enough-for-hs256-algorithm-food-delivery",
            "--jwt.expiration=86400000"
        });
    }
}