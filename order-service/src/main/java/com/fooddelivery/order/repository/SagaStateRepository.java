package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SagaStateRepository extends JpaRepository<SagaState, Long> {
    Optional<SagaState> findBySagaId(String sagaId);

    Optional<SagaState> findByOrderId(Long orderId);
}