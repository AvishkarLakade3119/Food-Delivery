package com.fooddelivery.order.service.resilience;

import com.fooddelivery.order.client.RestaurantClient;
import com.fooddelivery.order.dto.MenuItemDto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class ResilientRestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(ResilientRestaurantService.class);
    private static final String INSTANCE = "restaurantService";

    private final RestaurantClient restaurantClient;

    public ResilientRestaurantService(RestaurantClient restaurantClient) {
        this.restaurantClient = restaurantClient;
    }

    @CircuitBreaker(name = INSTANCE, fallbackMethod = "getMenuItemFallback")
    @Retry(name = INSTANCE)
    @TimeLimiter(name = INSTANCE)
    @Bulkhead(name = INSTANCE, type = Bulkhead.Type.SEMAPHORE)
    public CompletableFuture<MenuItemDto> getMenuItem(Long restaurantId, Long menuItemId) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Calling restaurant-service for menuItemId: {}", menuItemId);
            return restaurantClient.getMenuItem(restaurantId, menuItemId);
        });
    }

    // ===== FALLBACK =====
    public CompletableFuture<MenuItemDto> getMenuItemFallback(Long restaurantId, Long menuItemId, Throwable ex) {
        logger.error("Fallback for getMenuItem(restaurantId={}, menuItemId={}): {}",
                restaurantId, menuItemId, ex.getMessage());

        MenuItemDto fallback = new MenuItemDto();
        fallback.setId(menuItemId);
        fallback.setName("Item-" + menuItemId + " (cached)");
        fallback.setPrice(BigDecimal.valueOf(10.00));
        fallback.setIsAvailable(true);
        return CompletableFuture.completedFuture(fallback);
    }
}