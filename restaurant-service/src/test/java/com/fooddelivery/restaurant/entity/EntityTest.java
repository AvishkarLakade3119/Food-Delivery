package com.fooddelivery.restaurant.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for all Entity classes in the restaurant service.
 * Tests cover constructors, getters, setters, business logic methods, and lifecycle callbacks
 * to achieve 100% line and branch coverage.
 */
class EntityTest {

    // ===============================
    // MenuItem Entity Tests
    // ===============================

    /**
     * Test MenuItem default constructor creates object with default values
     */
    @Test
    void testMenuItem_NoArgsConstructor() {
        // Act
        MenuItem menuItem = new MenuItem();
        
        // Assert
        assertNotNull(menuItem);
        assertNull(menuItem.getId());
        assertNull(menuItem.getName());
        assertNull(menuItem.getDescription());
        assertNull(menuItem.getPrice());
        assertTrue(menuItem.getIsAvailable()); // Default value is true
        assertNull(menuItem.getRestaurant());
        assertNull(menuItem.getCreatedAt());
        assertNull(menuItem.getUpdatedAt());
    }

    /**
     * Test MenuItem constructor with required fields
     */
    @Test
    void testMenuItem_RequiredFieldsConstructor() {
        // Arrange
        String name = "Margherita Pizza";
        String description = "Classic pizza with tomato and mozzarella";
        BigDecimal price = new BigDecimal("15.99");
        Restaurant restaurant = new Restaurant("Pizza Palace", "123 Main St", "+1234567890", "Italian");
        
        // Act
        MenuItem menuItem = new MenuItem(name, description, price, restaurant);
        
        // Assert
        assertEquals(name, menuItem.getName());
        assertEquals(description, menuItem.getDescription());
        assertEquals(price, menuItem.getPrice());
        assertEquals(restaurant, menuItem.getRestaurant());
        assertNull(menuItem.getId());
        assertTrue(menuItem.getIsAvailable()); // Default value
        assertNull(menuItem.getCreatedAt());
        assertNull(menuItem.getUpdatedAt());
    }

    /**
     * Test MenuItem getters and setters work correctly
     */
    @Test
    void testMenuItem_GettersAndSetters() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        Long id = 1L;
        String name = "Pepperoni Pizza";
        String description = "Pizza with pepperoni and cheese";
        BigDecimal price = new BigDecimal("18.99");
        Boolean isAvailable = false;
        Restaurant restaurant = new Restaurant("Pizza Palace", "123 Main St", "+1234567890", "Italian");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusMinutes(5);
        
        // Act
        menuItem.setId(id);
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setIsAvailable(isAvailable);
        menuItem.setRestaurant(restaurant);
        menuItem.setCreatedAt(createdAt);
        menuItem.setUpdatedAt(updatedAt);
        
        // Assert
        assertEquals(id, menuItem.getId());
        assertEquals(name, menuItem.getName());
        assertEquals(description, menuItem.getDescription());
        assertEquals(price, menuItem.getPrice());
        assertEquals(isAvailable, menuItem.getIsAvailable());
        assertEquals(restaurant, menuItem.getRestaurant());
        assertEquals(createdAt, menuItem.getCreatedAt());
        assertEquals(updatedAt, menuItem.getUpdatedAt());
    }

    /**
     * Test MenuItem setters with null values
     */
    @Test
    void testMenuItem_SettersWithNullValues() {
        // Arrange
        MenuItem menuItem = new MenuItem("Pizza", "Description", new BigDecimal("10.00"), new Restaurant());
        
        // Act
        menuItem.setId(null);
        menuItem.setName(null);
        menuItem.setDescription(null);
        menuItem.setPrice(null);
        menuItem.setIsAvailable(null);
        menuItem.setRestaurant(null);
        menuItem.setCreatedAt(null);
        menuItem.setUpdatedAt(null);
        
        // Assert
        assertNull(menuItem.getId());
        assertNull(menuItem.getName());
        assertNull(menuItem.getDescription());
        assertNull(menuItem.getPrice());
        assertNull(menuItem.getIsAvailable());
        assertNull(menuItem.getRestaurant());
        assertNull(menuItem.getCreatedAt());
        assertNull(menuItem.getUpdatedAt());
    }

    /**
     * Test MenuItem getRestaurantId method returns restaurant ID when restaurant is set
     */
    @Test
    void testMenuItem_GetRestaurantId_WithRestaurant() {
        // Arrange
        Restaurant restaurant = new Restaurant("Pizza Palace", "123 Main St", "+1234567890", "Italian");
        restaurant.setId(5L);
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        
        // Act
        Long restaurantId = menuItem.getRestaurantId();
        
        // Assert
        assertEquals(5L, restaurantId);
    }

    /**
     * Test MenuItem getRestaurantId method returns null when restaurant is null
     */
    @Test
    void testMenuItem_GetRestaurantId_WithoutRestaurant() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(null);
        
        // Act
        Long restaurantId = menuItem.getRestaurantId();
        
        // Assert
        assertNull(restaurantId);
    }

    /**
     * Test MenuItem getRestaurantId method returns null when restaurant has no ID
     */
    @Test
    void testMenuItem_GetRestaurantId_RestaurantWithoutId() {
        // Arrange
        Restaurant restaurant = new Restaurant("Pizza Palace", "123 Main St", "+1234567890", "Italian");
        // restaurant.setId() not called, so ID is null
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        
        // Act
        Long restaurantId = menuItem.getRestaurantId();
        
        // Assert
        assertNull(restaurantId);
    }

    /**
     * Test MenuItem onCreate lifecycle method sets createdAt
     */
    @Test
    void testMenuItem_OnCreate_SetsCreatedAt() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        
        // Act
        menuItem.onCreate(); // Simulate @PrePersist
        
        // Assert
        assertNotNull(menuItem.getCreatedAt());
        assertTrue(menuItem.getCreatedAt().isAfter(beforeCreate));
    }

    /**
     * Test MenuItem onUpdate lifecycle method sets updatedAt
     */
    @Test
    void testMenuItem_OnUpdate_SetsUpdatedAt() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);
        
        // Act
        menuItem.onUpdate(); // Simulate @PreUpdate
        
        // Assert
        assertNotNull(menuItem.getUpdatedAt());
        assertTrue(menuItem.getUpdatedAt().isAfter(beforeUpdate));
    }

    // ===============================
    // Restaurant Entity Tests
    // ===============================

    /**
     * Test Restaurant default constructor creates object with default values
     */
    @Test
    void testRestaurant_NoArgsConstructor() {
        // Act
        Restaurant restaurant = new Restaurant();
        
        // Assert
        assertNotNull(restaurant);
        assertNull(restaurant.getId());
        assertNull(restaurant.getName());
        assertNull(restaurant.getAddress());
        assertNull(restaurant.getPhone());
        assertNull(restaurant.getCuisineType());
        assertEquals(BigDecimal.ZERO, restaurant.getRating()); // Default value
        assertTrue(restaurant.getIsActive()); // Default value is true
        assertNull(restaurant.getMenuItems());
        assertNull(restaurant.getCreatedAt());
        assertNull(restaurant.getUpdatedAt());
    }

    /**
     * Test Restaurant constructor with required fields
     */
    @Test
    void testRestaurant_RequiredFieldsConstructor() {
        // Arrange
        String name = "Pizza Palace";
        String address = "123 Main St, New York, NY 10001";
        String phone = "+1234567890";
        String cuisineType = "Italian";
        
        // Act
        Restaurant restaurant = new Restaurant(name, address, phone, cuisineType);
        
        // Assert
        assertEquals(name, restaurant.getName());
        assertEquals(address, restaurant.getAddress());
        assertEquals(phone, restaurant.getPhone());
        assertEquals(cuisineType, restaurant.getCuisineType());
        assertNull(restaurant.getId());
        assertEquals(BigDecimal.ZERO, restaurant.getRating()); // Default value
        assertTrue(restaurant.getIsActive()); // Default value
        assertNull(restaurant.getMenuItems());
        assertNull(restaurant.getCreatedAt());
        assertNull(restaurant.getUpdatedAt());
    }

    /**
     * Test Restaurant getters and setters work correctly
     */
    @Test
    void testRestaurant_GettersAndSetters() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        Long id = 1L;
        String name = "Burger Joint";
        String address = "456 Oak Ave, Los Angeles, CA 90210";
        String phone = "+1987654321";
        String cuisineType = "American";
        BigDecimal rating = new BigDecimal("4.5");
        Boolean isActive = false;
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Burger", "Delicious burger", new BigDecimal("12.99"), restaurant));
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusMinutes(5);
        
        // Act
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone(phone);
        restaurant.setCuisineType(cuisineType);
        restaurant.setRating(rating);
        restaurant.setIsActive(isActive);
        restaurant.setMenuItems(menuItems);
        restaurant.setCreatedAt(createdAt);
        restaurant.setUpdatedAt(updatedAt);
        
        // Assert
        assertEquals(id, restaurant.getId());
        assertEquals(name, restaurant.getName());
        assertEquals(address, restaurant.getAddress());
        assertEquals(phone, restaurant.getPhone());
        assertEquals(cuisineType, restaurant.getCuisineType());
        assertEquals(rating, restaurant.getRating());
        assertEquals(isActive, restaurant.getIsActive());
        assertEquals(menuItems, restaurant.getMenuItems());
        assertEquals(createdAt, restaurant.getCreatedAt());
        assertEquals(updatedAt, restaurant.getUpdatedAt());
    }

    /**
     * Test Restaurant setters with null values
     */
    @Test
    void testRestaurant_SettersWithNullValues() {
        // Arrange
        Restaurant restaurant = new Restaurant("Pizza Palace", "123 Main St", "+1234567890", "Italian");
        
        // Act
        restaurant.setId(null);
        restaurant.setName(null);
        restaurant.setAddress(null);
        restaurant.setPhone(null);
        restaurant.setCuisineType(null);
        restaurant.setRating(null);
        restaurant.setIsActive(null);
        restaurant.setMenuItems(null);
        restaurant.setCreatedAt(null);
        restaurant.setUpdatedAt(null);
        
        // Assert
        assertNull(restaurant.getId());
        assertNull(restaurant.getName());
        assertNull(restaurant.getAddress());
        assertNull(restaurant.getPhone());
        assertNull(restaurant.getCuisineType());
        assertNull(restaurant.getRating());
        assertNull(restaurant.getIsActive());
        assertNull(restaurant.getMenuItems());
        assertNull(restaurant.getCreatedAt());
        assertNull(restaurant.getUpdatedAt());
    }

    /**
     * Test Restaurant with empty menu items list
     */
    @Test
    void testRestaurant_EmptyMenuItems() {
        // Arrange
        Restaurant restaurant = new Restaurant("Pizza Palace", "123 Main St", "+1234567890", "Italian");
        List<MenuItem> emptyMenuItems = new ArrayList<>();
        
        // Act
        restaurant.setMenuItems(emptyMenuItems);
        
        // Assert
        assertNotNull(restaurant.getMenuItems());
        assertTrue(restaurant.getMenuItems().isEmpty());
    }

    /**
     * Test Restaurant with multiple menu items
     */
    @Test
    void testRestaurant_MultipleMenuItems() {
        // Arrange
        Restaurant restaurant = new Restaurant("Pizza Palace", "123 Main St", "+1234567890", "Italian");
        MenuItem pizza = new MenuItem("Margherita Pizza", "Classic pizza", new BigDecimal("15.99"), restaurant);
        MenuItem pasta = new MenuItem("Spaghetti Carbonara", "Creamy pasta", new BigDecimal("13.99"), restaurant);
        List<MenuItem> menuItems = Arrays.asList(pizza, pasta);
        
        // Act
        restaurant.setMenuItems(menuItems);
        
        // Assert
        assertNotNull(restaurant.getMenuItems());
        assertEquals(2, restaurant.getMenuItems().size());
        assertTrue(restaurant.getMenuItems().contains(pizza));
        assertTrue(restaurant.getMenuItems().contains(pasta));
    }

    /**
     * Test Restaurant onCreate lifecycle method sets createdAt
     */
    @Test
    void testRestaurant_OnCreate_SetsCreatedAt() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        
        // Act
        restaurant.onCreate(); // Simulate @PrePersist
        
        // Assert
        assertNotNull(restaurant.getCreatedAt());
        assertTrue(restaurant.getCreatedAt().isAfter(beforeCreate));
    }

    /**
     * Test Restaurant onUpdate lifecycle method sets updatedAt
     */
    @Test
    void testRestaurant_OnUpdate_SetsUpdatedAt() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);
        
        // Act
        restaurant.onUpdate(); // Simulate @PreUpdate
        
        // Assert
        assertNotNull(restaurant.getUpdatedAt());
        assertTrue(restaurant.getUpdatedAt().isAfter(beforeUpdate));
    }

    /**
     * Test Restaurant rating with zero value
     */
    @Test
    void testRestaurant_RatingZero() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        
        // Act
        restaurant.setRating(BigDecimal.ZERO);
        
        // Assert
        assertEquals(BigDecimal.ZERO, restaurant.getRating());
    }

    /**
     * Test Restaurant rating with decimal value
     */
    @Test
    void testRestaurant_RatingDecimal() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        BigDecimal rating = new BigDecimal("4.75");
        
        // Act
        restaurant.setRating(rating);
        
        // Assert
        assertEquals(rating, restaurant.getRating());
    }

    /**
     * Test Restaurant isActive boolean values
     */
    @Test
    void testRestaurant_IsActiveBooleanValues() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        
        // Test true
        restaurant.setIsActive(true);
        assertTrue(restaurant.getIsActive());
        
        // Test false
        restaurant.setIsActive(false);
        assertFalse(restaurant.getIsActive());
    }

    /**
     * Test MenuItem isAvailable boolean values
     */
    @Test
    void testMenuItem_IsAvailableBooleanValues() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        
        // Test true
        menuItem.setIsAvailable(true);
        assertTrue(menuItem.getIsAvailable());
        
        // Test false
        menuItem.setIsAvailable(false);
        assertFalse(menuItem.getIsAvailable());
    }

    /**
     * Test MenuItem price with zero value
     */
    @Test
    void testMenuItem_PriceZero() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        
        // Act
        menuItem.setPrice(BigDecimal.ZERO);
        
        // Assert
        assertEquals(BigDecimal.ZERO, menuItem.getPrice());
    }

    /**
     * Test MenuItem price with decimal value
     */
    @Test
    void testMenuItem_PriceDecimal() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        BigDecimal price = new BigDecimal("19.95");
        
        // Act
        menuItem.setPrice(price);
        
        // Assert
        assertEquals(price, menuItem.getPrice());
    }
}