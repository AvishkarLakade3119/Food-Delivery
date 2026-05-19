package com.fooddelivery.payment;

import org.junit.jupiter.api.Test;

class PaymentServiceMainTest {

    @Test
    void mainMethodCoverage() {
        PaymentServiceApplication.main(new String[]{
            "--spring.datasource.url=jdbc:h2:mem:paymentmain;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
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