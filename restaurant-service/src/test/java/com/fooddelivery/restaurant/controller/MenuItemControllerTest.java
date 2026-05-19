package com.fooddelivery.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService menuItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private MenuItem menuItem;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setName("Test Pizza");
        menuItem.setDescription("Delicious test pizza");
        menuItem.setPrice(BigDecimal.valueOf(12.99));
        menuItem.setIsAvailable(true);
        menuItem.setRestaurant(restaurant);
        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllMenuItems_ShouldReturnList() throws Exception {
        List<MenuItem> menuItems = Arrays.asList(menuItem);
        when(menuItemService.findAll()).thenReturn(menuItems);

        mockMvc.perform(get("/api/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Pizza"))
                .andExpect(jsonPath("$[0].price").value(12.99));

        verify(menuItemService).findAll();
    }

    @Test
    void getMenuItemById_ShouldReturnMenuItem() throws Exception {
        when(menuItemService.findById(1L)).thenReturn(Optional.of(menuItem));

        mockMvc.perform(get("/api/menu-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Pizza"))
                .andExpect(jsonPath("$.description").value("Delicious test pizza"));

        verify(menuItemService).findById(1L);
    }

    @Test
    void getMenuItemById_NotFound_ShouldReturn404() throws Exception {
        when(menuItemService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/menu-items/1"))
                .andExpect(status().isNotFound());

        verify(menuItemService).findById(1L);
    }

    @Test
    void updateMenuItem_ShouldReturnUpdatedMenuItem() throws Exception {
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setId(1L);
        updatedMenuItem.setName("Updated Pizza");
        updatedMenuItem.setDescription("Updated description");
        updatedMenuItem.setPrice(BigDecimal.valueOf(15.99));
        updatedMenuItem.setIsAvailable(true);
        updatedMenuItem.setRestaurant(restaurant);
        updatedMenuItem.setCreatedAt(menuItem.getCreatedAt());
        updatedMenuItem.setUpdatedAt(LocalDateTime.now());

        when(menuItemService.findById(1L)).thenReturn(Optional.of(menuItem));
        when(menuItemService.save(any(MenuItem.class))).thenReturn(updatedMenuItem);

        MenuItem updateRequest = new MenuItem();
        updateRequest.setName("Updated Pizza");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(BigDecimal.valueOf(15.99));
        updateRequest.setIsAvailable(true);

        mockMvc.perform(put("/api/menu-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Pizza"))
                .andExpect(jsonPath("$.price").value(15.99));

        verify(menuItemService).findById(1L);
        verify(menuItemService).save(any(MenuItem.class));
    }

    @Test
    void updateMenuItem_NotFound_ShouldReturn404() throws Exception {
        when(menuItemService.findById(1L)).thenReturn(Optional.empty());

        MenuItem updateRequest = new MenuItem();
        updateRequest.setName("Updated Pizza");
        updateRequest.setPrice(BigDecimal.valueOf(15.99));

        mockMvc.perform(put("/api/menu-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(menuItemService).findById(1L);
        verify(menuItemService, never()).save(any(MenuItem.class));
    }

    @Test
    void deleteMenuItem_ShouldReturnNoContent() throws Exception {
        when(menuItemService.findById(1L)).thenReturn(Optional.of(menuItem));
        doNothing().when(menuItemService).deleteById(1L);

        mockMvc.perform(delete("/api/menu-items/1"))
                .andExpect(status().isNoContent());

        verify(menuItemService).findById(1L);
        verify(menuItemService).deleteById(1L);
    }

    @Test
    void deleteMenuItem_NotFound_ShouldReturn404() throws Exception {
        when(menuItemService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/menu-items/1"))
                .andExpect(status().isNotFound());

        verify(menuItemService).findById(1L);
        verify(menuItemService, never()).deleteById(1L);
    }
}