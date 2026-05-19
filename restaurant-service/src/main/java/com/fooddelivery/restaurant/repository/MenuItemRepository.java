package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // FIXED: Use restaurant.id navigation instead of restaurantId
    List<MenuItem> findByRestaurant_IdAndIsAvailableTrue(Long restaurantId);

    // FIXED: Use restaurant.id navigation instead of restaurantId
    List<MenuItem> findByRestaurant_Id(Long restaurantId);

    // Alternative explicit JPQL approach (more readable)
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true")
    List<MenuItem> findAvailableByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId")
    List<MenuItem> findAllByRestaurantId(@Param("restaurantId") Long restaurantId);

    List<MenuItem> findByNameContainingIgnoreCaseAndIsAvailableTrue(String name);
}