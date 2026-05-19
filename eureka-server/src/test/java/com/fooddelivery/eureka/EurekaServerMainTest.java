package com.fooddelivery.eureka;

import org.junit.jupiter.api.Test;

class EurekaServerMainTest {
    @Test
    void mainMethodCoverage() {
        EurekaServerApplication.main(new String[]{
            "--eureka.client.register-with-eureka=false",
            "--eureka.client.fetch-registry=false",
            "--server.port=0"
        });
    }
}