package com.fooddelivery.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for User Service
 * 
 * This class demonstrates @RefreshScope usage for dynamic configuration refresh.
 * Properties can be updated in Config Server and refreshed without service restart.
 * 
 * To refresh: POST http://localhost:8081/actuator/refresh
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class ConfigProperties {

    private String secret;
    private long expiration;
    private long refreshExpiration;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}