package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(RestaurantRequest restaurantRequest) {
        Restaurant restaurant = convertRequestToEntity(restaurantRequest);
        // Force ID to null to ensure new ID generation (prevent duplicate ID errors)
        restaurant.setId(null);
        return restaurantRepository.save(restaurant);
    }

    /**
     * Converts RestaurantRequest DTO to Restaurant entity
     * Handles nested objects by converting them to strings
     */
    private Restaurant convertRequestToEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();

        // Basic fields
        restaurant.setName(request.getName());
        restaurant.setCuisineType(request.getCuisine());
        restaurant.setPhone(request.getPhone());
        restaurant.setRating(request.getRating());
        restaurant.setIsActive(request.getIsActive());

        // Convert nested address object to string
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress().toFormattedString());
        }

        return restaurant;
    }
    
    public List<Restaurant> getAllActiveRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }
    
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }
    
    public List<Restaurant> getRestaurantsByCuisine(String cuisineType) {
        return restaurantRepository.findByCuisineTypeIgnoreCase(cuisineType);
    }
    
    public List<Restaurant> searchRestaurants(String query) {
        List<Restaurant> nameResults = restaurantRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(query);
        List<Restaurant> addressResults = restaurantRepository.findByAddressContainingIgnoreCaseAndIsActiveTrue(query);
        
        return Stream.concat(nameResults.stream(), addressResults.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public Restaurant updateRestaurant(Long id, RestaurantRequest restaurantRequest) {
        Restaurant restaurant = getRestaurantById(id);

        // Update basic fields
        if (restaurantRequest.getName() != null) {
            restaurant.setName(restaurantRequest.getName());
        }
        if (restaurantRequest.getCuisine() != null) {
            restaurant.setCuisineType(restaurantRequest.getCuisine());
        }
        if (restaurantRequest.getPhone() != null) {
            restaurant.setPhone(restaurantRequest.getPhone());
        }
        if (restaurantRequest.getRating() != null) {
            restaurant.setRating(restaurantRequest.getRating());
        }
        if (restaurantRequest.getIsActive() != null) {
            restaurant.setIsActive(restaurantRequest.getIsActive());
        }

        // Convert nested address object to string
        if (restaurantRequest.getAddress() != null) {
            restaurant.setAddress(restaurantRequest.getAddress().toFormattedString());
        }

        return restaurantRepository.save(restaurant);
    }
    
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }
    
    public Restaurant activateRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setIsActive(true);
        return restaurantRepository.save(restaurant);
    }
    
    public Restaurant deactivateRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setIsActive(false);
        return restaurantRepository.save(restaurant);
    }
}