# COMPLETE ENDPOINT TESTING GUIDE - FOOD DELIVERY MICROSERVICES

## Prerequisites
- All services running and healthy
- Database sequences fixed (run DATABASE_FIX_SCRIPT.sql)
- Restaurant service restarted (for LazyInitializationException fix)

---

## PHASE 1: USER SERVICE ENDPOINTS (Port 8085)

### 1.1 Register New User
```http
POST http://localhost:8085/api/auth/register
Content-Type: application/json

{
  "username": "testuser2",
  "email": "test2@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User",
  "phone": "9876543210",
  "address": "123 Test Street, Bengaluru"
}
```
**Expected**: 201 Created, save `userId`

### 1.2 Login User
```http
POST http://localhost:8085/api/auth/login
Content-Type: application/json

{
  "email": "test2@example.com",
  "password": "password123"
}
```
**Expected**: 200 OK, returns user details

### 1.3 Get All Users
```http
GET http://localhost:8085/api/users
```
**Expected**: 200 OK, array of users

### 1.4 Get User by ID
```http
GET http://localhost:8085/api/users/{userId}
```
**Expected**: 200 OK, user object

### 1.5 Get User by Email
```http
GET http://localhost:8085/api/users/email/test2@example.com
```
**Expected**: 200 OK, user object

### 1.6 Get Users by Role
```http
GET http://localhost:8085/api/users/role/CUSTOMER
```
**Expected**: 200 OK, array of customers

### 1.7 Update User
```http
PUT http://localhost:8085/api/users/{userId}
Content-Type: application/json

{
  "username": "testuser2",
  "email": "test2@example.com",
  "password": "password123",
  "firstName": "Test Updated",
  "lastName": "User Updated",
  "phone": "9876543210",
  "address": "456 Updated Street, Bengaluru",
  "role": "CUSTOMER"
}
```
**Expected**: 200 OK, updated user

### 1.8 Search Users by Name
```http
GET http://localhost:8085/api/users/search?name=Test
```
**Expected**: 200 OK, matching users

### 1.9 Delete User
```http
DELETE http://localhost:8085/api/users/{userId}
```
**Expected**: 204 No Content

---

## PHASE 2: RESTAURANT SERVICE ENDPOINTS (Port 8082)

### 2.1 Create Restaurant
```http
POST http://localhost:8082/api/restaurants
Content-Type: application/json

{
  "name": "Spice Garden",
  "cuisine": "INDIAN",
  "description": "Authentic Indian cuisine",
  "address": {
    "street": "123 MG Road",
    "city": "Bengaluru",
    "state": "Karnataka",
    "zipCode": "560001",
    "country": "India"
  },
  "phone": "+919876543211",
  "email": "spicegarden@example.com",
  "website": "https://spicegarden.com",
  "rating": 4.5,
  "isActive": true,
  "deliveryRadius": 5.0,
  "minimumOrderAmount": 100.00,
  "deliveryFee": 50.00
}
```
**Expected**: 201 Created, save `restaurantId`

### 2.2 Get All Restaurants
```http
GET http://localhost:8082/api/restaurants
```
**Expected**: 200 OK, array of active restaurants

### 2.3 Get Restaurant by ID
```http
GET http://localhost:8082/api/restaurants/{restaurantId}
```
**Expected**: 200 OK, restaurant object

### 2.4 Get Restaurants by Cuisine
```http
GET http://localhost:8082/api/restaurants/cuisine/INDIAN
```
**Expected**: 200 OK, array of Indian restaurants

### 2.5 Search Restaurants
```http
GET http://localhost:8082/api/restaurants/search?query=Spice
```
**Expected**: 200 OK, matching restaurants

### 2.6 Update Restaurant
```http
PUT http://localhost:8082/api/restaurants/{restaurantId}
Content-Type: application/json

{
  "name": "Spice Garden Updated",
  "cuisine": "INDIAN",
  "address": {
    "street": "123 MG Road",
    "city": "Bengaluru",
    "state": "Karnataka",
    "zipCode": "560001",
    "country": "India"
  },
  "phone": "+919876543211",
  "rating": 4.7,
  "isActive": true
}
```
**Expected**: 200 OK, updated restaurant

### 2.7 Activate Restaurant
```http
PUT http://localhost:8082/api/restaurants/{restaurantId}/activate
```
**Expected**: 200 OK, activated restaurant

### 2.8 Deactivate Restaurant
```http
PUT http://localhost:8082/api/restaurants/{restaurantId}/deactivate
```
**Expected**: 200 OK, deactivated restaurant

### 2.9 Delete Restaurant
```http
DELETE http://localhost:8082/api/restaurants/{restaurantId}
```
**Expected**: 204 No Content

---

## PHASE 3: MENU ITEM ENDPOINTS (Port 8082)

### 3.1 Add Menu Item to Restaurant
```http
POST http://localhost:8082/api/restaurants/{restaurantId}/menu
Content-Type: application/json

{
  "name": "Butter Chicken",
  "description": "Creamy tomato-based chicken curry",
  "price": 350.00,
  "category": "Main Course",
  "isAvailable": true
}
```
**Expected**: 201 Created, save `menuItemId`

### 3.2 Get Available Menu Items for Restaurant
```http
GET http://localhost:8082/api/restaurants/{restaurantId}/menu
```
**Expected**: 200 OK, array of available menu items

### 3.3 Get All Menu Items for Restaurant
```http
GET http://localhost:8082/api/restaurants/{restaurantId}/menu/all
```
**Expected**: 200 OK, array of all menu items (including unavailable)

### 3.4 Get Menu Item by ID
```http
GET http://localhost:8082/api/restaurants/{restaurantId}/menu/{menuItemId}
```
**Expected**: 200 OK, menu item object

### 3.5 Update Menu Item
```http
PUT http://localhost:8082/api/restaurants/{restaurantId}/menu/{menuItemId}
Content-Type: application/json

{
  "name": "Butter Chicken Special",
  "description": "Premium creamy tomato-based chicken curry",
  "price": 400.00,
  "category": "Main Course",
  "isAvailable": true
}
```
**Expected**: 200 OK, updated menu item

### 3.6 Toggle Menu Item Availability
```http
PUT http://localhost:8082/api/restaurants/{restaurantId}/menu/{menuItemId}/availability
```
**Expected**: 200 OK, menu item with toggled availability

### 3.7 Delete Menu Item
```http
DELETE http://localhost:8082/api/restaurants/{restaurantId}/menu/{menuItemId}
```
**Expected**: 204 No Content

### 3.8 Get All Menu Items (Direct)
```http
GET http://localhost:8082/api/menu-items
```
**Expected**: 200 OK, all menu items across all restaurants

### 3.9 Get Menu Item by ID (Direct)
```http
GET http://localhost:8082/api/menu-items/{menuItemId}
```
**Expected**: 200 OK, menu item object

### 3.10 Update Menu Item (Direct)
```http
PUT http://localhost:8082/api/menu-items/{menuItemId}
Content-Type: application/json

{
  "name": "Butter Chicken Deluxe",
  "description": "Extra creamy chicken curry",
  "price": 450.00,
  "category": "Main Course",
  "isAvailable": true
}
```
**Expected**: 200 OK, updated menu item

### 3.11 Delete Menu Item (Direct)
```http
DELETE http://localhost:8082/api/menu-items/{menuItemId}
```
**Expected**: 204 No Content

---

## PHASE 4: ORDER SERVICE ENDPOINTS (Port 8083)

### 4.1 Create Order
```http
POST http://localhost:8083/api/orders
Content-Type: application/json

{
  "userId": {userId},
  "restaurantId": {restaurantId},
  "items": [
    {
      "menuItemId": {menuItemId},
      "quantity": 2,
      "price": 350.00
    }
  ],
  "deliveryAddress": "HSR Layout, Bengaluru, Karnataka, 560102"
}
```
**Expected**: 201 Created, save `orderId`

### 4.2 Create Order (Legacy)
```http
POST http://localhost:8083/api/orders/legacy
Content-Type: application/json

{
  "userId": {userId},
  "restaurantId": {restaurantId},
  "items": [
    {
      "menuItemId": {menuItemId},
      "quantity": 1,
      "price": 350.00
    }
  ],
  "deliveryAddress": "Koramangala, Bengaluru",
  "paymentMethod": "ONLINE"
}
```
**Expected**: 201 Created

### 4.3 Get All Orders
```http
GET http://localhost:8083/api/orders
```
**Expected**: 200 OK, array of all orders

### 4.4 Get Order by ID
```http
GET http://localhost:8083/api/orders/{orderId}
```
**Expected**: 200 OK, order object

### 4.5 Get Orders by User ID
```http
GET http://localhost:8083/api/orders/user/{userId}
```
**Expected**: 200 OK, array of user's orders

### 4.6 Get Orders by Restaurant ID
```http
GET http://localhost:8083/api/orders/restaurant/{restaurantId}
```
**Expected**: 200 OK, array of restaurant's orders

### 4.7 Get Orders by Status
```http
GET http://localhost:8083/api/orders/status/CREATED
```
**Expected**: 200 OK, array of orders with CREATED status

**Available Statuses**: CREATED, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

### 4.8 Update Order
```http
PUT http://localhost:8083/api/orders/{orderId}
Content-Type: application/json

{
  "userId": {userId},
  "restaurantId": {restaurantId},
  "deliveryAddress": "Updated Address, Bengaluru",
  "status": "CONFIRMED",
  "totalAmount": 700.00
}
```
**Expected**: 200 OK, updated order

### 4.9 Update Order Status
```http
PUT http://localhost:8083/api/orders/{orderId}/status
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```
**Expected**: 200 OK, order with updated status

### 4.10 Confirm Order
```http
POST http://localhost:8083/api/orders/{orderId}/confirm
```
**Expected**: 200 OK, confirmed order

### 4.11 Cancel Order
```http
POST http://localhost:8083/api/orders/{orderId}/cancel
```
**Expected**: 200 OK, cancelled order

### 4.12 Deliver Order
```http
POST http://localhost:8083/api/orders/{orderId}/deliver
```
**Expected**: 200 OK, delivered order

### 4.13 Get Order Tracking
```http
GET http://localhost:8083/api/orders/{orderId}/tracking
```
**Expected**: 200 OK, order tracking information

### 4.14 Delete Order
```http
DELETE http://localhost:8083/api/orders/{orderId}
```
**Expected**: 204 No Content

---

## PHASE 5: PAYMENT SERVICE ENDPOINTS (Port 8084)

### 5.1 Create Payment
```http
POST http://localhost:8084/api/payments
Content-Type: application/json

{
  "orderId": {orderId},
  "userId": {userId},
  "amount": 700.00,
  "paymentMethod": "ONLINE",
  "transactionId": "TXN" + Date.now()
}
```
**Expected**: 201 Created, save `paymentId`

**Available Payment Methods**: CASH, ONLINE, CARD, UPI, WALLET

### 5.2 Process Payment
```http
POST http://localhost:8084/api/payments/process
Content-Type: application/json

{
  "orderId": {orderId},
  "userId": {userId},
  "amount": 700.00,
  "paymentMethod": "ONLINE",
  "transactionId": "TXN" + Date.now()
}
```
**Expected**: 200 OK, payment response

### 5.3 Process Payment by ID
```http
POST http://localhost:8084/api/payments/{paymentId}/process
```
**Expected**: 200 OK, processed payment

### 5.4 Get All Payments
```http
GET http://localhost:8084/api/payments
```
**Expected**: 200 OK, array of all payments

### 5.5 Get Payment by ID
```http
GET http://localhost:8084/api/payments/{paymentId}
```
**Expected**: 200 OK, payment object

### 5.6 Get Payments by Order ID
```http
GET http://localhost:8084/api/payments/order/{orderId}
```
**Expected**: 200 OK, array of payments for the order

### 5.7 Get Payments by Status
```http
GET http://localhost:8084/api/payments/status/PENDING
```
**Expected**: 200 OK, array of payments with PENDING status

**Available Statuses**: PENDING, SUCCESS, FAILED, REFUNDED

### 5.8 Update Payment
```http
PUT http://localhost:8084/api/payments/{paymentId}
Content-Type: application/json

{
  "orderId": {orderId},
  "userId": {userId},
  "amount": 750.00,
  "paymentMethod": "CARD",
  "transactionId": "TXN_UPDATED",
  "status": "SUCCESS"
}
```
**Expected**: 200 OK, updated payment

### 5.9 Refund Payment
```http
POST http://localhost:8084/api/payments/{paymentId}/refund
```
**Expected**: 200 OK, refunded payment

**Note**: Can only refund payments with SUCCESS or COMPLETED status

### 5.10 Delete Payment
```http
DELETE http://localhost:8084/api/payments/{paymentId}
```
**Expected**: 204 No Content

---

## PHASE 6: NOTIFICATION SERVICE ENDPOINTS (Port 8081)

### 6.1 Create Notification
```http
POST http://localhost:8081/api/notifications
Content-Type: application/json

{
  "userId": {userId},
  "title": "Order Placed",
  "message": "Your order has been placed successfully",
  "type": "ORDER_UPDATE"
}
```
**Expected**: 201 Created, save `notificationId`

**Available Types**: ORDER_UPDATE, PAYMENT_UPDATE, DELIVERY_UPDATE, PROMOTION, SYSTEM

### 6.2 Send Notification
```http
POST http://localhost:8081/api/notifications/send
Content-Type: application/json

{
  "userId": {userId},
  "title": "Order Confirmed",
  "message": "Your order is being prepared",
  "type": "ORDER_UPDATE"
}
```
**Expected**: 200 OK, sent notification

### 6.3 Send Email Notification
```http
POST http://localhost:8081/api/notifications/email
Content-Type: application/json

{
  "userId": {userId},
  "title": "Order Receipt",
  "message": "Thank you for your order. Receipt attached.",
  "type": "ORDER_UPDATE"
}
```
**Expected**: 200 OK, "Email notification sent successfully"

### 6.4 Send SMS Notification
```http
POST http://localhost:8081/api/notifications/sms
Content-Type: application/json

{
  "userId": {userId},
  "title": "Order Status",
  "message": "Your order is out for delivery",
  "type": "DELIVERY_UPDATE"
}
```
**Expected**: 200 OK, "SMS notification sent successfully"

### 6.5 Get All Notifications
```http
GET http://localhost:8081/api/notifications
```
**Expected**: 200 OK, array of all notifications

### 6.6 Get Notification by ID
```http
GET http://localhost:8081/api/notifications/{notificationId}
```
**Expected**: 200 OK, notification object

### 6.7 Get Notifications by User ID
```http
GET http://localhost:8081/api/notifications/user/{userId}
```
**Expected**: 200 OK, array of user's notifications

### 6.8 Update Notification
```http
PUT http://localhost:8081/api/notifications/{notificationId}
Content-Type: application/json

{
  "userId": {userId},
  "title": "Order Delivered",
  "message": "Your order has been delivered successfully",
  "type": "DELIVERY_UPDATE"
}
```
**Expected**: 200 OK, updated notification

### 6.9 Mark Notification as Read
```http
PUT http://localhost:8081/api/notifications/{notificationId}/read
```
**Expected**: 200 OK, notification marked as read

### 6.10 Delete Notification
```http
DELETE http://localhost:8081/api/notifications/{notificationId}
```
**Expected**: 204 No Content

### 6.11 Health Check
```http
GET http://localhost:8081/api/notifications/health
```
**Expected**: 200 OK, "Notification Service is running"

---

## PHASE 7: API GATEWAY TESTING (Port 8080)

Test all endpoints through API Gateway by replacing service ports with 8080:

### Example Gateway URLs:
```
# User Service
http://localhost:8080/api/auth/register
http://localhost:8080/api/users

# Restaurant Service
http://localhost:8080/api/restaurants
http://localhost:8080/api/restaurants/{id}/menu

# Order Service
http://localhost:8080/api/orders

# Payment Service
http://localhost:8080/api/payments

# Notification Service
http://localhost:8080/api/notifications
```

**Expected**: All endpoints should work identically through gateway

---

## COMPLETE E2E WORKFLOW TEST

### Step 1: Register User
```http
POST http://localhost:8085/api/auth/register
```
Save `userId`

### Step 2: Login User
```http
POST http://localhost:8085/api/auth/login
```
Verify login successful

### Step 3: Create Restaurant
```http
POST http://localhost:8082/api/restaurants
```
Save `restaurantId`

### Step 4: Add Menu Items
```http
POST http://localhost:8082/api/restaurants/{restaurantId}/menu
```
Save `menuItemId`

### Step 5: Browse Restaurants
```http
GET http://localhost:8082/api/restaurants
```
Verify restaurant appears

### Step 6: View Menu
```http
GET http://localhost:8082/api/restaurants/{restaurantId}/menu
```
Verify menu items appear

### Step 7: Place Order
```http
POST http://localhost:8083/api/orders
```
Save `orderId`

### Step 8: Create Payment
```http
POST http://localhost:8084/api/payments
```
Save `paymentId`

### Step 9: Process Payment
```http
POST http://localhost:8084/api/payments/{paymentId}/process
```
Verify payment successful

### Step 10: Confirm Order
```http
POST http://localhost:8083/api/orders/{orderId}/confirm
```
Verify order confirmed

### Step 11: Send Notification
```http
POST http://localhost:8081/api/notifications/send
```
Verify notification created

### Step 12: Check User Notifications
```http
GET http://localhost:8081/api/notifications/user/{userId}
```
Verify notification appears

### Step 13: Track Order
```http
GET http://localhost:8083/api/orders/{orderId}/tracking
```
Verify order status

### Step 14: Deliver Order
```http
POST http://localhost:8083/api/orders/{orderId}/deliver
```
Verify order delivered

### Step 15: View Order History
```http
GET http://localhost:8083/api/orders/user/{userId}
```
Verify order appears in history

---

## POWERSHELL TESTING SCRIPT

```powershell
# Set base URLs
$userServiceUrl = "http://localhost:8085"
$restaurantServiceUrl = "http://localhost:8082"
$orderServiceUrl = "http://localhost:8083"
$paymentServiceUrl = "http://localhost:8084"
$notificationServiceUrl = "http://localhost:8081"

# Step 1: Register User
$registerBody = @'
{
  "username": "e2etest",
  "email": "e2e@example.com",
  "password": "password123",
  "firstName": "E2E",
  "lastName": "Test",
  "phone": "9876543210",
  "address": "Test Address"
}
'@

$headers = @{'Content-Type'='application/json'}
$registerResponse = Invoke-RestMethod -Uri "$userServiceUrl/api/auth/register" -Method POST -Headers $headers -Body $registerBody
$userId = $registerResponse.userId
Write-Host "User registered with ID: $userId"

# Step 2: Create Restaurant
$restaurantBody = @"
{
  "name": "E2E Test Restaurant",
  "cuisine": "INDIAN",
  "address": {
    "street": "Test Street",
    "city": "Bengaluru",
    "state": "Karnataka",
    "zipCode": "560001",
    "country": "India"
  },
  "phone": "+919876543211",
  "email": "test@restaurant.com",
  "rating": 4.5,
  "isActive": true
}
"@

$restaurantResponse = Invoke-RestMethod -Uri "$restaurantServiceUrl/api/restaurants" -Method POST -Headers $headers -Body $restaurantBody
$restaurantId = $restaurantResponse.id
Write-Host "Restaurant created with ID: $restaurantId"

# Step 3: Add Menu Item
$menuItemBody = @"
{
  "name": "Test Dish",
  "description": "Test Description",
  "price": 100.00,
  "category": "Main Course",
  "isAvailable": true
}
"@

$menuItemResponse = Invoke-RestMethod -Uri "$restaurantServiceUrl/api/restaurants/$restaurantId/menu" -Method POST -Headers $headers -Body $menuItemBody
$menuItemId = $menuItemResponse.id
Write-Host "Menu item created with ID: $menuItemId"

# Step 4: Place Order
$orderBody = @"
{
  "userId": $userId,
  "restaurantId": $restaurantId,
  "items": [
    {
      "menuItemId": $menuItemId,
      "quantity": 2,
      "price": 100.00
    }
  ],
  "deliveryAddress": "Test Delivery Address"
}
"@

$orderResponse = Invoke-RestMethod -Uri "$orderServiceUrl/api/orders" -Method POST -Headers $headers -Body $orderBody
$orderId = $orderResponse.id
Write-Host "Order placed with ID: $orderId"

# Step 5: Create Payment
$paymentBody = @"
{
  "orderId": $orderId,
  "userId": $userId,
  "amount": 200.00,
  "paymentMethod": "ONLINE",
  "transactionId": "TXN_E2E_TEST"
}
"@

$paymentResponse = Invoke-RestMethod -Uri "$paymentServiceUrl/api/payments" -Method POST -Headers $headers -Body $paymentBody
$paymentId = $paymentResponse.id
Write-Host "Payment created with ID: $paymentId"

# Step 6: Create Notification
$notificationBody = @"
{
  "userId": $userId,
  "title": "E2E Test Notification",
  "message": "Your order is being processed",
  "type": "ORDER_UPDATE"
}
"@

$notificationResponse = Invoke-RestMethod -Uri "$notificationServiceUrl/api/notifications" -Method POST -Headers $headers -Body $notificationBody
$notificationId = $notificationResponse.id
Write-Host "Notification created with ID: $notificationId"

Write-Host "`nE2E Test Completed Successfully!"
Write-Host "User ID: $userId"
Write-Host "Restaurant ID: $restaurantId"
Write-Host "Menu Item ID: $menuItemId"
Write-Host "Order ID: $orderId"
Write-Host "Payment ID: $paymentId"
Write-Host "Notification ID: $notificationId"
```

---

## TROUBLESHOOTING

### Issue: 409 Conflict - Duplicate Key
**Solution**: Run DATABASE_FIX_SCRIPT.sql

### Issue: 500 Internal Server Error - LazyInitializationException
**Solution**: Restart restaurant-service after applying the EAGER fetch fix

### Issue: 400 Validation Error
**Solution**: Check request body matches DTO structure exactly

### Issue: 404 Not Found
**Solution**: Verify service is running and endpoint path is correct

### Issue: Connection Refused
**Solution**: Check if service is running on the expected port

---

## VERIFICATION CHECKLIST

- [ ] All 8 services health checks pass
- [ ] Database sequences reset successfully
- [ ] Restaurant service restarted
- [ ] User registration works
- [ ] User login works
- [ ] Restaurant creation works
- [ ] Menu item creation works
- [ ] Order placement works
- [ ] Payment creation works
- [ ] Notification creation works
- [ ] All GET endpoints return data
- [ ] All PUT endpoints update data
- [ ] All DELETE endpoints remove data
- [ ] All endpoints work through API Gateway
- [ ] Complete E2E workflow succeeds

---

**Total Endpoints to Test**: 70+  
**Estimated Testing Time**: 2-3 hours for complete manual testing  
**Recommended**: Use Postman Collection for automated testing
