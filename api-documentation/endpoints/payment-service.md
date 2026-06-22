# Payment Service API Documentation

## Service Information
- **Service Name:** Payment Service
- **Base URL:** `http://localhost:8083`
- **Port:** 8083
- **Description:** Payment processing

---

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

## Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/payments | Create payment |
| POST | /api/payments/process | Process payment |
| POST | /api/payments/{id}/process | Process payment by ID |
| POST | /api/payments/{id}/refund | Refund payment |
| GET | /api/payments | Get all payments |
| GET | /api/payments/{id} | Get payment by ID |
| GET | /api/payments/order/{orderId} | Get payments by order |
| GET | /api/payments/status/{status} | Get payments by status |
| PUT | /api/payments/{id} | Update payment |
| DELETE | /api/payments/{id} | Delete payment |