# User Service API Documentation

## Service Information
- **Service Name:** User Service
- **Base URL:** `http://localhost:8081`
- **Port:** 8081
- **Description:** User authentication and profile management

---

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

## Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | User login |
| GET | /api/auth/health | Health check |
| POST | /api/users | Create user |
| GET | /api/users | Get all users |
| GET | /api/users/{id} | Get user by ID |
| GET | /api/users/email/{email} | Get user by email |
| GET | /api/users/role/{role} | Get users by role |
| GET | /api/users/search | Search users by name |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |