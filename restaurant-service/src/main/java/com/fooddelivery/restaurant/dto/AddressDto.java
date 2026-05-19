package com.fooddelivery.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for Address information in Restaurant requests
 */
public class AddressDto {
    
    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must not exceed 100 characters")
    private String street;
    
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;
    
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;
    
    @NotBlank(message = "Zip code is required")
    @Size(max = 10, message = "Zip code must not exceed 10 characters")
    private String zipCode;
    
    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    // Default constructor
    public AddressDto() {
    }

    // Constructor with all fields
    public AddressDto(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    // JsonCreator constructor for String deserialization
    @JsonCreator
    public AddressDto(String fullAddress) {
        this.street = fullAddress;
    }
    
    // Getters and Setters
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     * Converts the address to a formatted string
     * @return Formatted address string
     */
    public String toFormattedString() {
        return String.format("%s, %s, %s %s, %s", 
            street, city, state, zipCode, country);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (street != null) sb.append(street);
        if (city != null) sb.append(", ").append(city);
        if (state != null) sb.append(", ").append(state);
        if (zipCode != null) sb.append(" ").append(zipCode);
        return sb.toString();
    }
}