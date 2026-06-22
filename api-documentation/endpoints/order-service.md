# Order Service API Documentation

## Service Information
- **Service Name:** Order Service
- **Base URL:** `http://localhost:8082`
- **Port:** 8082
- **Description:** Order lifecycle management

---

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

## Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/orders | Create new order |
| POST | /api/orders/legacy | Create order (legacy) |
| GET | /api/orders | Get all orders |
| GET | /api/orders/{id} | Get order by ID |
| GET | /api/orders/user/{userId} | Get orders by user |
| GET | /api/orders/restaurant/{restaurantId} | Get orders by restaurant |
| GET | /api/orders/status/{status} | Get orders by status |
| GET | /api/orders/{id}/tracking | Get order tracking |
| PUT | /api/orders/{id} | Update order |
| PUT | /api/orders/{id}/status | Update order status |
| POST | /api/orders/{id}/confirm | Confirm order |
| POST | /api/orders/{id}/cancel | Cancel order |
| POST | /api/orders/{id}/deliver | Mark as delivered |
| DELETE | /api/orders/{id} | Delete order |