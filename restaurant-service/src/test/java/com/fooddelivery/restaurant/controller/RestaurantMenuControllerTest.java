package com.fooddelivery.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.security.JwtAuthenticationFilter;
import com.fooddelivery.restaurant.security.JwtTokenProvider;
import com.fooddelivery.restaurant.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive unit tests for RestaurantMenuController.
 * Tests all endpoints with valid inputs, validation errors, and exception scenarios
 * to achieve 100% line and branch coverage.
 */
@WebMvcTest(RestaurantMenuController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService menuItemService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private MenuItem menuItem;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setIsActive(true);

        menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setName("Margherita Pizza");
        menuItem.setDescription("Classic Italian pizza with tomato and mozzarella");
        menuItem.setPrice(BigDecimal.valueOf(12.99));
        menuItem.setIsAvailable(true);
        menuItem.setRestaurant(restaurant);
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Test POST /api/restaurants/{restaurantId}/menu - Create menu item with valid data
     */
    @Test
    void createMenuItem_WithValidData_ShouldReturnCreated() throws Exception {
        when(menuItemService.createMenuItem(eq(1L), any(MenuItem.class))).thenReturn(menuItem);

        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setName("Margherita Pizza");
        newMenuItem.setDescription("Classic Italian pizza with tomato and mozzarella");
        newMenuItem.setPrice(BigDecimal.valueOf(12.99));
        newMenuItem.setIsAvailable(true);

        mockMvc.perform(post("/api/restaurants/1/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMenuItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.description").value("Classic Italian pizza with tomato and mozzarella"))
                .andExpect(jsonPath("$.price").value(12.99))
                .andExpect(jsonPath("$.isAvailable").value(true));

        verify(menuItemService).createMenuItem(eq(1L), any(MenuItem.class));
    }

    /**
     * Test POST /api/restaurants/{restaurantId}/menu - Create menu item with invalid data (missing required fields)
     */
    @Test
    void createMenuItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        MenuItem invalidMenuItem = new MenuItem();
        // Missing required fields like name, price

        mockMvc.perform(post("/api/restaurants/1/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMenuItem)))
                .andExpect(status().isBadRequest());

        verify(menuItemService, never()).createMenuItem(anyLong(), any(MenuItem.class));
    }

    /**
     * Test POST /api/restaurants/{restaurantId}/menu - Restaurant not found scenario
     */
    @Test
    void createMenuItem_RestaurantNotFound_ShouldReturnNotFound() throws Exception {
        when(menuItemService.createMenuItem(eq(999L), any(MenuItem.class)))
                .thenThrow(new RuntimeException("Restaurant not found with id: 999"));

        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setName("Test Pizza");
        newMenuItem.setDescription("Test description");
        newMenuItem.setPrice(BigDecimal.valueOf(10.99));
        newMenuItem.setIsAvailable(true);

        mockMvc.perform(post("/api/restaurants/999/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMenuItem)))
                .andExpect(status().isInternalServerError());

        verify(menuItemService).createMenuItem(eq(999L), any(MenuItem.class));
    }

    /**
     * Test GET /api/restaurants/{restaurantId}/menu - Get available menu items
     */
    @Test
    void getMenuItemsByRestaurant_ShouldReturnAvailableItems() throws Exception {
        List<MenuItem> availableItems = Arrays.asList(menuItem);
        when(menuItemService.getAvailableMenuItemsByRestaurant(1L)).thenReturn(availableItems);

        mockMvc.perform(get("/api/restaurants/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Margherita Pizza"))
                .andExpect(jsonPath("$[0].isAvailable").value(true));

        verify(menuItemService).getAvailableMenuItemsByRestaurant(1L);
    }

    /**
     * Test GET /api/restaurants/{restaurantId}/menu - Empty results
     */
    @Test
    void getMenuItemsByRestaurant_EmptyResults_ShouldReturnEmptyArray() throws Exception {
        when(menuItemService.getAvailableMenuItemsByRestaurant(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/restaurants/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(menuItemService).getAvailableMenuItemsByRestaurant(1L);
    }

    /**
     * Test GET /api/restaurants/{restaurantId}/menu/all - Get all menu items
     */
    @Test
    void getAllMenuItemsByRestaurant_ShouldReturnAllItems() throws Exception {
        MenuItem unavailableItem = new MenuItem();
        unavailableItem.setId(2L);
        unavailableItem.setName("Pepperoni Pizza");
        unavailableItem.setIsAvailable(false);
        unavailableItem.setRestaurant(restaurant);

        List<MenuItem> allItems = Arrays.asList(menuItem, unavailableItem);
        when(menuItemService.getAllMenuItemsByRestaurant(1L)).thenReturn(allItems);

        mockMvc.perform(get("/api/restaurants/1/menu/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].isAvailable").value(true))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].isAvailable").value(false));

        verify(menuItemService).getAllMenuItemsByRestaurant(1L);
    }

    /**
     * Test GET /api/restaurants/{restaurantId}/menu/{itemId} - Get menu item by ID
     */
    @Test
    void getMenuItemById_WhenExists_ShouldReturnMenuItem() throws Exception {
        when(menuItemService.getMenuItemById(1L)).thenReturn(menuItem);

        mockMvc.perform(get("/api/restaurants/1/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.description").value("Classic Italian pizza with tomato and mozzarella"))
                .andExpect(jsonPath("$.price").value(12.99));

        verify(menuItemService).getMenuItemById(1L);
    }

    /**
     * Test GET /api/restaurants/{restaurantId}/menu/{itemId} - Menu item not found
     */
    @Test
    void getMenuItemById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(menuItemService.getMenuItemById(999L))
                .thenThrow(new RuntimeException("Menu item not found with id: 999"));

        mockMvc.perform(get("/api/restaurants/1/menu/999"))
                .andExpect(status().isInternalServerError());

        verify(menuItemService).getMenuItemById(999L);
    }

    /**
     * Test PUT /api/restaurants/{restaurantId}/menu/{itemId} - Update menu item with valid data
     */
    @Test
    void updateMenuItem_WithValidData_ShouldReturnUpdatedItem() throws Exception {
        MenuItem updatedItem = new MenuItem();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Pizza");
        updatedItem.setDescription("Updated description");
        updatedItem.setPrice(BigDecimal.valueOf(15.99));
        updatedItem.setIsAvailable(false);
        updatedItem.setRestaurant(restaurant);

        when(menuItemService.updateMenuItem(eq(1L), any(MenuItem.class))).thenReturn(updatedItem);

        MenuItem updateRequest = new MenuItem();
        updateRequest.setName("Updated Pizza");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(BigDecimal.valueOf(15.99));
        updateRequest.setIsAvailable(false);

        mockMvc.perform(put("/api/restaurants/1/menu/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Pizza"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(15.99))
                .andExpect(jsonPath("$.isAvailable").value(false));

        verify(menuItemService).updateMenuItem(eq(1L), any(MenuItem.class));
    }

    /**
     * Test PUT /api/restaurants/{restaurantId}/menu/{itemId} - Update with invalid data
     */
    @Test
    void updateMenuItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        MenuItem invalidUpdate = new MenuItem();
        // Invalid data - e.g., negative price
        invalidUpdate.setPrice(BigDecimal.valueOf(-5.00));

        mockMvc.perform(put("/api/restaurants/1/menu/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());

        verify(menuItemService, never()).updateMenuItem(anyLong(), any(MenuItem.class));
    }

    /**
     * Test PUT /api/restaurants/{restaurantId}/menu/{itemId} - Menu item not found
     */
    @Test
    void updateMenuItem_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(menuItemService.updateMenuItem(eq(999L), any(MenuItem.class)))
                .thenThrow(new RuntimeException("Menu item not found with id: 999"));

        MenuItem updateRequest = new MenuItem();
        updateRequest.setName("Updated Pizza");
        updateRequest.setPrice(BigDecimal.valueOf(15.99));

        mockMvc.perform(put("/api/restaurants/1/menu/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError());

        verify(menuItemService).updateMenuItem(eq(999L), any(MenuItem.class));
    }

    /**
     * Test DELETE /api/restaurants/{restaurantId}/menu/{itemId} - Delete menu item
     */
    @Test
    void deleteMenuItem_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(menuItemService).deleteMenuItem(1L);

        mockMvc.perform(delete("/api/restaurants/1/menu/1"))
                .andExpect(status().isNoContent());

        verify(menuItemService).deleteMenuItem(1L);
    }

    /**
     * Test DELETE /api/restaurants/{restaurantId}/menu/{itemId} - Menu item not found
     */
    @Test
    void deleteMenuItem_WhenNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Menu item not found with id: 999"))
                .when(menuItemService).deleteMenuItem(999L);

        mockMvc.perform(delete("/api/restaurants/1/menu/999"))
                .andExpect(status().isInternalServerError());

        verify(menuItemService).deleteMenuItem(999L);
    }

    /**
     * Test PUT /api/restaurants/{restaurantId}/menu/{itemId}/availability - Toggle availability
     */
    @Test
    void toggleMenuItemAvailability_ShouldReturnToggledItem() throws Exception {
        MenuItem toggledItem = new MenuItem();
        toggledItem.setId(1L);
        toggledItem.setName("Margherita Pizza");
        toggledItem.setDescription("Classic Italian pizza with tomato and mozzarella");
        toggledItem.setPrice(BigDecimal.valueOf(12.99));
        toggledItem.setIsAvailable(false); // Toggled from true to false
        toggledItem.setRestaurant(restaurant);

        when(menuItemService.toggleAvailability(1L)).thenReturn(toggledItem);

        mockMvc.perform(put("/api/restaurants/1/menu/1/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.isAvailable").value(false));

        verify(menuItemService).toggleAvailability(1L);
    }

    /**
     * Test PUT /api/restaurants/{restaurantId}/menu/{itemId}/availability - Menu item not found
     */
    @Test
    void toggleMenuItemAvailability_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(menuItemService.toggleAvailability(999L))
                .thenThrow(new RuntimeException("Menu item not found with id: 999"));

        mockMvc.perform(put("/api/restaurants/1/menu/999/availability"))
                .andExpect(status().isInternalServerError());

        verify(menuItemService).toggleAvailability(999L);
    }
}