package com.fooddelivery.restaurant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for Restaurant creation/update requests that supports nested objects
 */
public class RestaurantRequest {
    
    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Cuisine type is required")
    private String cuisine;
    
    @Valid
    @NotNull(message = "Address is required")
    private AddressDto address;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid")
    private String phone;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String website;
    
    @Valid
    private OpeningHoursDto openingHours;
    
    @DecimalMin(value = "0.0", message = "Rating must be positive")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    private BigDecimal rating;
    
    private Boolean isActive = true;
    
    @DecimalMin(value = "0.0", message = "Delivery radius must be positive")
    private Double deliveryRadius;
    
    @DecimalMin(value = "0.0", message = "Minimum order amount must be positive")
    private BigDecimal minimumOrderAmount;
    
    @DecimalMin(value = "0.0", message = "Delivery fee must be positive")
    private BigDecimal deliveryFee;
    
    // Default constructor
    public RestaurantRequest() {}
    
    // Constructor with required fields
    public RestaurantRequest(String name, String cuisine, AddressDto address, String phone) {
        this.name = name;
        this.cuisine = cuisine;
        this.address = address;
        this.phone = phone;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCuisine() {
        return cuisine;
    }
    
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }
    
    public AddressDto getAddress() {
        return address;
    }
    
    public void setAddress(AddressDto address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public OpeningHoursDto getOpeningHours() {
        return openingHours;
    }
    
    public void setOpeningHours(OpeningHoursDto openingHours) {
        this.openingHours = openingHours;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Double getDeliveryRadius() {
        return deliveryRadius;
    }
    
    public void setDeliveryRadius(Double deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
    }
    
    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }
    
    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }
    
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }
    
    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    @Override
    public String toString() {
        return "RestaurantRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cuisine='" + cuisine + '\'' +
                ", address=" + address +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", openingHours=" + openingHours +
                ", rating=" + rating +
                ", isActive=" + isActive +
                ", deliveryRadius=" + deliveryRadius +
                ", minimumOrderAmount=" + minimumOrderAmount +
                ", deliveryFee=" + deliveryFee +
                '}';
    }
}