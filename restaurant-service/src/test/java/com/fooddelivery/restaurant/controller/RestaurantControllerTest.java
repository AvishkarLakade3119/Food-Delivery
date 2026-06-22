package com.fooddelivery.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.dto.AddressDto;
import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.messaging.RestaurantEventConsumer;
import com.fooddelivery.restaurant.messaging.RestaurantEventPublisher;
import com.fooddelivery.restaurant.security.JwtAuthenticationFilter;
import com.fooddelivery.restaurant.security.JwtTokenProvider;
import com.fooddelivery.restaurant.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private RabbitTemplate rabbitTemplate;
    
    @MockBean
    private RestaurantEventPublisher restaurantEventPublisher;
    
    @MockBean
    private RestaurantEventConsumer restaurantEventConsumer;

    @Autowired
    private ObjectMapper objectMapper;

    private Restaurant restaurant;
    private RestaurantRequest restaurantRequest;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St");
        restaurant.setPhone("+1234567890");
        restaurant.setIsActive(true);

        restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Test Restaurant");
        restaurantRequest.setCuisine("Italian");
        AddressDto addressDto = new AddressDto("123 Test St", "New York", "NY", "10001", "USA");
        restaurantRequest.setAddress(addressDto);
        restaurantRequest.setPhone("+1234567890");
        restaurantRequest.setEmail("test@restaurant.com");
    }

    @Test
    void createRestaurant_ShouldReturnCreated() throws Exception {
        when(restaurantService.createRestaurant(any(RestaurantRequest.class))).thenReturn(restaurant);

        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.cuisineType").value("Italian"));

        verify(restaurantService).createRestaurant(any(RestaurantRequest.class));
    }

    @Test
    void getAllRestaurants_ShouldReturnList() throws Exception {
        List<Restaurant> restaurants = Arrays.asList(restaurant);
        when(restaurantService.getAllActiveRestaurants()).thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));

        verify(restaurantService).getAllActiveRestaurants();
    }

    @Test
    void getRestaurantById_ShouldReturnRestaurant() throws Exception {
        when(restaurantService.getRestaurantById(1L)).thenReturn(restaurant);

        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Restaurant"));

        verify(restaurantService).getRestaurantById(1L);
    }

    @Test
    void getRestaurantsByCuisine_ShouldReturnList() throws Exception {
        List<Restaurant> restaurants = Arrays.asList(restaurant);
        when(restaurantService.getRestaurantsByCuisine("Italian")).thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants/cuisine/Italian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cuisineType").value("Italian"));

        verify(restaurantService).getRestaurantsByCuisine("Italian");
    }

    @Test
    void searchRestaurants_ShouldReturnList() throws Exception {
        List<Restaurant> restaurants = Arrays.asList(restaurant);
        when(restaurantService.searchRestaurants("Test")).thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants/search")
                .param("query", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));

        verify(restaurantService).searchRestaurants("Test");
    }

    @Test
    void updateRestaurant_ShouldReturnUpdated() throws Exception {
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setId(1L);
        updatedRestaurant.setName("Updated Restaurant");
        updatedRestaurant.setCuisineType("Italian");

        when(restaurantService.updateRestaurant(eq(1L), any(RestaurantRequest.class))).thenReturn(updatedRestaurant);

        restaurantRequest.setName("Updated Restaurant");

        mockMvc.perform(put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Restaurant"));

        verify(restaurantService).updateRestaurant(eq(1L), any(RestaurantRequest.class));
    }

    @Test
    void deleteRestaurant_ShouldReturnNoContent() throws Exception {
        doNothing().when(restaurantService).deleteRestaurant(1L);

        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());

        verify(restaurantService).deleteRestaurant(1L);
    }

    @Test
    void activateRestaurant_ShouldReturnActivatedRestaurant() throws Exception {
        restaurant.setIsActive(true);
        when(restaurantService.activateRestaurant(1L)).thenReturn(restaurant);

        mockMvc.perform(put("/api/restaurants/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));

        verify(restaurantService).activateRestaurant(1L);
    }

    @Test
    void deactivateRestaurant_ShouldReturnDeactivatedRestaurant() throws Exception {
        restaurant.setIsActive(false);
        when(restaurantService.deactivateRestaurant(1L)).thenReturn(restaurant);

        mockMvc.perform(put("/api/restaurants/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        verify(restaurantService).deactivateRestaurant(1L);
    }
}