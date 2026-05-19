package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    List<Restaurant> findByIsActiveTrue();
    
    List<Restaurant> findByCuisineTypeIgnoreCase(String cuisineType);
    
    List<Restaurant> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    List<Restaurant> findByAddressContainingIgnoreCaseAndIsActiveTrue(String address);
}