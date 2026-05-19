package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for restaurant-specific menu operations.
 * Handles endpoints under /api/restaurants/{restaurantId}/menu
 * This controller is kept separate from MenuItemController to maintain
 * both restaurant-specific and direct menu item access patterns.
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu")
public class RestaurantMenuController {
    
    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@PathVariable("restaurantId") Long restaurantId, @Valid @RequestBody MenuItem menuItem) {
        MenuItem createdMenuItem = menuItemService.createMenuItem(restaurantId, menuItem);
        return new ResponseEntity<>(createdMenuItem, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenuItemsByRestaurant(@PathVariable("restaurantId") Long restaurantId) {
        List<MenuItem> menuItems = menuItemService.getAvailableMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItemsByRestaurant(@PathVariable("restaurantId") Long restaurantId) {
        List<MenuItem> menuItems = menuItemService.getAllMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable("restaurantId") Long restaurantId, @PathVariable("itemId") Long itemId) {
        MenuItem menuItem = menuItemService.getMenuItemById(itemId);
        return ResponseEntity.ok(menuItem);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable("restaurantId") Long restaurantId, @PathVariable("itemId") Long itemId, @Valid @RequestBody MenuItem menuItem) {
        MenuItem updatedMenuItem = menuItemService.updateMenuItem(itemId, menuItem);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable("restaurantId") Long restaurantId, @PathVariable("itemId") Long itemId) {
        menuItemService.deleteMenuItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{itemId}/availability")
    public ResponseEntity<MenuItem> toggleMenuItemAvailability(@PathVariable("restaurantId") Long restaurantId, @PathVariable("itemId") Long itemId) {
        MenuItem menuItem = menuItemService.toggleAvailability(itemId);
        return ResponseEntity.ok(menuItem);
    }
}