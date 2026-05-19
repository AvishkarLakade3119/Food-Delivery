package com.fooddelivery.user.service;

import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.exception.UserAlreadyExistsException;
import com.fooddelivery.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        logger.info("Attempting to create user with email: {}", user.getEmail());

        // Check for duplicate email
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Registration failed: User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException("email", user.getEmail());
        }

        // Check for duplicate username if provided
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
            if (existingUserByUsername.isPresent()) {
                logger.warn("Registration failed: User with username {} already exists", user.getUsername());
                throw new UserAlreadyExistsException("username", user.getUsername());
            }
        }

        try {
            User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            // Handle database-level constraint violations
            logger.error("Database constraint violation while creating user: {}", e.getMessage());
            if (e.getMessage().contains("email")) {
                throw new UserAlreadyExistsException("email", user.getEmail(), "User with this email already exists");
            } else if (e.getMessage().contains("username")) {
                throw new UserAlreadyExistsException("username", user.getUsername(), "User with this username already exists");
            } else {
                throw new UserAlreadyExistsException("User with the provided information already exists");
            }
        }
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        // Preserve created_at from existing record
        userDetails.setId(id);
        userDetails.setCreatedAt(existingUser.getCreatedAt());
        userDetails.setUpdatedAt(java.time.LocalDateTime.now());

        // Update only the fields that should change
        if (userDetails.getName() != null) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            existingUser.setPhone(userDetails.getPhone());
        }
        if (userDetails.getAddress() != null) {
            existingUser.setAddress(userDetails.getAddress());
        }
        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }
        if (userDetails.getUsername() != null) {
            existingUser.setUsername(userDetails.getUsername());
        }
        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
        }
        if (userDetails.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        }

        // Never update created_at - it's preserved automatically
        existingUser.setUpdatedAt(java.time.LocalDateTime.now());

        return userRepository.save(existingUser);
    }
    
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Check if a user exists by email
     *
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if a user exists by username
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}