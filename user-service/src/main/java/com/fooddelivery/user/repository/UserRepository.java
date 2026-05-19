package com.fooddelivery.user.repository;

import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByRole(UserRole role);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByNameContainingIgnoreCase(String name);
}