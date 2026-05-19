package com.fooddelivery.restaurant.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for all DTO classes in the restaurant service.
 * Tests cover constructors, getters, setters, equals, hashCode, toString methods,
 * and business logic methods to achieve 100% line and branch coverage.
 */
class DtoTest {

    // ===============================
    // AddressDto Tests
    // ===============================

    /**
     * Test AddressDto default constructor creates object with null fields
     */
    @Test
    void testAddressDto_NoArgsConstructor() {
        // Act
        AddressDto dto = new AddressDto();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getStreet());
        assertNull(dto.getCity());
        assertNull(dto.getState());
        assertNull(dto.getZipCode());
        assertNull(dto.getCountry());
    }

    /**
     * Test AddressDto all-args constructor sets all fields correctly
     */
    @Test
    void testAddressDto_AllArgsConstructor() {
        // Arrange
        String street = "123 Main St";
        String city = "New York";
        String state = "NY";
        String zipCode = "10001";
        String country = "USA";

        // Act
        AddressDto dto = new AddressDto(street, city, state, zipCode, country);

        // Assert
        assertEquals(street, dto.getStreet());
        assertEquals(city, dto.getCity());
        assertEquals(state, dto.getState());
        assertEquals(zipCode, dto.getZipCode());
        assertEquals(country, dto.getCountry());
    }

    /**
     * Test AddressDto JsonCreator constructor with full address string
     */
    @Test
    void testAddressDto_JsonCreatorConstructor() {
        // Arrange
        String fullAddress = "123 Main St, New York, NY 10001, USA";

        // Act
        AddressDto dto = new AddressDto(fullAddress);

        // Assert
        assertEquals(fullAddress, dto.getStreet());
        assertNull(dto.getCity());
        assertNull(dto.getState());
        assertNull(dto.getZipCode());
        assertNull(dto.getCountry());
    }

    /**
     * Test AddressDto getters and setters work correctly
     */
    @Test
    void testAddressDto_GettersAndSetters() {
        // Arrange
        AddressDto dto = new AddressDto();
        String street = "456 Oak Ave";
        String city = "Los Angeles";
        String state = "CA";
        String zipCode = "90210";
        String country = "USA";

        // Act
        dto.setStreet(street);
        dto.setCity(city);
        dto.setState(state);
        dto.setZipCode(zipCode);
        dto.setCountry(country);

        // Assert
        assertEquals(street, dto.getStreet());
        assertEquals(city, dto.getCity());
        assertEquals(state, dto.getState());
        assertEquals(zipCode, dto.getZipCode());
        assertEquals(country, dto.getCountry());
    }

    /**
     * Test AddressDto setters with null values
     */
    @Test
    void testAddressDto_SettersWithNullValues() {
        // Arrange
        AddressDto dto = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");

        // Act
        dto.setStreet(null);
        dto.setCity(null);
        dto.setState(null);
        dto.setZipCode(null);
        dto.setCountry(null);

        // Assert
        assertNull(dto.getStreet());
        assertNull(dto.getCity());
        assertNull(dto.getState());
        assertNull(dto.getZipCode());
        assertNull(dto.getCountry());
    }

    /**
     * Test AddressDto toFormattedString method with all fields populated
     */
    @Test
    void testAddressDto_ToFormattedString_AllFields() {
        // Arrange
        AddressDto dto = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");

        // Act
        String result = dto.toFormattedString();

        // Assert
        assertEquals("123 Main St, New York, NY 10001, USA", result);
    }

    /**
     * Test AddressDto toFormattedString method with null fields
     */
    @Test
    void testAddressDto_ToFormattedString_WithNulls() {
        // Arrange
        AddressDto dto = new AddressDto();
        dto.setStreet("123 Main St");
        dto.setCity("New York");
        // state, zipCode, country are null

        // Act
        String result = dto.toFormattedString();

        // Assert
        assertEquals("123 Main St, New York, null null, null", result);
    }

    /**
     * Test AddressDto toString method with all fields populated
     */
    @Test
    void testAddressDto_ToString_AllFields() {
        // Arrange
        AddressDto dto = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");

        // Act
        String result = dto.toString();

        // Assert
        assertEquals("123 Main St, New York, NY 10001", result);
    }

    /**
     * Test AddressDto toString method with only street
     */
    @Test
    void testAddressDto_ToString_OnlyStreet() {
        // Arrange
        AddressDto dto = new AddressDto();
        dto.setStreet("123 Main St");

        // Act
        String result = dto.toString();

        // Assert
        assertEquals("123 Main St", result);
    }

    /**
     * Test AddressDto toString method with null fields
     */
    @Test
    void testAddressDto_ToString_AllNulls() {
        // Arrange
        AddressDto dto = new AddressDto();

        // Act
        String result = dto.toString();

        // Assert
        assertEquals("", result);
    }

    /**
     * Test AddressDto toString method with partial fields
     */
    @Test
    void testAddressDto_ToString_PartialFields() {
        // Arrange
        AddressDto dto = new AddressDto();
        dto.setStreet("123 Main St");
        dto.setCity("New York");
        dto.setZipCode("10001");
        // state is null

        // Act
        String result = dto.toString();

        // Assert
        assertEquals("123 Main St, New York 10001", result);
    }

    // ===============================
    // OpeningHoursDto Tests
    // ===============================

    /**
     * Test OpeningHoursDto default constructor creates object with null fields
     */
    @Test
    void testOpeningHoursDto_NoArgsConstructor() {
        // Act
        OpeningHoursDto dto = new OpeningHoursDto();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getMonday());
        assertNull(dto.getTuesday());
        assertNull(dto.getWednesday());
        assertNull(dto.getThursday());
        assertNull(dto.getFriday());
        assertNull(dto.getSaturday());
        assertNull(dto.getSunday());
    }

    /**
     * Test OpeningHoursDto all-args constructor sets all fields correctly
     */
    @Test
    void testOpeningHoursDto_AllArgsConstructor() {
        // Arrange
        String monday = "09:00-17:00";
        String tuesday = "09:00-17:00";
        String wednesday = "09:00-17:00";
        String thursday = "09:00-17:00";
        String friday = "09:00-17:00";
        String saturday = "10:00-16:00";
        String sunday = "CLOSED";

        // Act
        OpeningHoursDto dto = new OpeningHoursDto(monday, tuesday, wednesday, thursday, friday, saturday, sunday);

        // Assert
        assertEquals(monday, dto.getMonday());
        assertEquals(tuesday, dto.getTuesday());
        assertEquals(wednesday, dto.getWednesday());
        assertEquals(thursday, dto.getThursday());
        assertEquals(friday, dto.getFriday());
        assertEquals(saturday, dto.getSaturday());
        assertEquals(sunday, dto.getSunday());
    }

    /**
     * Test OpeningHoursDto getters and setters work correctly
     */
    @Test
    void testOpeningHoursDto_GettersAndSetters() {
        // Arrange
        OpeningHoursDto dto = new OpeningHoursDto();
        String monday = "08:00-18:00";
        String tuesday = "08:00-18:00";
        String wednesday = "08:00-18:00";
        String thursday = "08:00-18:00";
        String friday = "08:00-20:00";
        String saturday = "09:00-21:00";
        String sunday = "10:00-17:00";

        // Act
        dto.setMonday(monday);
        dto.setTuesday(tuesday);
        dto.setWednesday(wednesday);
        dto.setThursday(thursday);
        dto.setFriday(friday);
        dto.setSaturday(saturday);
        dto.setSunday(sunday);

        // Assert
        assertEquals(monday, dto.getMonday());
        assertEquals(tuesday, dto.getTuesday());
        assertEquals(wednesday, dto.getWednesday());
        assertEquals(thursday, dto.getThursday());
        assertEquals(friday, dto.getFriday());
        assertEquals(saturday, dto.getSaturday());
        assertEquals(sunday, dto.getSunday());
    }

    /**
     * Test OpeningHoursDto setters with null values
     */
    @Test
    void testOpeningHoursDto_SettersWithNullValues() {
        // Arrange
        OpeningHoursDto dto = new OpeningHoursDto("09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "10:00-16:00", "CLOSED");

        // Act
        dto.setMonday(null);
        dto.setTuesday(null);
        dto.setWednesday(null);
        dto.setThursday(null);
        dto.setFriday(null);
        dto.setSaturday(null);
        dto.setSunday(null);

        // Assert
        assertNull(dto.getMonday());
        assertNull(dto.getTuesday());
        assertNull(dto.getWednesday());
        assertNull(dto.getThursday());
        assertNull(dto.getFriday());
        assertNull(dto.getSaturday());
        assertNull(dto.getSunday());
    }

    /**
     * Test OpeningHoursDto toFormattedString method with all fields populated
     */
    @Test
    void testOpeningHoursDto_ToFormattedString_AllFields() {
        // Arrange
        OpeningHoursDto dto = new OpeningHoursDto("09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "10:00-16:00", "CLOSED");

        // Act
        String result = dto.toFormattedString();

        // Assert
        assertEquals("Mon: 09:00-17:00, Tue: 09:00-17:00, Wed: 09:00-17:00, Thu: 09:00-17:00, Fri: 09:00-17:00, Sat: 10:00-16:00, Sun: CLOSED", result);
    }

    /**
     * Test OpeningHoursDto toFormattedString method with null fields
     */
    @Test
    void testOpeningHoursDto_ToFormattedString_WithNulls() {
        // Arrange
        OpeningHoursDto dto = new OpeningHoursDto();
        dto.setMonday("09:00-17:00");
        dto.setFriday("09:00-20:00");
        // Other days are null

        // Act
        String result = dto.toFormattedString();

        // Assert
        assertEquals("Mon: 09:00-17:00, Tue: CLOSED, Wed: CLOSED, Thu: CLOSED, Fri: 09:00-20:00, Sat: CLOSED, Sun: CLOSED", result);
    }

    /**
     * Test OpeningHoursDto toString method
     */
    @Test
    void testOpeningHoursDto_ToString() {
        // Arrange
        OpeningHoursDto dto = new OpeningHoursDto("09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "10:00-16:00", "CLOSED");

        // Act
        String result = dto.toString();

        // Assert
        String expected = "OpeningHoursDto{monday='09:00-17:00', tuesday='09:00-17:00', wednesday='09:00-17:00', thursday='09:00-17:00', friday='09:00-17:00', saturday='10:00-16:00', sunday='CLOSED'}";
        assertEquals(expected, result);
    }

    /**
     * Test OpeningHoursDto toString method with null values
     */
    @Test
    void testOpeningHoursDto_ToString_WithNulls() {
        // Arrange
        OpeningHoursDto dto = new OpeningHoursDto();

        // Act
        String result = dto.toString();

        // Assert
        String expected = "OpeningHoursDto{monday='null', tuesday='null', wednesday='null', thursday='null', friday='null', saturday='null', sunday='null'}";
        assertEquals(expected, result);
    }

    // ===============================
    // RestaurantRequest Tests
    // ===============================

    /**
     * Test RestaurantRequest default constructor creates object with null fields and default isActive
     */
    @Test
    void testRestaurantRequest_NoArgsConstructor() {
        // Act
        RestaurantRequest request = new RestaurantRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getName());
        assertNull(request.getDescription());
        assertNull(request.getCuisine());
        assertNull(request.getAddress());
        assertNull(request.getPhone());
        assertNull(request.getEmail());
        assertNull(request.getWebsite());
        assertNull(request.getOpeningHours());
        assertNull(request.getRating());
        assertTrue(request.getIsActive()); // Default value is true
        assertNull(request.getDeliveryRadius());
        assertNull(request.getMinimumOrderAmount());
        assertNull(request.getDeliveryFee());
    }

    /**
     * Test RestaurantRequest constructor with required fields
     */
    @Test
    void testRestaurantRequest_RequiredFieldsConstructor() {
        // Arrange
        String name = "Pizza Palace";
        String cuisine = "Italian";
        AddressDto address = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");
        String phone = "+1234567890";

        // Act
        RestaurantRequest request = new RestaurantRequest(name, cuisine, address, phone);

        // Assert
        assertEquals(name, request.getName());
        assertEquals(cuisine, request.getCuisine());
        assertEquals(address, request.getAddress());
        assertEquals(phone, request.getPhone());
        assertNull(request.getDescription());
        assertNull(request.getEmail());
        assertNull(request.getWebsite());
        assertNull(request.getOpeningHours());
        assertNull(request.getRating());
        assertTrue(request.getIsActive()); // Default value is true
        assertNull(request.getDeliveryRadius());
        assertNull(request.getMinimumOrderAmount());
        assertNull(request.getDeliveryFee());
    }

    /**
     * Test RestaurantRequest getters and setters work correctly
     */
    @Test
    void testRestaurantRequest_GettersAndSetters() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();
        String name = "Burger Joint";
        String description = "Best burgers in town";
        String cuisine = "American";
        AddressDto address = new AddressDto("456 Oak Ave", "Los Angeles", "CA", "90210", "USA");
        String phone = "+1987654321";
        String email = "info@burgerjoint.com";
        String website = "www.burgerjoint.com";
        OpeningHoursDto openingHours = new OpeningHoursDto("10:00-22:00", "10:00-22:00", "10:00-22:00", "10:00-22:00", "10:00-23:00", "11:00-23:00", "11:00-21:00");
        BigDecimal rating = new BigDecimal("4.5");
        Boolean isActive = false;
        Double deliveryRadius = 5.0;
        BigDecimal minimumOrderAmount = new BigDecimal("15.00");
        BigDecimal deliveryFee = new BigDecimal("2.99");

        // Act
        request.setName(name);
        request.setDescription(description);
        request.setCuisine(cuisine);
        request.setAddress(address);
        request.setPhone(phone);
        request.setEmail(email);
        request.setWebsite(website);
        request.setOpeningHours(openingHours);
        request.setRating(rating);
        request.setIsActive(isActive);
        request.setDeliveryRadius(deliveryRadius);
        request.setMinimumOrderAmount(minimumOrderAmount);
        request.setDeliveryFee(deliveryFee);

        // Assert
        assertEquals(name, request.getName());
        assertEquals(description, request.getDescription());
        assertEquals(cuisine, request.getCuisine());
        assertEquals(address, request.getAddress());
        assertEquals(phone, request.getPhone());
        assertEquals(email, request.getEmail());
        assertEquals(website, request.getWebsite());
        assertEquals(openingHours, request.getOpeningHours());
        assertEquals(rating, request.getRating());
        assertEquals(isActive, request.getIsActive());
        assertEquals(deliveryRadius, request.getDeliveryRadius());
        assertEquals(minimumOrderAmount, request.getMinimumOrderAmount());
        assertEquals(deliveryFee, request.getDeliveryFee());
    }

    /**
     * Test RestaurantRequest setters with null values
     */
    @Test
    void testRestaurantRequest_SettersWithNullValues() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest("Pizza Palace", "Italian", new AddressDto("123 Main St", "New York", "NY", "10001", "USA"), "+1234567890");

        // Act
        request.setName(null);
        request.setDescription(null);
        request.setCuisine(null);
        request.setAddress(null);
        request.setPhone(null);
        request.setEmail(null);
        request.setWebsite(null);
        request.setOpeningHours(null);
        request.setRating(null);
        request.setIsActive(null);
        request.setDeliveryRadius(null);
        request.setMinimumOrderAmount(null);
        request.setDeliveryFee(null);

        // Assert
        assertNull(request.getName());
        assertNull(request.getDescription());
        assertNull(request.getCuisine());
        assertNull(request.getAddress());
        assertNull(request.getPhone());
        assertNull(request.getEmail());
        assertNull(request.getWebsite());
        assertNull(request.getOpeningHours());
        assertNull(request.getRating());
        assertNull(request.getIsActive());
        assertNull(request.getDeliveryRadius());
        assertNull(request.getMinimumOrderAmount());
        assertNull(request.getDeliveryFee());
    }

    /**
     * Test RestaurantRequest toString method
     */
    @Test
    void testRestaurantRequest_ToString() {
        // Arrange
        AddressDto address = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");
        OpeningHoursDto openingHours = new OpeningHoursDto("09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "10:00-16:00", "CLOSED");
        RestaurantRequest request = new RestaurantRequest("Pizza Palace", "Italian", address, "+1234567890");
        request.setDescription("Best pizza in town");
        request.setEmail("info@pizzapalace.com");
        request.setWebsite("www.pizzapalace.com");
        request.setOpeningHours(openingHours);
        request.setRating(new BigDecimal("4.8"));
        request.setIsActive(true);
        request.setDeliveryRadius(3.5);
        request.setMinimumOrderAmount(new BigDecimal("20.00"));
        request.setDeliveryFee(new BigDecimal("3.99"));

        // Act
        String result = request.toString();

        // Assert
        assertTrue(result.contains("RestaurantRequest{"));
        assertTrue(result.contains("name='Pizza Palace'"));
        assertTrue(result.contains("description='Best pizza in town'"));
        assertTrue(result.contains("cuisine='Italian'"));
        assertTrue(result.contains("phone='+1234567890'"));
        assertTrue(result.contains("email='info@pizzapalace.com'"));
        assertTrue(result.contains("website='www.pizzapalace.com'"));
        assertTrue(result.contains("rating=4.8"));
        assertTrue(result.contains("isActive=true"));
        assertTrue(result.contains("deliveryRadius=3.5"));
        assertTrue(result.contains("minimumOrderAmount=20.00"));
        assertTrue(result.contains("deliveryFee=3.99"));
    }

    /**
     * Test RestaurantRequest toString method with null values
     */
    @Test
    void testRestaurantRequest_ToString_WithNulls() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest();

        // Act
        String result = request.toString();

        // Assert
        assertTrue(result.contains("RestaurantRequest{"));
        assertTrue(result.contains("name='null'"));
        assertTrue(result.contains("description='null'"));
        assertTrue(result.contains("cuisine='null'"));
        assertTrue(result.contains("address=null"));
        assertTrue(result.contains("phone='null'"));
        assertTrue(result.contains("email='null'"));
        assertTrue(result.contains("website='null'"));
        assertTrue(result.contains("openingHours=null"));
        assertTrue(result.contains("rating=null"));
        assertTrue(result.contains("isActive=true")); // Default value
        assertTrue(result.contains("deliveryRadius=null"));
        assertTrue(result.contains("minimumOrderAmount=null"));
        assertTrue(result.contains("deliveryFee=null"));
    }

    // ===============================
    // Comprehensive equals() and hashCode() Tests for Branch Coverage
    // ===============================

    /**
     * Test AddressDto equals() method with null field comparisons to cover all branches
     */
    @Test
    void testAddressDto_Equals_NullFieldBranches() {
        // Test equals with null vs non-null street
        AddressDto dto1 = new AddressDto();
        dto1.setStreet(null);
        dto1.setCity("New York");

        AddressDto dto2 = new AddressDto();
        dto2.setStreet("123 Main St");
        dto2.setCity("New York");

        assertFalse(dto1.equals(dto2));
        assertFalse(dto2.equals(dto1));

        // Test equals with null vs non-null city
        AddressDto dto3 = new AddressDto();
        dto3.setStreet("123 Main St");
        dto3.setCity(null);

        AddressDto dto4 = new AddressDto();
        dto4.setStreet("123 Main St");
        dto4.setCity("New York");

        assertFalse(dto3.equals(dto4));
        assertFalse(dto4.equals(dto3));

        // Test equals with null vs non-null state
        AddressDto dto5 = new AddressDto();
        dto5.setStreet("123 Main St");
        dto5.setCity("New York");
        dto5.setState(null);

        AddressDto dto6 = new AddressDto();
        dto6.setStreet("123 Main St");
        dto6.setCity("New York");
        dto6.setState("NY");

        assertFalse(dto5.equals(dto6));
        assertFalse(dto6.equals(dto5));

        // Test equals with null vs non-null zipCode
        AddressDto dto7 = new AddressDto();
        dto7.setStreet("123 Main St");
        dto7.setCity("New York");
        dto7.setState("NY");
        dto7.setZipCode(null);

        AddressDto dto8 = new AddressDto();
        dto8.setStreet("123 Main St");
        dto8.setCity("New York");
        dto8.setState("NY");
        dto8.setZipCode("10001");

        assertFalse(dto7.equals(dto8));
        assertFalse(dto8.equals(dto7));

        // Test equals with null vs non-null country
        AddressDto dto9 = new AddressDto();
        dto9.setStreet("123 Main St");
        dto9.setCity("New York");
        dto9.setState("NY");
        dto9.setZipCode("10001");
        dto9.setCountry(null);

        AddressDto dto10 = new AddressDto();
        dto10.setStreet("123 Main St");
        dto10.setCity("New York");
        dto10.setState("NY");
        dto10.setZipCode("10001");
        dto10.setCountry("USA");

        assertFalse(dto9.equals(dto10));
        assertFalse(dto10.equals(dto9));
    }

    /**
     * Test OpeningHoursDto equals() method with null field comparisons to cover all branches
     */
    @Test
    void testOpeningHoursDto_Equals_NullFieldBranches() {
        // Test equals with null vs non-null monday
        OpeningHoursDto dto1 = new OpeningHoursDto();
        dto1.setMonday(null);
        dto1.setTuesday("09:00-17:00");

        OpeningHoursDto dto2 = new OpeningHoursDto();
        dto2.setMonday("09:00-17:00");
        dto2.setTuesday("09:00-17:00");

        assertFalse(dto1.equals(dto2));
        assertFalse(dto2.equals(dto1));

        // Test equals with null vs non-null tuesday
        OpeningHoursDto dto3 = new OpeningHoursDto();
        dto3.setMonday("09:00-17:00");
        dto3.setTuesday(null);

        OpeningHoursDto dto4 = new OpeningHoursDto();
        dto4.setMonday("09:00-17:00");
        dto4.setTuesday("09:00-17:00");

        assertFalse(dto3.equals(dto4));
        assertFalse(dto4.equals(dto3));

        // Test equals with null vs non-null wednesday
        OpeningHoursDto dto5 = new OpeningHoursDto();
        dto5.setMonday("09:00-17:00");
        dto5.setTuesday("09:00-17:00");
        dto5.setWednesday(null);

        OpeningHoursDto dto6 = new OpeningHoursDto();
        dto6.setMonday("09:00-17:00");
        dto6.setTuesday("09:00-17:00");
        dto6.setWednesday("09:00-17:00");

        assertFalse(dto5.equals(dto6));
        assertFalse(dto6.equals(dto5));

        // Test equals with null vs non-null thursday
        OpeningHoursDto dto7 = new OpeningHoursDto();
        dto7.setMonday("09:00-17:00");
        dto7.setTuesday("09:00-17:00");
        dto7.setWednesday("09:00-17:00");
        dto7.setThursday(null);

        OpeningHoursDto dto8 = new OpeningHoursDto();
        dto8.setMonday("09:00-17:00");
        dto8.setTuesday("09:00-17:00");
        dto8.setWednesday("09:00-17:00");
        dto8.setThursday("09:00-17:00");

        assertFalse(dto7.equals(dto8));
        assertFalse(dto8.equals(dto7));

        // Test equals with null vs non-null friday
        OpeningHoursDto dto9 = new OpeningHoursDto();
        dto9.setMonday("09:00-17:00");
        dto9.setTuesday("09:00-17:00");
        dto9.setWednesday("09:00-17:00");
        dto9.setThursday("09:00-17:00");
        dto9.setFriday(null);

        OpeningHoursDto dto10 = new OpeningHoursDto();
        dto10.setMonday("09:00-17:00");
        dto10.setTuesday("09:00-17:00");
        dto10.setWednesday("09:00-17:00");
        dto10.setThursday("09:00-17:00");
        dto10.setFriday("09:00-17:00");

        assertFalse(dto9.equals(dto10));
        assertFalse(dto10.equals(dto9));

        // Test equals with null vs non-null saturday
        OpeningHoursDto dto11 = new OpeningHoursDto();
        dto11.setMonday("09:00-17:00");
        dto11.setTuesday("09:00-17:00");
        dto11.setWednesday("09:00-17:00");
        dto11.setThursday("09:00-17:00");
        dto11.setFriday("09:00-17:00");
        dto11.setSaturday(null);

        OpeningHoursDto dto12 = new OpeningHoursDto();
        dto12.setMonday("09:00-17:00");
        dto12.setTuesday("09:00-17:00");
        dto12.setWednesday("09:00-17:00");
        dto12.setThursday("09:00-17:00");
        dto12.setFriday("09:00-17:00");
        dto12.setSaturday("10:00-16:00");

        assertFalse(dto11.equals(dto12));
        assertFalse(dto12.equals(dto11));

        // Test equals with null vs non-null sunday
        OpeningHoursDto dto13 = new OpeningHoursDto();
        dto13.setMonday("09:00-17:00");
        dto13.setTuesday("09:00-17:00");
        dto13.setWednesday("09:00-17:00");
        dto13.setThursday("09:00-17:00");
        dto13.setFriday("09:00-17:00");
        dto13.setSaturday("10:00-16:00");
        dto13.setSunday(null);

        OpeningHoursDto dto14 = new OpeningHoursDto();
        dto14.setMonday("09:00-17:00");
        dto14.setTuesday("09:00-17:00");
        dto14.setWednesday("09:00-17:00");
        dto14.setThursday("09:00-17:00");
        dto14.setFriday("09:00-17:00");
        dto14.setSaturday("10:00-16:00");
        dto14.setSunday("CLOSED");

        assertFalse(dto13.equals(dto14));
        assertFalse(dto14.equals(dto13));
    }

    /**
     * Test RestaurantRequest equals() method with null field comparisons to cover all branches
     */
    @Test
    void testRestaurantRequest_Equals_NullFieldBranches() {
        AddressDto address = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");
        OpeningHoursDto hours = new OpeningHoursDto("09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "10:00-16:00", "CLOSED");

        // Test equals with null vs non-null name
        RestaurantRequest request1 = new RestaurantRequest();
        request1.setName(null);
        request1.setCuisine("Italian");

        RestaurantRequest request2 = new RestaurantRequest();
        request2.setName("Pizza Palace");
        request2.setCuisine("Italian");

        assertFalse(request1.equals(request2));
        assertFalse(request2.equals(request1));

        // Test equals with null vs non-null description
        RestaurantRequest request3 = new RestaurantRequest();
        request3.setName("Pizza Palace");
        request3.setDescription(null);

        RestaurantRequest request4 = new RestaurantRequest();
        request4.setName("Pizza Palace");
        request4.setDescription("Best pizza in town");

        assertFalse(request3.equals(request4));
        assertFalse(request4.equals(request3));

        // Test equals with null vs non-null cuisine
        RestaurantRequest request5 = new RestaurantRequest();
        request5.setName("Pizza Palace");
        request5.setCuisine(null);

        RestaurantRequest request6 = new RestaurantRequest();
        request6.setName("Pizza Palace");
        request6.setCuisine("Italian");

        assertFalse(request5.equals(request6));
        assertFalse(request6.equals(request5));

        // Test equals with null vs non-null address
        RestaurantRequest request7 = new RestaurantRequest();
        request7.setName("Pizza Palace");
        request7.setCuisine("Italian");
        request7.setAddress(null);

        RestaurantRequest request8 = new RestaurantRequest();
        request8.setName("Pizza Palace");
        request8.setCuisine("Italian");
        request8.setAddress(address);

        assertFalse(request7.equals(request8));
        assertFalse(request8.equals(request7));

        // Test equals with null vs non-null phone
        RestaurantRequest request9 = new RestaurantRequest();
        request9.setName("Pizza Palace");
        request9.setCuisine("Italian");
        request9.setAddress(address);
        request9.setPhone(null);

        RestaurantRequest request10 = new RestaurantRequest();
        request10.setName("Pizza Palace");
        request10.setCuisine("Italian");
        request10.setAddress(address);
        request10.setPhone("+1234567890");

        assertFalse(request9.equals(request10));
        assertFalse(request10.equals(request9));

        // Test equals with null vs non-null email
        RestaurantRequest request11 = new RestaurantRequest();
        request11.setName("Pizza Palace");
        request11.setCuisine("Italian");
        request11.setAddress(address);
        request11.setPhone("+1234567890");
        request11.setEmail(null);

        RestaurantRequest request12 = new RestaurantRequest();
        request12.setName("Pizza Palace");
        request12.setCuisine("Italian");
        request12.setAddress(address);
        request12.setPhone("+1234567890");
        request12.setEmail("info@pizzapalace.com");

        assertFalse(request11.equals(request12));
        assertFalse(request12.equals(request11));

        // Test equals with null vs non-null website
        RestaurantRequest request13 = new RestaurantRequest();
        request13.setName("Pizza Palace");
        request13.setCuisine("Italian");
        request13.setAddress(address);
        request13.setPhone("+1234567890");
        request13.setEmail("info@pizzapalace.com");
        request13.setWebsite(null);

        RestaurantRequest request14 = new RestaurantRequest();
        request14.setName("Pizza Palace");
        request14.setCuisine("Italian");
        request14.setAddress(address);
        request14.setPhone("+1234567890");
        request14.setEmail("info@pizzapalace.com");
        request14.setWebsite("www.pizzapalace.com");

        assertFalse(request13.equals(request14));
        assertFalse(request14.equals(request13));

        // Test equals with null vs non-null openingHours
        RestaurantRequest request15 = new RestaurantRequest();
        request15.setName("Pizza Palace");
        request15.setCuisine("Italian");
        request15.setAddress(address);
        request15.setPhone("+1234567890");
        request15.setEmail("info@pizzapalace.com");
        request15.setWebsite("www.pizzapalace.com");
        request15.setOpeningHours(null);

        RestaurantRequest request16 = new RestaurantRequest();
        request16.setName("Pizza Palace");
        request16.setCuisine("Italian");
        request16.setAddress(address);
        request16.setPhone("+1234567890");
        request16.setEmail("info@pizzapalace.com");
        request16.setWebsite("www.pizzapalace.com");
        request16.setOpeningHours(hours);

        assertFalse(request15.equals(request16));
        assertFalse(request16.equals(request15));

        // Test equals with null vs non-null rating
        RestaurantRequest request17 = new RestaurantRequest();
        request17.setName("Pizza Palace");
        request17.setCuisine("Italian");
        request17.setAddress(address);
        request17.setPhone("+1234567890");
        request17.setEmail("info@pizzapalace.com");
        request17.setWebsite("www.pizzapalace.com");
        request17.setOpeningHours(hours);
        request17.setRating(null);

        RestaurantRequest request18 = new RestaurantRequest();
        request18.setName("Pizza Palace");
        request18.setCuisine("Italian");
        request18.setAddress(address);
        request18.setPhone("+1234567890");
        request18.setEmail("info@pizzapalace.com");
        request18.setWebsite("www.pizzapalace.com");
        request18.setOpeningHours(hours);
        request18.setRating(BigDecimal.valueOf(4.5));

        assertFalse(request17.equals(request18));
        assertFalse(request18.equals(request17));

        // Test equals with null vs non-null isActive
        RestaurantRequest request19 = new RestaurantRequest();
        request19.setName("Pizza Palace");
        request19.setCuisine("Italian");
        request19.setAddress(address);
        request19.setPhone("+1234567890");
        request19.setEmail("info@pizzapalace.com");
        request19.setWebsite("www.pizzapalace.com");
        request19.setOpeningHours(hours);
        request19.setRating(BigDecimal.valueOf(4.5));
        request19.setIsActive(null);

        RestaurantRequest request20 = new RestaurantRequest();
        request20.setName("Pizza Palace");
        request20.setCuisine("Italian");
        request20.setAddress(address);
        request20.setPhone("+1234567890");
        request20.setEmail("info@pizzapalace.com");
        request20.setWebsite("www.pizzapalace.com");
        request20.setOpeningHours(hours);
        request20.setRating(BigDecimal.valueOf(4.5));
        request20.setIsActive(true);

        assertFalse(request19.equals(request20));
        assertFalse(request20.equals(request19));

        // Test equals with null vs non-null deliveryRadius
        RestaurantRequest request21 = new RestaurantRequest();
        request21.setName("Pizza Palace");
        request21.setCuisine("Italian");
        request21.setAddress(address);
        request21.setPhone("+1234567890");
        request21.setEmail("info@pizzapalace.com");
        request21.setWebsite("www.pizzapalace.com");
        request21.setOpeningHours(hours);
        request21.setRating(BigDecimal.valueOf(4.5));
        request21.setIsActive(true);
        request21.setDeliveryRadius(null);

        RestaurantRequest request22 = new RestaurantRequest();
        request22.setName("Pizza Palace");
        request22.setCuisine("Italian");
        request22.setAddress(address);
        request22.setPhone("+1234567890");
        request22.setEmail("info@pizzapalace.com");
        request22.setWebsite("www.pizzapalace.com");
        request22.setOpeningHours(hours);
        request22.setRating(BigDecimal.valueOf(4.5));
        request22.setIsActive(true);
        request22.setDeliveryRadius(5.0);

        assertFalse(request21.equals(request22));
        assertFalse(request22.equals(request21));

        // Test equals with null vs non-null minimumOrderAmount
        RestaurantRequest request23 = new RestaurantRequest();
        request23.setName("Pizza Palace");
        request23.setCuisine("Italian");
        request23.setAddress(address);
        request23.setPhone("+1234567890");
        request23.setEmail("info@pizzapalace.com");
        request23.setWebsite("www.pizzapalace.com");
        request23.setOpeningHours(hours);
        request23.setRating(BigDecimal.valueOf(4.5));
        request23.setIsActive(true);
        request23.setDeliveryRadius(5.0);
        request23.setMinimumOrderAmount(null);

        RestaurantRequest request24 = new RestaurantRequest();
        request24.setName("Pizza Palace");
        request24.setCuisine("Italian");
        request24.setAddress(address);
        request24.setPhone("+1234567890");
        request24.setEmail("info@pizzapalace.com");
        request24.setWebsite("www.pizzapalace.com");
        request24.setOpeningHours(hours);
        request24.setRating(BigDecimal.valueOf(4.5));
        request24.setIsActive(true);
        request24.setDeliveryRadius(5.0);
        request24.setMinimumOrderAmount(BigDecimal.valueOf(15.00));

        assertFalse(request23.equals(request24));
        assertFalse(request24.equals(request23));

        // Test equals with null vs non-null deliveryFee
        RestaurantRequest request25 = new RestaurantRequest();
        request25.setName("Pizza Palace");
        request25.setCuisine("Italian");
        request25.setAddress(address);
        request25.setPhone("+1234567890");
        request25.setEmail("info@pizzapalace.com");
        request25.setWebsite("www.pizzapalace.com");
        request25.setOpeningHours(hours);
        request25.setRating(BigDecimal.valueOf(4.5));
        request25.setIsActive(true);
        request25.setDeliveryRadius(5.0);
        request25.setMinimumOrderAmount(BigDecimal.valueOf(15.00));
        request25.setDeliveryFee(null);

        RestaurantRequest request26 = new RestaurantRequest();
        request26.setName("Pizza Palace");
        request26.setCuisine("Italian");
        request26.setAddress(address);
        request26.setPhone("+1234567890");
        request26.setEmail("info@pizzapalace.com");
        request26.setWebsite("www.pizzapalace.com");
        request26.setOpeningHours(hours);
        request26.setRating(BigDecimal.valueOf(4.5));
        request26.setIsActive(true);
        request26.setDeliveryRadius(5.0);
        request26.setMinimumOrderAmount(BigDecimal.valueOf(15.00));
        request26.setDeliveryFee(BigDecimal.valueOf(2.99));

        assertFalse(request25.equals(request26));
        assertFalse(request26.equals(request25));
    }

// ===============================
    // Additional Edge Case Tests for 100% Branch Coverage
    // ===============================

    /**
     * Test AddressDto object identity and null checks
     */
    @Test
    void testAddressDto_ObjectIdentity() {
        AddressDto dto = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");

        // Test same reference
        assertTrue(dto.equals(dto));

        // Test null comparison
        assertFalse(dto.equals(null));

        // Test different class type
        assertFalse(dto.equals("string"));
    }

    /**
     * Test OpeningHoursDto object identity and null checks
     */
    @Test
    void testOpeningHoursDto_ObjectIdentity() {
        OpeningHoursDto dto = new OpeningHoursDto("09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "10:00-16:00", "CLOSED");

        // Test same reference
        assertTrue(dto.equals(dto));

        // Test null comparison
        assertFalse(dto.equals(null));

        // Test different class type
        assertFalse(dto.equals("string"));
    }

    /**
     * Test RestaurantRequest object identity and null checks
     */
    @Test
    void testRestaurantRequest_ObjectIdentity() {
        AddressDto address = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");
        RestaurantRequest request = new RestaurantRequest("Pizza Palace", "Italian", address, "+1234567890");

        // Test same reference
        assertTrue(request.equals(request));

        // Test null comparison
        assertFalse(request.equals(null));

        // Test different class type
        assertFalse(request.equals("string"));
    }

    /**
     * Test AddressDto toString() method with null state to cover conditional branch
     */
    @Test
    void testAddressDto_ToString_NullStateBranch() {
        AddressDto dto = new AddressDto();
        dto.setStreet("123 Main St");
        dto.setCity("New York");
        dto.setState(null); // This should trigger the null check branch
        dto.setZipCode("10001");

        String result = dto.toString();
        assertEquals("123 Main St, New York 10001", result);
    }

    /**
     * Test AddressDto toString() method with null zipCode to cover conditional branch
     */
    @Test
    void testAddressDto_ToString_NullZipCodeBranch() {
        AddressDto dto = new AddressDto();
        dto.setStreet("123 Main St");
        dto.setCity("New York");
        dto.setState("NY");
        dto.setZipCode(null); // This should trigger the null check branch

        String result = dto.toString();
        assertEquals("123 Main St, New York, NY", result);
    }

    /**
     * Test AddressDto toString() method with only city to cover conditional branch
     */
    @Test
    void testAddressDto_ToString_OnlyCity() {
        AddressDto dto = new AddressDto();
        dto.setStreet(null);
        dto.setCity("New York");
        dto.setState(null);
        dto.setZipCode(null);

        String result = dto.toString();
        assertEquals(", New York", result);
    }

    /**
     * Test OpeningHoursDto toFormattedString() method with mixed null values
     */
    @Test
    void testOpeningHoursDto_ToFormattedString_MixedNulls() {
        OpeningHoursDto dto = new OpeningHoursDto();
        dto.setMonday("09:00-17:00");
        dto.setTuesday(null);
        dto.setWednesday("09:00-17:00");
        dto.setThursday(null);
        dto.setFriday("09:00-20:00");
        dto.setSaturday(null);
        dto.setSunday("10:00-16:00");

        String result = dto.toFormattedString();
        assertEquals("Mon: 09:00-17:00, Tue: CLOSED, Wed: 09:00-17:00, Thu: CLOSED, Fri: 09:00-20:00, Sat: CLOSED, Sun: 10:00-16:00", result);
    }

    /**
     * Test OpeningHoursDto toFormattedString() method with specific null patterns to cover missed branches
     * Line 116 in OpeningHoursDto.java has a missed branch for monday null check
     */
    @Test
    void testOpeningHoursDto_ToFormattedString_SpecificNullBranches() {
        // Test with monday null (covers line 116 missed branch)
        OpeningHoursDto dto1 = new OpeningHoursDto();
        dto1.setMonday(null); // This should trigger the null branch in line 116
        dto1.setTuesday("09:00-17:00");
        dto1.setWednesday("09:00-17:00");
        dto1.setThursday("09:00-17:00");
        dto1.setFriday("09:00-17:00");
        dto1.setSaturday("10:00-16:00");
        dto1.setSunday("CLOSED");

        String result1 = dto1.toFormattedString();
        assertTrue(result1.contains("Mon: CLOSED"));

        // Test with tuesday null (covers line 117 branch)
        OpeningHoursDto dto2 = new OpeningHoursDto();
        dto2.setMonday("09:00-17:00");
        dto2.setTuesday(null); // This should trigger the null branch in line 117
        dto2.setWednesday("09:00-17:00");
        dto2.setThursday("09:00-17:00");
        dto2.setFriday("09:00-17:00");
        dto2.setSaturday("10:00-16:00");
        dto2.setSunday("CLOSED");

        String result2 = dto2.toFormattedString();
        assertTrue(result2.contains("Tue: CLOSED"));

        // Test with friday null (covers line 120 branch)
        OpeningHoursDto dto3 = new OpeningHoursDto();
        dto3.setMonday("09:00-17:00");
        dto3.setTuesday("09:00-17:00");
        dto3.setWednesday("09:00-17:00");
        dto3.setThursday("09:00-17:00");
        dto3.setFriday(null); // This should trigger the null branch in line 120
        dto3.setSaturday("10:00-16:00");
        dto3.setSunday("CLOSED");

        String result3 = dto3.toFormattedString();
        assertTrue(result3.contains("Fri: CLOSED"));

        // Test with all fields having values to cover the non-null branches
        OpeningHoursDto dto4 = new OpeningHoursDto("09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "09:00-17:00", "10:00-16:00", "CLOSED");
        String result4 = dto4.toFormattedString();
        assertFalse(result4.contains("CLOSED") && !result4.contains("Sun: CLOSED")); // Should not have CLOSED except for Sunday
    }

    /**
     * Test OpeningHoursDto toFormattedString() method to specifically target the missed monday null branch
     * This test is designed to achieve 100% branch coverage for the toFormattedString() method
     */
    @Test
    void testOpeningHoursDto_ToFormattedString_MondayNullBranch() {
        // Create a fresh object with monday explicitly set to null
        OpeningHoursDto dto = new OpeningHoursDto();

        // Set all other days to non-null values
        dto.setMonday(null); // Target the missed branch in line 116
        dto.setTuesday("09:00-17:00");
        dto.setWednesday("10:00-18:00");
        dto.setThursday("08:00-16:00");
        dto.setFriday("09:00-20:00");
        dto.setSaturday("12:00-22:00");
        dto.setSunday("11:00-19:00");

        // Execute the method that contains the missed branch
        String result = dto.toFormattedString();

        // Verify that monday shows as CLOSED (null branch executed)
        assertTrue(result.contains("Mon: CLOSED"));

        // Verify other days show their actual values (non-null branches executed)
        assertTrue(result.contains("Tue: 09:00-17:00"));
        assertTrue(result.contains("Wed: 10:00-18:00"));
        assertTrue(result.contains("Thu: 08:00-16:00"));
        assertTrue(result.contains("Fri: 09:00-20:00"));
        assertTrue(result.contains("Sat: 12:00-22:00"));
        assertTrue(result.contains("Sun: 11:00-19:00"));

        // Ensure the method completes successfully
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}