package com.fooddelivery.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.dto.AddressDto;
import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RestaurantControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private RestaurantRequest restaurantRequest;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/restaurants";
        restaurantRepository.deleteAll();

        addressDto = new AddressDto();
        addressDto.setStreet("123 Test St");
        addressDto.setCity("Test City");
        addressDto.setState("Test State");
        addressDto.setZipCode("12345");
        addressDto.setCountry("Test Country");

        restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Test Restaurant");
        restaurantRequest.setCuisine("Italian");
        restaurantRequest.setAddress(addressDto);
        restaurantRequest.setPhone("+1234567890");
        restaurantRequest.setRating(BigDecimal.valueOf(4.5));
        restaurantRequest.setIsActive(true);
    }

    @Test
    void createRestaurant_ShouldReturnCreated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RestaurantRequest> request = new HttpEntity<>(restaurantRequest, headers);

        ResponseEntity<Restaurant> response = restTemplate.postForEntity(baseUrl, request, Restaurant.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Restaurant");
        assertThat(response.getBody().getCuisineType()).isEqualTo("Italian");
    }

    @Test
    void getAllRestaurants_ShouldReturnList() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St, Test City, Test State 12345, Test Country");
        restaurant.setPhone("+1234567890");
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurant.setIsActive(true);
        restaurantRepository.save(restaurant);

        ResponseEntity<Restaurant[]> response = restTemplate.getForEntity(baseUrl, Restaurant[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void getRestaurantById_ShouldReturnRestaurant() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St, Test City, Test State 12345, Test Country");
        restaurant.setPhone("+1234567890");
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurant.setIsActive(true);
        Restaurant saved = restaurantRepository.save(restaurant);

        ResponseEntity<Restaurant> response = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), Restaurant.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Restaurant");
    }

    @Test
    void getRestaurantsByCuisine_ShouldReturnList() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St, Test City, Test State 12345, Test Country");
        restaurant.setPhone("+1234567890");
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurant.setIsActive(true);
        restaurantRepository.save(restaurant);

        ResponseEntity<Restaurant[]> response = restTemplate.getForEntity(baseUrl + "/cuisine/Italian", Restaurant[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void searchRestaurants_ShouldReturnList() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St, Test City, Test State 12345, Test Country");
        restaurant.setPhone("+1234567890");
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurant.setIsActive(true);
        restaurantRepository.save(restaurant);

        ResponseEntity<Restaurant[]> response = restTemplate.getForEntity(baseUrl + "/search?query=Test", Restaurant[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateRestaurant_ShouldReturnUpdated() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St, Test City, Test State 12345, Test Country");
        restaurant.setPhone("+1234567890");
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurant.setIsActive(true);
        Restaurant saved = restaurantRepository.save(restaurant);

        restaurantRequest.setName("Updated Restaurant");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RestaurantRequest> request = new HttpEntity<>(restaurantRequest, headers);

        ResponseEntity<Restaurant> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                request,
                Restaurant.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Restaurant");
    }

    @Test
    void deleteRestaurant_ShouldReturnNoContent() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Italian");
        restaurant.setAddress("123 Test St, Test City, Test State 12345, Test Country");
        restaurant.setPhone("+1234567890");
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurant.setIsActive(true);
        Restaurant saved = restaurantRepository.save(restaurant);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}