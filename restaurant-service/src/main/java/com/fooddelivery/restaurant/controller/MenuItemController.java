package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {
    
    @Autowired
    private MenuItemService menuItemService;
    
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.findAll();
        return ResponseEntity.ok(menuItems);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable("id") Long id) {
        Optional<MenuItem> menuItem = menuItemService.findById(id);
        return menuItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable("id") Long id,
            @Valid @RequestBody MenuItem menuItem) {
        Optional<MenuItem> existingMenuItem = menuItemService.findById(id);
        return existingMenuItem.map(existing -> {
            menuItem.setId(id);
            // Preserve the restaurant relationship
            menuItem.setRestaurant(existing.getRestaurant());
            menuItem.setCreatedAt(existing.getCreatedAt());
            menuItem.setUpdatedAt(LocalDateTime.now());
            MenuItem updated = menuItemService.save(menuItem);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable("id") Long id) {
        Optional<MenuItem> menuItem = menuItemService.findById(id);
        if (menuItem.isPresent()) {
            menuItemService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}