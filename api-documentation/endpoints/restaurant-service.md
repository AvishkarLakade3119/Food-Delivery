# Restaurant Service API Documentation

## Service Information
- **Service Name:** Restaurant Service
- **Base URL:** `http://localhost:8084`
- **Port:** 8084
- **Description:** Restaurant and menu management

---

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
  "rating": 4.5,
  "isActive": true,
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
  "rating": 4.5,
  "isActive": true
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
  "isAvailable": true,
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
  "isAvailable": true
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
  "price": 14.99
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Margherita Pizza Deluxe",
  "price": 14.99,
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

## Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/restaurants | Create restaurant |
| GET | /api/restaurants | Get all restaurants |
| GET | /api/restaurants/{id} | Get restaurant by ID |
| GET | /api/restaurants/cuisine/{cuisineType} | Get by cuisine |
| GET | /api/restaurants/search | Search restaurants |
| PUT | /api/restaurants/{id} | Update restaurant |
| PUT | /api/restaurants/{id}/activate | Activate restaurant |
| PUT | /api/restaurants/{id}/deactivate | Deactivate restaurant |
| DELETE | /api/restaurants/{id} | Delete restaurant |
| POST | /api/restaurants/{restaurantId}/menu | Create menu item |
| GET | /api/restaurants/{restaurantId}/menu | Get available menu items |
| GET | /api/restaurants/{restaurantId}/menu/all | Get all menu items |
| GET | /api/restaurants/{restaurantId}/menu/{itemId} | Get menu item |
| PUT | /api/restaurants/{restaurantId}/menu/{itemId} | Update menu item |
| PUT | /api/restaurants/{restaurantId}/menu/{itemId}/availability | Toggle availability |
| DELETE | /api/restaurants/{restaurantId}/menu/{itemId} | Delete menu item |
| GET | /api/menu-items | Get all menu items |
| GET | /api/menu-items/{id} | Get menu item by ID |
| PUT | /api/menu-items/{id} | Update menu item |
| DELETE | /api/menu-items/{id} | Delete menu item |