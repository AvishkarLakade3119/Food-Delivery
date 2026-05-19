package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems")
    List<Order> findAllWithItems();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.userId = :userId")
    List<Order> findByUserIdWithItems(@Param("userId") Long userId);

    List<Order> findByUserId(Long userId);

    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
}