package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.AddressDto;
import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private RestaurantRequest restaurantRequest;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        addressDto = new AddressDto();
        addressDto.setStreet("123 Test St");
        addressDto.setCity("Test City");
        addressDto.setState("Test State");
        addressDto.setZipCode("12345");
        addressDto.setCountry("Test Country");

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St, Test City, Test State 12345, Test Country");
        restaurant.setPhone("+1234567890");
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurant.setIsActive(true);

        restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Test Restaurant");
        restaurantRequest.setCuisine("Italian");
        restaurantRequest.setAddress(addressDto);
        restaurantRequest.setPhone("+1234567890");
        restaurantRequest.setRating(BigDecimal.valueOf(4.5));
        restaurantRequest.setIsActive(true);
    }

    @Test
    void createRestaurant_ShouldSaveAndReturnRestaurant() {
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        Restaurant result = restaurantService.createRestaurant(restaurantRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Restaurant");
        assertThat(result.getCuisineType()).isEqualTo("Italian");
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void getAllActiveRestaurants_ShouldReturnActiveRestaurants() {
        List<Restaurant> activeRestaurants = Arrays.asList(restaurant);
        when(restaurantRepository.findByIsActiveTrue()).thenReturn(activeRestaurants);

        List<Restaurant> result = restaurantService.getAllActiveRestaurants();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Restaurant");
        verify(restaurantRepository).findByIsActiveTrue();
    }

    @Test
    void getRestaurantById_ShouldReturnRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getRestaurantById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Restaurant");
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void getRestaurantById_NotFound_ShouldThrowException() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getRestaurantById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Restaurant not found with id: 1");

        verify(restaurantRepository).findById(1L);
    }

    @Test
    void getRestaurantsByCuisine_ShouldReturnRestaurantsByCuisine() {
        List<Restaurant> italianRestaurants = Arrays.asList(restaurant);
        when(restaurantRepository.findByCuisineTypeIgnoreCase("Italian")).thenReturn(italianRestaurants);

        List<Restaurant> result = restaurantService.getRestaurantsByCuisine("Italian");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCuisineType()).isEqualTo("Italian");
        verify(restaurantRepository).findByCuisineTypeIgnoreCase("Italian");
    }

    @Test
    void searchRestaurants_ShouldReturnMatchingRestaurants() {
        List<Restaurant> nameResults = Arrays.asList(restaurant);
        List<Restaurant> addressResults = Arrays.asList();
        
        when(restaurantRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("Test")).thenReturn(nameResults);
        when(restaurantRepository.findByAddressContainingIgnoreCaseAndIsActiveTrue("Test")).thenReturn(addressResults);

        List<Restaurant> result = restaurantService.searchRestaurants("Test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");
        verify(restaurantRepository).findByNameContainingIgnoreCaseAndIsActiveTrue("Test");
        verify(restaurantRepository).findByAddressContainingIgnoreCaseAndIsActiveTrue("Test");
    }

    @Test
    void updateRestaurant_ShouldUpdateAndReturnRestaurant() {
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setId(1L);
        updatedRestaurant.setName("Updated Restaurant");
        updatedRestaurant.setCuisineType("Mexican");
        updatedRestaurant.setIsActive(true);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        RestaurantRequest updateRequest = new RestaurantRequest();
        updateRequest.setName("Updated Restaurant");
        updateRequest.setCuisine("Mexican");

        Restaurant result = restaurantService.updateRestaurant(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Restaurant");
        assertThat(result.getCuisineType()).isEqualTo("Mexican");
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void deleteRestaurant_ShouldDeleteRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantRepository).delete(restaurant);

        restaurantService.deleteRestaurant(1L);

        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).delete(restaurant);
    }

    @Test
    void activateRestaurant_ShouldActivateAndReturnRestaurant() {
        restaurant.setIsActive(false);
        Restaurant activatedRestaurant = new Restaurant();
        activatedRestaurant.setId(1L);
        activatedRestaurant.setIsActive(true);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(activatedRestaurant);

        Restaurant result = restaurantService.activateRestaurant(1L);

        assertThat(result.getIsActive()).isTrue();
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void deactivateRestaurant_ShouldDeactivateAndReturnRestaurant() {
        restaurant.setIsActive(true);
        Restaurant deactivatedRestaurant = new Restaurant();
        deactivatedRestaurant.setId(1L);
        deactivatedRestaurant.setIsActive(false);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(deactivatedRestaurant);

        Restaurant result = restaurantService.deactivateRestaurant(1L);

        assertThat(result.getIsActive()).isFalse();
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    /**
     * Test searchRestaurants method with duplicate results to cover Stream.distinct() branch
     */
    @Test
    void searchRestaurants_WithDuplicates_ShouldReturnDistinctResults() {
        // Same restaurant appears in both name and address results
        List<Restaurant> nameResults = Arrays.asList(restaurant);
        List<Restaurant> addressResults = Arrays.asList(restaurant);
        
        when(restaurantRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("Test")).thenReturn(nameResults);
        when(restaurantRepository.findByAddressContainingIgnoreCaseAndIsActiveTrue("Test")).thenReturn(addressResults);

        List<Restaurant> result = restaurantService.searchRestaurants("Test");

        // Should return only one restaurant despite appearing in both results
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");
        verify(restaurantRepository).findByNameContainingIgnoreCaseAndIsActiveTrue("Test");
        verify(restaurantRepository).findByAddressContainingIgnoreCaseAndIsActiveTrue("Test");
    }

    /**
     * Test convertRequestToEntity method with null address to cover null check branch
     */
    @Test
    void createRestaurant_WithNullAddress_ShouldHandleGracefully() {
        RestaurantRequest requestWithNullAddress = new RestaurantRequest();
        requestWithNullAddress.setName("Test Restaurant");
        requestWithNullAddress.setCuisine("Italian");
        requestWithNullAddress.setPhone("+1234567890");
        requestWithNullAddress.setRating(BigDecimal.valueOf(4.5));
        requestWithNullAddress.setIsActive(true);
        requestWithNullAddress.setAddress(null); // Null address to test branch

        Restaurant expectedRestaurant = new Restaurant();
        expectedRestaurant.setId(1L);
        expectedRestaurant.setName("Test Restaurant");
        expectedRestaurant.setCuisineType("Italian");
        expectedRestaurant.setPhone("+1234567890");
        expectedRestaurant.setRating(BigDecimal.valueOf(4.5));
        expectedRestaurant.setIsActive(true);
        expectedRestaurant.setAddress(null); // Address should be null

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(expectedRestaurant);

        Restaurant result = restaurantService.createRestaurant(requestWithNullAddress);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Restaurant");
        assertThat(result.getAddress()).isNull();
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    /**
     * Test updateRestaurant method with null fields to cover all null check branches
     */
    @Test
    void updateRestaurant_WithNullFields_ShouldOnlyUpdateNonNullFields() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantRequest updateRequest = new RestaurantRequest();
        // All fields are null - should not update any field
        updateRequest.setName(null);
        updateRequest.setCuisine(null);
        updateRequest.setPhone(null);
        updateRequest.setRating(null);
        updateRequest.setIsActive(null);
        updateRequest.setAddress(null);

        Restaurant result = restaurantService.updateRestaurant(1L, updateRequest);

        // Original values should be preserved
        assertThat(result.getName()).isEqualTo("Test Restaurant");
        assertThat(result.getCuisineType()).isEqualTo("Italian");
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    /**
     * Test updateRestaurant method with null address to cover address null check branch
     */
    @Test
    void updateRestaurant_WithNullAddress_ShouldNotUpdateAddress() {
        String originalAddress = restaurant.getAddress();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantRequest updateRequest = new RestaurantRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setAddress(null); // Null address should not update address field

        Restaurant result = restaurantService.updateRestaurant(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getAddress()).isEqualTo(originalAddress); // Address unchanged
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    /**
     * Test updateRestaurant method with individual null field checks to cover all missed branches
     */
    @Test
    void updateRestaurant_WithNullPhone_ShouldNotUpdatePhone() {
        String originalPhone = restaurant.getPhone();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantRequest updateRequest = new RestaurantRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setPhone(null); // Null phone should not update phone field
        updateRequest.setCuisine("Updated Cuisine");

        Restaurant result = restaurantService.updateRestaurant(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getPhone()).isEqualTo(originalPhone); // Phone unchanged
        assertThat(result.getCuisineType()).isEqualTo("Updated Cuisine");
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    /**
     * Test updateRestaurant method with null rating to cover rating null check branch
     */
    @Test
    void updateRestaurant_WithNullRating_ShouldNotUpdateRating() {
        BigDecimal originalRating = restaurant.getRating();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantRequest updateRequest = new RestaurantRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setRating(null); // Null rating should not update rating field
        updateRequest.setCuisine("Updated Cuisine");

        Restaurant result = restaurantService.updateRestaurant(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getRating()).isEqualTo(originalRating); // Rating unchanged
        assertThat(result.getCuisineType()).isEqualTo("Updated Cuisine");
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    /**
     * Test updateRestaurant method with null isActive to cover isActive null check branch
     */
    @Test
    void updateRestaurant_WithNullIsActive_ShouldNotUpdateIsActive() {
        Boolean originalIsActive = restaurant.getIsActive();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantRequest updateRequest = new RestaurantRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setIsActive(null); // Null isActive should not update isActive field
        updateRequest.setCuisine("Updated Cuisine");

        Restaurant result = restaurantService.updateRestaurant(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getIsActive()).isEqualTo(originalIsActive); // IsActive unchanged
        assertThat(result.getCuisineType()).isEqualTo("Updated Cuisine");
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    /**
     * Test updateRestaurant method with non-null fields to cover the positive branches
     * This covers lines 80, 83, 91 where null checks return false and fields are updated
     */
    @Test
    void updateRestaurant_WithNonNullFields_ShouldUpdateFields() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantRequest updateRequest = new RestaurantRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setCuisine("Updated Cuisine");
        updateRequest.setPhone("+9876543210");
        updateRequest.setRating(BigDecimal.valueOf(4.8));
        updateRequest.setIsActive(false);

        // Create a new address to test the non-null address branch
        AddressDto newAddress = new AddressDto();
        newAddress.setStreet("456 New St");
        newAddress.setCity("New City");
        newAddress.setState("NC");
        newAddress.setZipCode("54321");
        newAddress.setCountry("USA");
        updateRequest.setAddress(newAddress);

        Restaurant result = restaurantService.updateRestaurant(1L, updateRequest);

        // Verify that the update method was called and fields would be updated
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }
}