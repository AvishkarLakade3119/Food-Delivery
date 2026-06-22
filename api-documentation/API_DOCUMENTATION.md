# Food Delivery API Documentation

## Overview
This document provides comprehensive API documentation for all microservices in the Food Delivery application. The application consists of five core microservices:

- **User Service** - User authentication and profile management
- **Order Service** - Order lifecycle management
- **Payment Service** - Payment processing
- **Restaurant Service** - Restaurant and menu management
- **Notification Service** - Notification operations

## Base URLs

| Service | Base URL | Port |
|---------|----------|------|
| User Service | `http://localhost:8081` | 8081 |
| Order Service | `http://localhost:8082` | 8082 |
| Payment Service | `http://localhost:8083` | 8083 |
| Restaurant Service | `http://localhost:8084` | 8084 |
| Notification Service | `http://localhost:8085` | 8085 |

## Authentication

Currently, the API endpoints are **publicly accessible** without authentication. The controllers implement CORS configuration to allow cross-origin requests from multiple origins.

**CORS Allowed Origins:**
- `http://localhost:3000`
- `http://localhost:4200`
- `http://localhost:8080`
- `http://localhost:8081-8086`

---

# User Service API

## Authentication Endpoints

### POST /api/auth/register
**Description:** Register a new user account

**Request Headers:**
- `Content-Type: application/json` (Required)

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "address": "123 Main St, New York, NY 10001",
  "dateOfBirth": "1990-01-15"
}
```

**Validation Rules:**
- `username`: Required, 2-50 characters
- `email`: Required, valid email format
- `password`: Required, minimum 6 characters
- `phone`: Required
- `address`: Required

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "userId": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CUSTOMER"
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "error": "Validation Error",
  "message": "Email already exists"
}
```

---

### POST /api/auth/login
**Description:** Authenticate user and login

**Request Headers:**
- `Content-Type: application/json` (Required)

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "userId": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "role": "CUSTOMER"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid email or password"
}
```

---

### GET /api/auth/health
**Description:** Health check endpoint for authentication service

**Success Response (200 OK):**
```json
{
  "status": "UP",
  "service": "user-service-auth",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## User Management Endpoints

### POST /api/users
**Description:** Create a new user

**Request Body:**
```json
{
  "username": "janedoe",
  "email": "jane.doe@example.com",
  "password": "password123",
  "firstName": "Jane",
  "lastName": "Doe",
  "phone": "+1234567891",
  "address": "456 Oak Ave, Boston, MA 02101",
  "role": "CUSTOMER"
}
```

**Success Response (201 Created):**
```json
{
  "id": 2,
  "username": "janedoe",
  "email": "jane.doe@example.com",
  "firstName": "Jane",
  "lastName": "Doe",
  "phone": "+1234567891",
  "address": "456 Oak Ave, Boston, MA 02101",
  "role": "CUSTOMER",
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### GET /api/users
**Description:** Get all users

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "CUSTOMER"
  }
]
```

---

### GET /api/users/{id}
**Description:** Get user by ID

**Path Parameters:**
- `id` (Long, required): User ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "address": "123 Main St, New York, NY 10001",
  "role": "CUSTOMER"
}
```

---

### GET /api/users/email/{email}
**Description:** Get user by email address

**Path Parameters:**
- `email` (String, required): User email

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CUSTOMER"
}
```

---

### GET /api/users/role/{role}
**Description:** Get all users by role

**Path Parameters:**
- `role` (UserRole enum, required): User role (CUSTOMER, RESTAURANT_OWNER, DELIVERY_DRIVER, ADMIN)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john.doe@example.com",
    "role": "CUSTOMER"
  }
]
```

---

### GET /api/users/search
**Description:** Search users by name

**Query Parameters:**
- `name` (String, required): Search term for user name

**Example:** `GET /api/users/search?name=John`

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  }
]
```

---

### PUT /api/users/{id}
**Description:** Update user information

**Path Parameters:**
- `id` (Long, required): User ID

**Request Body:**
```json
{
  "username": "johndoe_updated",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "address": "789 New St, New York, NY 10002"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe_updated",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "address": "789 New St, New York, NY 10002",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### DELETE /api/users/{id}
**Description:** Delete user by ID

**Path Parameters:**
- `id` (Long, required): User ID

**Success Response (204 No Content)**

---

# Order Service API

## Order Management Endpoints

### POST /api/orders
**Description:** Create a new order

**Request Body:**
```json
{
  "userId": 1,
  "restaurantId": 1,
  "deliveryAddress": "123 Main St, New York, NY 10001",
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2,
      "price": 12.99,
      "specialInstructions": "No onions"
    },
    {
      "menuItemId": 2,
      "quantity": 1,
      "price": 8.50
    }
  ]
}
```

**Validation Rules:**
- `userId`: Required
- `restaurantId`: Required
- `deliveryAddress`: Required (can be string or object)
- `items`: Required, not empty
- `items[].menuItemId`: Required
- `items[].quantity`: Required, minimum 1
- `items[].price`: Required, greater than 0

**Success Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "restaurantId": 1,
  "deliveryAddress": "123 Main St, New York, NY 10001",
  "status": "CREATED",
  "totalAmount": 34.48,
  "orderItems": [
    {
      "id": 1,
      "menuItemId": 1,
      "quantity": 2,
      "price": 12.99,
      "specialInstructions": "No onions"
    },
    {
      "id": 2,
      "menuItemId": 2,
      "quantity": 1,
      "price": 8.50
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### POST /api/orders/legacy
**Description:** Create order using legacy format (backward compatibility)

**Request Body:**
```json
{
  "userId": 1,
  "restaurantId": 1,
  "deliveryAddress": "123 Main St, New York, NY 10001",
  "orderItems": [
    {
      "menuItemId": 1,
      "quantity": 2,
      "price": 12.99
    }
  ]
}
```

**Success Response (201 Created):** Same as POST /api/orders

---

### GET /api/orders
**Description:** Get all orders

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "restaurantId": 1,
    "status": "CREATED",
    "totalAmount": 34.48,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### GET /api/orders/{id}
**Description:** Get order by ID

**Path Parameters:**
- `id` (Long, required): Order ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "restaurantId": 1,
  "deliveryAddress": "123 Main St, New York, NY 10001",
  "status": "CREATED",
  "totalAmount": 34.48,
  "orderItems": [
    {
      "id": 1,
      "menuItemId": 1,
      "quantity": 2,
      "price": 12.99
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### GET /api/orders/user/{userId}
**Description:** Get all orders for a specific user

**Path Parameters:**
- `userId` (Long, required): User ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "restaurantId": 1,
    "status": "CREATED",
    "totalAmount": 34.48,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### GET /api/orders/restaurant/{restaurantId}
**Description:** Get all orders for a specific restaurant

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "restaurantId": 1,
    "status": "CREATED",
    "totalAmount": 34.48,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### GET /api/orders/status/{status}
**Description:** Get all orders by status

**Path Parameters:**
- `status` (OrderStatus enum, required): Order status (CREATED, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "restaurantId": 1,
    "status": "CREATED",
    "totalAmount": 34.48,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### GET /api/orders/{id}/tracking
**Description:** Get order tracking information

**Path Parameters:**
- `id` (Long, required): Order ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "restaurantId": 1,
  "status": "OUT_FOR_DELIVERY",
  "totalAmount": 34.48,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T11:15:00"
}
```

---

### PUT /api/orders/{id}
**Description:** Update order details

**Path Parameters:**
- `id` (Long, required): Order ID

**Request Body:**
```json
{
  "deliveryAddress": "456 New Address, New York, NY 10002",
  "status": "CONFIRMED"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "restaurantId": 1,
  "deliveryAddress": "456 New Address, New York, NY 10002",
  "status": "CONFIRMED",
  "totalAmount": 34.48,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### PUT /api/orders/{id}/status
**Description:** Update order status only

**Path Parameters:**
- `id` (Long, required): Order ID

**Request Body:**
```json
{
  "status": "CONFIRMED"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "status": "CONFIRMED",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### POST /api/orders/{id}/confirm
**Description:** Confirm an order

**Path Parameters:**
- `id` (Long, required): Order ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "status": "CONFIRMED",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### POST /api/orders/{id}/cancel
**Description:** Cancel an order

**Path Parameters:**
- `id` (Long, required): Order ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "status": "CANCELLED",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### POST /api/orders/{id}/deliver
**Description:** Mark order as delivered

**Path Parameters:**
- `id` (Long, required): Order ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "status": "DELIVERED",
  "updatedAt": "2024-01-15T12:00:00"
}
```

---

### DELETE /api/orders/{id}
**Description:** Delete an order

**Path Parameters:**
- `id` (Long, required): Order ID

**Success Response (204 No Content)**

---

# Payment Service API

## Payment Management Endpoints

### POST /api/payments
**Description:** Create a new payment record

**Request Body:**
```json
{
  "orderId": 1,
  "amount": 34.48,
  "paymentMethod": "CREDIT_CARD",
  "transactionId": "TXN123456789"
}
```

**Success Response (201 Created):**
```json
{
  "id": 1,
  "orderId": 1,
  "amount": 34.48,
  "paymentMethod": "CREDIT_CARD",
  "status": "PENDING",
  "transactionId": "TXN123456789",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### POST /api/payments/process
**Description:** Process a payment

**Request Body:**
```json
{
  "orderId": 1,
  "amount": 34.48,
  "paymentMethod": "CREDIT_CARD"
}
```

**Validation Rules:**
- `orderId`: Required
- `amount`: Required, must be positive
- `paymentMethod`: Required

**Success Response (200 OK):**
```json
{
  "status": "SUCCESS",
  "transactionId": "TXN987654321",
  "message": "Payment processed successfully"
}
```

---

### POST /api/payments/{id}/process
**Description:** Process an existing payment by ID

**Path Parameters:**
- `id` (Long, required): Payment ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "orderId": 1,
  "amount": 34.48,
  "status": "SUCCESS",
  "updatedAt": "2024-01-15T10:35:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Payment already processed"
}
```

---

### POST /api/payments/{id}/refund
**Description:** Refund a payment

**Path Parameters:**
- `id` (Long, required): Payment ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "orderId": 1,
  "amount": 34.48,
  "status": "REFUNDED",
  "updatedAt": "2024-01-15T11:00:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Payment already refunded"
}
```

---

### GET /api/payments
**Description:** Get all payments

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderId": 1,
    "amount": 34.48,
    "paymentMethod": "CREDIT_CARD",
    "status": "SUCCESS",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### GET /api/payments/{id}
**Description:** Get payment by ID

**Path Parameters:**
- `id` (Long, required): Payment ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "orderId": 1,
  "amount": 34.48,
  "paymentMethod": "CREDIT_CARD",
  "status": "SUCCESS",
  "transactionId": "TXN123456789",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:35:00"
}
```

---

### GET /api/payments/order/{orderId}
**Description:** Get all payments for a specific order

**Path Parameters:**
- `orderId` (Long, required): Order ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderId": 1,
    "amount": 34.48,
    "paymentMethod": "CREDIT_CARD",
    "status": "SUCCESS",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### GET /api/payments/status/{status}
**Description:** Get all payments by status

**Path Parameters:**
- `status` (PaymentStatus enum, required): Payment status (PENDING, SUCCESS, FAILED, REFUNDED)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderId": 1,
    "amount": 34.48,
    "status": "SUCCESS",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### PUT /api/payments/{id}
**Description:** Update payment details

**Path Parameters:**
- `id` (Long, required): Payment ID

**Request Body:**
```json
{
  "amount": 35.00,
  "paymentMethod": "DEBIT_CARD"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "orderId": 1,
  "amount": 35.00,
  "paymentMethod": "DEBIT_CARD",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### DELETE /api/payments/{id}
**Description:** Delete a payment

**Path Parameters:**
- `id` (Long, required): Payment ID

**Success Response (204 No Content)**

---

# Restaurant Service API

## Restaurant Management Endpoints

### POST /api/restaurants
**Description:** Create a new restaurant

**Request Body:**
```json
{
  "name": "Pizza Palace",
  "description": "Authentic Italian pizza and pasta",
  "cuisine": "Italian",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "phone": "+12125551234",
  "email": "contact@pizzapalace.com",
  "website": "https://pizzapalace.com",
  "openingHours": {
    "monday": "09:00-22:00",
    "tuesday": "09:00-22:00",
    "wednesday": "09:00-22:00",
    "thursday": "09:00-22:00",
    "friday": "09:00-23:00",
    "saturday": "10:00-23:00",
    "sunday": "10:00-21:00"
  },
  "rating": 4.5,
  "isActive": true,
  "deliveryRadius": 5.0,
  "minimumOrderAmount": 15.00,
  "deliveryFee": 3.99
}
```

**Validation Rules:**
- `name`: Required, 2-100 characters
- `cuisine`: Required
- `address`: Required (nested object)
- `phone`: Required, valid phone format
- `email`: Valid email format
- `rating`: 0.0-5.0
- `deliveryRadius`: Positive number
- `minimumOrderAmount`: Positive number
- `deliveryFee`: Positive number

**Success Response (201 Created):**
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "description": "Authentic Italian pizza and pasta",
  "cuisine": "Italian",
  "address": "123 Main St, New York, NY 10001, USA",
  "phone": "+12125551234",
  "email": "contact@pizzapalace.com",
  "website": "https://pizzapalace.com",
  "rating": 4.5,
  "isActive": true,
  "deliveryRadius": 5.0,
  "minimumOrderAmount": 15.00,
  "deliveryFee": 3.99,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### GET /api/restaurants
**Description:** Get all active restaurants

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Pizza Palace",
    "cuisine": "Italian",
    "rating": 4.5,
    "isActive": true,
    "deliveryFee": 3.99
  }
]
```

---

### GET /api/restaurants/{id}
**Description:** Get restaurant by ID

**Path Parameters:**
- `id` (Long, required): Restaurant ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "description": "Authentic Italian pizza and pasta",
  "cuisine": "Italian",
  "address": "123 Main St, New York, NY 10001, USA",
  "phone": "+12125551234",
  "email": "contact@pizzapalace.com",
  "rating": 4.5,
  "isActive": true,
  "deliveryRadius": 5.0,
  "minimumOrderAmount": 15.00,
  "deliveryFee": 3.99
}
```

---

### GET /api/restaurants/cuisine/{cuisineType}
**Description:** Get restaurants by cuisine type

**Path Parameters:**
- `cuisineType` (String, required): Cuisine type (e.g., Italian, Chinese, Mexican)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Pizza Palace",
    "cuisine": "Italian",
    "rating": 4.5
  }
]
```

---

### GET /api/restaurants/search
**Description:** Search restaurants by name or description

**Query Parameters:**
- `query` (String, required): Search term

**Example:** `GET /api/restaurants/search?query=pizza`

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Pizza Palace",
    "cuisine": "Italian",
    "rating": 4.5
  }
]
```

---

### PUT /api/restaurants/{id}
**Description:** Update restaurant information

**Path Parameters:**
- `id` (Long, required): Restaurant ID

**Request Body:**
```json
{
  "name": "Pizza Palace Updated",
  "description": "Best Italian food in town",
  "cuisine": "Italian",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "phone": "+12125551234",
  "deliveryFee": 4.99
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Pizza Palace Updated",
  "description": "Best Italian food in town",
  "deliveryFee": 4.99,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### PUT /api/restaurants/{id}/activate
**Description:** Activate a restaurant

**Path Parameters:**
- `id` (Long, required): Restaurant ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "isActive": true,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### PUT /api/restaurants/{id}/deactivate
**Description:** Deactivate a restaurant

**Path Parameters:**
- `id` (Long, required): Restaurant ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "isActive": false,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### DELETE /api/restaurants/{id}
**Description:** Delete a restaurant

**Path Parameters:**
- `id` (Long, required): Restaurant ID

**Success Response (204 No Content)**

---

## Menu Item Endpoints

### POST /api/restaurants/{restaurantId}/menu
**Description:** Create a new menu item for a restaurant

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID

**Request Body:**
```json
{
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato, mozzarella, and basil",
  "price": 12.99,
  "category": "Pizza",
  "imageUrl": "https://example.com/margherita.jpg",
  "isAvailable": true,
  "preparationTime": 20
}
```

**Success Response (201 Created):**
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato, mozzarella, and basil",
  "price": 12.99,
  "category": "Pizza",
  "imageUrl": "https://example.com/margherita.jpg",
  "isAvailable": true,
  "preparationTime": 20,
  "restaurantId": 1,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### GET /api/restaurants/{restaurantId}/menu
**Description:** Get all available menu items for a restaurant

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Margherita Pizza",
    "price": 12.99,
    "category": "Pizza",
    "isAvailable": true
  }
]
```

---

### GET /api/restaurants/{restaurantId}/menu/all
**Description:** Get all menu items (including unavailable) for a restaurant

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Margherita Pizza",
    "price": 12.99,
    "isAvailable": true
  },
  {
    "id": 2,
    "name": "Pepperoni Pizza",
    "price": 14.99,
    "isAvailable": false
  }
]
```

---

### GET /api/restaurants/{restaurantId}/menu/{itemId}
**Description:** Get specific menu item by ID

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID
- `itemId` (Long, required): Menu item ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato, mozzarella, and basil",
  "price": 12.99,
  "category": "Pizza",
  "isAvailable": true,
  "preparationTime": 20
}
```

---

### PUT /api/restaurants/{restaurantId}/menu/{itemId}
**Description:** Update menu item

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID
- `itemId` (Long, required): Menu item ID

**Request Body:**
```json
{
  "name": "Margherita Pizza Deluxe",
  "price": 14.99,
  "description": "Premium Margherita with buffalo mozzarella"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Margherita Pizza Deluxe",
  "price": 14.99,
  "description": "Premium Margherita with buffalo mozzarella",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### PUT /api/restaurants/{restaurantId}/menu/{itemId}/availability
**Description:** Toggle menu item availability

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID
- `itemId` (Long, required): Menu item ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "isAvailable": false,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### DELETE /api/restaurants/{restaurantId}/menu/{itemId}
**Description:** Delete menu item

**Path Parameters:**
- `restaurantId` (Long, required): Restaurant ID
- `itemId` (Long, required): Menu item ID

**Success Response (204 No Content)**

---

## Direct Menu Item Endpoints

### GET /api/menu-items
**Description:** Get all menu items across all restaurants

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Margherita Pizza",
    "price": 12.99,
    "restaurantId": 1
  }
]
```

---

### GET /api/menu-items/{id}
**Description:** Get menu item by ID

**Path Parameters:**
- `id` (Long, required): Menu item ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato, mozzarella, and basil",
  "price": 12.99,
  "category": "Pizza",
  "isAvailable": true
}
```

---

### PUT /api/menu-items/{id}
**Description:** Update menu item directly

**Path Parameters:**
- `id` (Long, required): Menu item ID

**Request Body:**
```json
{
  "name": "Updated Item Name",
  "price": 15.99
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Updated Item Name",
  "price": 15.99,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### DELETE /api/menu-items/{id}
**Description:** Delete menu item directly

**Path Parameters:**
- `id` (Long, required): Menu item ID

**Success Response (204 No Content)**

---

# Notification Service API

## Notification Management Endpoints

### POST /api/notifications
**Description:** Create a new notification

**Request Body:**
```json
{
  "userId": 1,
  "message": "Your order #12345 has been confirmed",
  "type": "ORDER_CONFIRMATION"
}
```

**Validation Rules:**
- `userId`: Required
- `message`: Required
- `type`: Required

**Success Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "message": "Your order #12345 has been confirmed",
  "type": "ORDER_CONFIRMATION",
  "status": "PENDING",
  "isRead": false,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### POST /api/notifications/send
**Description:** Send notification immediately

**Request Body:**
```json
{
  "userId": 1,
  "message": "Your order is out for delivery",
  "type": "ORDER_UPDATE"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "message": "Your order is out for delivery",
  "type": "ORDER_UPDATE",
  "status": "SENT",
  "isRead": false,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### POST /api/notifications/email
**Description:** Send email notification

**Request Body:**
```json
{
  "userId": 1,
  "message": "Your order has been delivered",
  "type": "ORDER_DELIVERED"
}
```

**Success Response (200 OK):**
```json
"Email notification sent successfully"
```

---

### POST /api/notifications/sms
**Description:** Send SMS notification

**Request Body:**
```json
{
  "userId": 1,
  "message": "Your order is ready for pickup",
  "type": "ORDER_READY"
}
```

**Success Response (200 OK):**
```json
"SMS notification sent successfully"
```

---

### GET /api/notifications
**Description:** Get all notifications

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "message": "Your order #12345 has been confirmed",
    "type": "ORDER_CONFIRMATION",
    "status": "SENT",
    "isRead": false,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### GET /api/notifications/{id}
**Description:** Get notification by ID

**Path Parameters:**
- `id` (Long, required): Notification ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "message": "Your order #12345 has been confirmed",
  "type": "ORDER_CONFIRMATION",
  "status": "SENT",
  "isRead": false,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### GET /api/notifications/user/{userId}
**Description:** Get all notifications for a specific user

**Path Parameters:**
- `userId` (Long, required): User ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "message": "Your order #12345 has been confirmed",
    "type": "ORDER_CONFIRMATION",
    "isRead": false,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### PUT /api/notifications/{id}
**Description:** Update notification

**Path Parameters:**
- `id` (Long, required): Notification ID

**Request Body:**
```json
{
  "message": "Updated notification message",
  "type": "ORDER_UPDATE"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "message": "Updated notification message",
  "type": "ORDER_UPDATE",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### PUT /api/notifications/{id}/read
**Description:** Mark notification as read

**Path Parameters:**
- `id` (Long, required): Notification ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "message": "Your order #12345 has been confirmed",
  "isRead": true,
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### DELETE /api/notifications/{id}
**Description:** Delete notification

**Path Parameters:**
- `id` (Long, required): Notification ID

**Success Response (204 No Content)**

---

### GET /api/notifications/health
**Description:** Health check endpoint for notification service

**Success Response (200 OK):**
```json
"Notification Service is running"
```

---

## Common Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## API Testing Notes

### Content-Type Header
All POST and PUT requests must include:
```
Content-Type: application/json
```

### CORS Configuration
The APIs support cross-origin requests from:
- Frontend applications (localhost:3000, localhost:4200)
- Other microservices (localhost:8080-8086)

### Date/Time Format
All timestamps use ISO 8601 format:
```
2024-01-15T10:30:00
```

### Decimal Values
Monetary amounts use BigDecimal with 2 decimal places:
```json
{
  "amount": 12.99
}
```

---

## Postman Collection

For easy testing, import the `POSTMAN_COLLECTION.json` file included in this documentation package. The collection includes:

- Pre-configured requests for all endpoints
- Sample request bodies
- Environment variables for base URLs
- Test scripts for response validation

---

**Document Version:** 1.0  
**Last Updated:** January 15, 2024  
**Generated for:** Food Delivery Microservices Application