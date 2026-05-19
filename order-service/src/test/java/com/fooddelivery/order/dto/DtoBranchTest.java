package com.fooddelivery.order.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Brand new test file to cover missed branches in OrderRequest.getDeliveryAddressAsString().
 * This file ONLY contains tests for DTO branches that were not covered by existing tests.
 */
class DtoBranchTest {

    /**
     * Covers line 109 FALSE path: if (sb.length() > 0) sb.append(", ");
     * When apartmentNumber is the FIRST field (no street), sb.length() == 0, so no comma is added.
     */
    @Test
    void getDeliveryAddressAsString_ApartmentNumberFirst_NoComma() {
        OrderRequest request = new OrderRequest();
        
        // Create map with ONLY apartmentNumber (no street)
        Map<String, Object> address = new HashMap<>();
        address.put("apartmentNumber", "Apt 5B");
        // No street, so sb.length() will be 0 when checking apartmentNumber
        
        request.setDeliveryAddress(address);
        
        String result = request.getDeliveryAddressAsString();
        
        // Should be just "Apt 5B" without leading comma
        assertEquals("Apt 5B", result);
    }

    /**
     * Covers line 114 FALSE path: if (sb.length() > 0) sb.append(", ");
     * When city is the FIRST field (no street, no apartmentNumber), sb.length() == 0.
     */
    @Test
    void getDeliveryAddressAsString_CityFirst_NoComma() {
        OrderRequest request = new OrderRequest();
        
        // Create map with ONLY city
        Map<String, Object> address = new HashMap<>();
        address.put("city", "New York");
        
        request.setDeliveryAddress(address);
        
        String result = request.getDeliveryAddressAsString();
        
        assertEquals("New York", result);
    }

    /**
     * Covers line 119 FALSE path: if (sb.length() > 0) sb.append(", ");
     * When state is the FIRST field, sb.length() == 0.
     */
    @Test
    void getDeliveryAddressAsString_StateFirst_NoComma() {
        OrderRequest request = new OrderRequest();
        
        // Create map with ONLY state
        Map<String, Object> address = new HashMap<>();
        address.put("state", "NY");
        
        request.setDeliveryAddress(address);
        
        String result = request.getDeliveryAddressAsString();
        
        assertEquals("NY", result);
    }

    /**
     * Covers line 124 FALSE path: if (sb.length() > 0) sb.append(" ");
     * When zipCode is the FIRST field, sb.length() == 0, so no space is added.
     */
    @Test
    void getDeliveryAddressAsString_ZipCodeFirst_NoSpace() {
        OrderRequest request = new OrderRequest();
        
        // Create map with ONLY zipCode
        Map<String, Object> address = new HashMap<>();
        address.put("zipCode", "10001");
        
        request.setDeliveryAddress(address);
        
        String result = request.getDeliveryAddressAsString();
        
        assertEquals("10001", result);
    }

    /**
     * Covers line 129 FALSE path: if (sb.length() > 0) sb.append(", ");
     * When country is the FIRST field, sb.length() == 0.
     */
    @Test
    void getDeliveryAddressAsString_CountryFirst_NoComma() {
        OrderRequest request = new OrderRequest();
        
        // Create map with ONLY country
        Map<String, Object> address = new HashMap<>();
        address.put("country", "USA");
        
        request.setDeliveryAddress(address);
        
        String result = request.getDeliveryAddressAsString();
        
        assertEquals("USA", result);
    }

    /**
     * Test with multiple fields to ensure TRUE paths still work.
     * This ensures we didn't break existing functionality.
     */
    @Test
    void getDeliveryAddressAsString_MultipleFields_FormattedCorrectly() {
        OrderRequest request = new OrderRequest();
        
        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("apartmentNumber", "Apt 5B");
        address.put("city", "New York");
        address.put("state", "NY");
        address.put("zipCode", "10001");
        address.put("country", "USA");
        
        request.setDeliveryAddress(address);
        
        String result = request.getDeliveryAddressAsString();
        
        // Should have commas between fields and space before zipCode
        assertEquals("123 Main St, Apt 5B, New York, NY 10001, USA", result);
    }

    /**
     * Test with null deliveryAddress.
     * Covers line 89-90: if (deliveryAddress == null) return "";
     */
    @Test
    void getDeliveryAddressAsString_NullAddress_ReturnsEmpty() {
        OrderRequest request = new OrderRequest();
        request.setDeliveryAddress(null);
        
        String result = request.getDeliveryAddressAsString();
        
        assertEquals("", result);
    }

    /**
     * Test with String deliveryAddress (not Map).
     * Covers line 94-95: if (deliveryAddress instanceof String) return (String) deliveryAddress;
     */
    @Test
    void getDeliveryAddressAsString_StringAddress_ReturnsAsIs() {
        OrderRequest request = new OrderRequest();
        request.setDeliveryAddress("123 Main St, New York, NY 10001");
        
        String result = request.getDeliveryAddressAsString();
        
        assertEquals("123 Main St, New York, NY 10001", result);
    }
}