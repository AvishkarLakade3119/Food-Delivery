package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:4200",
        "http://localhost:8080",
        "http://localhost:8081"
}, allowCredentials = "true")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest) {
        // Force ID to null to ensure new ID generation (prevent client from sending conflicting IDs)
        Restaurant createdRestaurant = restaurantService.createRestaurant(restaurantRequest);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllActiveRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable("id") Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/cuisine/{cuisineType}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCuisine(@PathVariable("cuisineType") String cuisineType) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisine(cuisineType);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurants(@RequestParam("query") String query) {
        List<Restaurant> restaurants = restaurantService.searchRestaurants(query);
        return ResponseEntity.ok(restaurants);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable("id") Long id, @Valid @RequestBody RestaurantRequest restaurantRequest) {
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, restaurantRequest);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Restaurant> activateRestaurant(@PathVariable("id") Long id) {
        Restaurant restaurant = restaurantService.activateRestaurant(id);
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Restaurant> deactivateRestaurant(@PathVariable("id") Long id) {
        Restaurant restaurant = restaurantService.deactivateRestaurant(id);
        return ResponseEntity.ok(restaurant);
    }
}