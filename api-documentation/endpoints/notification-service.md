# Notification Service API Documentation

## Service Information
- **Service Name:** Notification Service
- **Base URL:** `http://localhost:8085`
- **Port:** 8085
- **Description:** Notification operations

---

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

## Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/notifications | Create notification |
| POST | /api/notifications/send | Send notification |
| POST | /api/notifications/email | Send email notification |
| POST | /api/notifications/sms | Send SMS notification |
| GET | /api/notifications | Get all notifications |
| GET | /api/notifications/{id} | Get notification by ID |
| GET | /api/notifications/user/{userId} | Get notifications by user |
| PUT | /api/notifications/{id} | Update notification |
| PUT | /api/notifications/{id}/read | Mark as read |
| DELETE | /api/notifications/{id} | Delete notification |
| GET | /api/notifications/health | Health check |