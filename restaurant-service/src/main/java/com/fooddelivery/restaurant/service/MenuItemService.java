package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantService restaurantService;

    // Existing methods for restaurant-specific menu items
    public MenuItem createMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getAvailableMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurant_IdAndIsAvailableTrue(restaurantId);
    }

    public List<MenuItem> getAllMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurant_Id(restaurantId);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }

    public MenuItem updateMenuItem(Long id, MenuItem menuItemDetails) {
        MenuItem menuItem = getMenuItemById(id);

        menuItem.setName(menuItemDetails.getName());
        menuItem.setDescription(menuItemDetails.getDescription());
        menuItem.setPrice(menuItemDetails.getPrice());
        menuItem.setIsAvailable(menuItemDetails.getIsAvailable());

        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        MenuItem menuItem = getMenuItemById(id);
        menuItemRepository.delete(menuItem);
    }

    public MenuItem toggleAvailability(Long id) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> searchMenuItems(String query) {
        return menuItemRepository.findByNameContainingIgnoreCaseAndIsAvailableTrue(query);
    }

    // New methods for direct menu item access (needed for /api/menu-items endpoints)
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    public Optional<MenuItem> findById(Long id) {
        return menuItemRepository.findById(id);
    }

    public MenuItem save(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public void deleteById(Long id) {
        menuItemRepository.deleteById(id);
    }
}