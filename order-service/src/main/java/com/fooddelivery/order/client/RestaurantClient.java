package com.fooddelivery.order.client;

import com.fooddelivery.order.dto.MenuItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/restaurants/{restaurantId}/menu/{itemId}")
    MenuItemDto getMenuItem(@PathVariable("restaurantId") Long restaurantId, @PathVariable("itemId") Long itemId);
}