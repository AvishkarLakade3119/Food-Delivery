package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private MenuItemService menuItemService;

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
    void findAll_ShouldReturnAllMenuItems() {
        List<MenuItem> menuItems = Arrays.asList(menuItem);
        when(menuItemRepository.findAll()).thenReturn(menuItems);

        List<MenuItem> result = menuItemService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Pizza");
        verify(menuItemRepository).findAll();
    }

    @Test
    void findById_ShouldReturnMenuItem() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        Optional<MenuItem> result = menuItemService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Pizza");
        verify(menuItemRepository).findById(1L);
    }

    @Test
    void findById_NotFound_ShouldReturnEmpty() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<MenuItem> result = menuItemService.findById(1L);

        assertThat(result).isEmpty();
        verify(menuItemRepository).findById(1L);
    }

    @Test
    void save_ShouldSaveAndReturnMenuItem() {
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        MenuItem result = menuItemService.save(menuItem);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Pizza");
        verify(menuItemRepository).save(menuItem);
    }

    @Test
    void deleteById_ShouldDeleteMenuItem() {
        doNothing().when(menuItemRepository).deleteById(1L);

        menuItemService.deleteById(1L);

        verify(menuItemRepository).deleteById(1L);
    }

    // ===============================
    // Restaurant-specific menu item methods tests
    // ===============================

    /**
     * Test createMenuItem method with valid restaurant ID
     */
    @Test
    void createMenuItem_WithValidRestaurantId_ShouldCreateMenuItem() {
        when(restaurantService.getRestaurantById(1L)).thenReturn(restaurant);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setName("New Pizza");
        newMenuItem.setDescription("Delicious new pizza");
        newMenuItem.setPrice(BigDecimal.valueOf(15.99));
        newMenuItem.setIsAvailable(true);

        MenuItem result = menuItemService.createMenuItem(1L, newMenuItem);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Pizza");
        verify(restaurantService).getRestaurantById(1L);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    /**
     * Test getAvailableMenuItemsByRestaurant method
     */
    @Test
    void getAvailableMenuItemsByRestaurant_ShouldReturnAvailableItems() {
        List<MenuItem> availableItems = Arrays.asList(menuItem);
        when(menuItemRepository.findByRestaurant_IdAndIsAvailableTrue(1L)).thenReturn(availableItems);

        List<MenuItem> result = menuItemService.getAvailableMenuItemsByRestaurant(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsAvailable()).isTrue();
        verify(menuItemRepository).findByRestaurant_IdAndIsAvailableTrue(1L);
    }

    /**
     * Test getAllMenuItemsByRestaurant method
     */
    @Test
    void getAllMenuItemsByRestaurant_ShouldReturnAllItems() {
        List<MenuItem> allItems = Arrays.asList(menuItem);
        when(menuItemRepository.findByRestaurant_Id(1L)).thenReturn(allItems);

        List<MenuItem> result = menuItemService.getAllMenuItemsByRestaurant(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Pizza");
        verify(menuItemRepository).findByRestaurant_Id(1L);
    }

    /**
     * Test getMenuItemById method when item exists
     */
    @Test
    void getMenuItemById_WhenExists_ShouldReturnMenuItem() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        MenuItem result = menuItemService.getMenuItemById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Pizza");
        verify(menuItemRepository).findById(1L);
    }

    /**
     * Test getMenuItemById method when item not found - should throw exception
     */
    @Test
    void getMenuItemById_WhenNotFound_ShouldThrowException() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.getMenuItemById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Menu item not found with id: 1");

        verify(menuItemRepository).findById(1L);
    }

    /**
     * Test updateMenuItem method
     */
    @Test
    void updateMenuItem_ShouldUpdateAndReturnMenuItem() {
        MenuItem updatedItem = new MenuItem();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Pizza");
        updatedItem.setDescription("Updated description");
        updatedItem.setPrice(BigDecimal.valueOf(16.99));
        updatedItem.setIsAvailable(false);
        updatedItem.setRestaurant(restaurant);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(updatedItem);

        MenuItem menuItemDetails = new MenuItem();
        menuItemDetails.setName("Updated Pizza");
        menuItemDetails.setDescription("Updated description");
        menuItemDetails.setPrice(BigDecimal.valueOf(16.99));
        menuItemDetails.setIsAvailable(false);

        MenuItem result = menuItemService.updateMenuItem(1L, menuItemDetails);

        assertThat(result.getName()).isEqualTo("Updated Pizza");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(16.99));
        assertThat(result.getIsAvailable()).isFalse();
        verify(menuItemRepository).findById(1L);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    /**
     * Test deleteMenuItem method
     */
    @Test
    void deleteMenuItem_ShouldDeleteMenuItem() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        doNothing().when(menuItemRepository).delete(menuItem);

        menuItemService.deleteMenuItem(1L);

        verify(menuItemRepository).findById(1L);
        verify(menuItemRepository).delete(menuItem);
    }

    /**
     * Test toggleAvailability method
     */
    @Test
    void toggleAvailability_ShouldToggleAndReturnMenuItem() {
        menuItem.setIsAvailable(true);
        MenuItem toggledItem = new MenuItem();
        toggledItem.setId(1L);
        toggledItem.setIsAvailable(false);
        toggledItem.setName("Test Pizza");
        toggledItem.setRestaurant(restaurant);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(toggledItem);

        MenuItem result = menuItemService.toggleAvailability(1L);

        assertThat(result.getIsAvailable()).isFalse();
        verify(menuItemRepository).findById(1L);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    /**
     * Test searchMenuItems method
     */
    @Test
    void searchMenuItems_ShouldReturnMatchingItems() {
        List<MenuItem> searchResults = Arrays.asList(menuItem);
        when(menuItemRepository.findByNameContainingIgnoreCaseAndIsAvailableTrue("Pizza")).thenReturn(searchResults);

        List<MenuItem> result = menuItemService.searchMenuItems("Pizza");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Pizza");
        verify(menuItemRepository).findByNameContainingIgnoreCaseAndIsAvailableTrue("Pizza");
    }

    /**
     * Test toggleAvailability method when item is currently unavailable
     * This covers the missed branch in line 59 where getIsAvailable() returns false
     */
    @Test
    void toggleAvailability_WhenItemUnavailable_ShouldMakeAvailable() {
        // Set up menu item as unavailable
        menuItem.setIsAvailable(false);

        MenuItem toggledItem = new MenuItem();
        toggledItem.setId(1L);
        toggledItem.setIsAvailable(true); // Should become available
        toggledItem.setName("Test Pizza");
        toggledItem.setRestaurant(restaurant);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(toggledItem);

        MenuItem result = menuItemService.toggleAvailability(1L);

        assertThat(result.getIsAvailable()).isTrue();
        verify(menuItemRepository).findById(1L);
        verify(menuItemRepository).save(any(MenuItem.class));
    }
}