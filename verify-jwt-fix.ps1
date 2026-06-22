# ========================================
# JWT Secret Fix Verification Script
# ========================================
# This script verifies that the JWT secret fix is working correctly
# across all microservices in the Food Delivery Platform

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "JWT Secret Fix Verification Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Function to test endpoint
function Test-Endpoint {
    param(
        [string]$Url,
        [string]$Method = "GET",
        [string]$Token = "",
        [string]$Body = "",
        [string]$ServiceName
    )
    
    Write-Host "Testing: $ServiceName" -ForegroundColor Yellow
    Write-Host "  URL: $Url" -ForegroundColor Gray
    
    try {
        $headers = @{
            "Content-Type" = "application/json"
        }
        
        if ($Token -ne "") {
            $headers["Authorization"] = "Bearer $Token"
        }
        
        if ($Method -eq "GET") {
            $response = Invoke-WebRequest -Uri $Url -Method $Method -Headers $headers -UseBasicParsing -ErrorAction Stop
        } else {
            $response = Invoke-WebRequest -Uri $Url -Method $Method -Headers $headers -Body $Body -UseBasicParsing -ErrorAction Stop
        }
        
        if ($response.StatusCode -eq 200) {
            Write-Host "  Result: SUCCESS (200 OK)" -ForegroundColor Green
            return $true, $response.Content
        } else {
            Write-Host "  Result: UNEXPECTED ($($response.StatusCode))" -ForegroundColor Yellow
            return $false, $null
        }
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "  Result: FAILED ($statusCode - $($_.Exception.Message))" -ForegroundColor Red
        return $false, $null
    }
}

# Step 1: Check if services are running
Write-Host "Step 1: Checking if services are running..." -ForegroundColor Cyan
Write-Host ""

$services = @(
    @{Name="Config Server"; Port=8888; Url="http://localhost:8888/actuator/health"},
    @{Name="Eureka Server"; Port=8761; Url="http://localhost:8761/actuator/health"},
    @{Name="User Service"; Port=8085; Url="http://localhost:8085/actuator/health"},
    @{Name="Notification Service"; Port=8081; Url="http://localhost:8081/actuator/health"},
    @{Name="Restaurant Service"; Port=8082; Url="http://localhost:8082/actuator/health"},
    @{Name="Order Service"; Port=8083; Url="http://localhost:8083/actuator/health"},
    @{Name="Payment Service"; Port=8084; Url="http://localhost:8084/actuator/health"}
)

$allServicesRunning = $true
foreach ($service in $services) {
    try {
        $response = Invoke-WebRequest -Uri $service.Url -UseBasicParsing -ErrorAction Stop
        Write-Host "  ✓ $($service.Name) is running on port $($service.Port)" -ForegroundColor Green
    } catch {
        Write-Host "  ✗ $($service.Name) is NOT running on port $($service.Port)" -ForegroundColor Red
        $allServicesRunning = $false
    }
}

Write-Host ""

if (-not $allServicesRunning) {
    Write-Host "ERROR: Not all services are running. Please start all services first." -ForegroundColor Red
    Write-Host "Run: .\start-all-services.ps1" -ForegroundColor Yellow
    exit 1
}

# Step 2: Login and get JWT token
Write-Host "Step 2: Logging in to get JWT token..." -ForegroundColor Cyan
Write-Host ""

$loginBody = @{
    email = "user@example.com"
    password = "password123"
} | ConvertTo-Json

$loginUrl = "http://localhost:8085/api/auth/login"
Write-Host "  Attempting login at: $loginUrl" -ForegroundColor Gray
Write-Host "  Email: user@example.com" -ForegroundColor Gray

try {
    $loginResponse = Invoke-WebRequest -Uri $loginUrl -Method POST -Body $loginBody -ContentType "application/json" -UseBasicParsing -ErrorAction Stop
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    
    if ($token) {
        Write-Host "  ✓ Login successful! Token received." -ForegroundColor Green
        Write-Host "  Token (first 50 chars): $($token.Substring(0, [Math]::Min(50, $token.Length)))..." -ForegroundColor Gray
    } else {
        Write-Host "  ✗ Login failed: No token in response" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "  ✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Please ensure a user exists with email: user@example.com and password: password123" -ForegroundColor Yellow
    Write-Host "  You can register using: POST http://localhost:8085/api/auth/register" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Step 3: Test User Service (should already work)
Write-Host "Step 3: Testing User Service (baseline - should already work)..." -ForegroundColor Cyan
Write-Host ""

$userProfileUrl = "http://localhost:8085/api/users/profile"
$success, $content = Test-Endpoint -Url $userProfileUrl -Method "GET" -Token $token -ServiceName "User Service - Get Profile"

Write-Host ""

if (-not $success) {
    Write-Host "ERROR: User service is not working. This is unexpected." -ForegroundColor Red
    exit 1
}

# Step 4: Test Notification Service (previously 403)
Write-Host "Step 4: Testing Notification Service (previously 403 Forbidden)..." -ForegroundColor Cyan
Write-Host ""

$notificationUrl = "http://localhost:8081/api/notifications"
$notificationSuccess, $notificationContent = Test-Endpoint -Url $notificationUrl -Method "GET" -Token $token -ServiceName "Notification Service - Get Notifications"

Write-Host ""

# Step 5: Test Restaurant Service (previously 403)
Write-Host "Step 5: Testing Restaurant Service (previously 403 Forbidden)..." -ForegroundColor Cyan
Write-Host ""

$restaurantUrl = "http://localhost:8082/api/restaurants"
$restaurantSuccess, $restaurantContent = Test-Endpoint -Url $restaurantUrl -Method "GET" -Token $token -ServiceName "Restaurant Service - Get Restaurants"

Write-Host ""

# Step 6: Test Order Service (previously 403)
Write-Host "Step 6: Testing Order Service (previously 403 Forbidden)..." -ForegroundColor Cyan
Write-Host ""

$orderUrl = "http://localhost:8083/api/orders/user/1"
$orderSuccess, $orderContent = Test-Endpoint -Url $orderUrl -Method "GET" -Token $token -ServiceName "Order Service - Get Orders by User"

Write-Host ""

# Step 7: Test Payment Service (previously 403)
Write-Host "Step 7: Testing Payment Service (previously 403 Forbidden)..." -ForegroundColor Cyan
Write-Host ""

$paymentUrl = "http://localhost:8084/api/payments/order/1"
$paymentSuccess, $paymentContent = Test-Endpoint -Url $paymentUrl -Method "GET" -Token $token -ServiceName "Payment Service - Get Payment by Order"

Write-Host ""

# Step 8: Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "VERIFICATION SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$results = @(
    @{Service="User Service"; Status=$success; Expected="200 OK"},
    @{Service="Notification Service"; Status=$notificationSuccess; Expected="200 OK"},
    @{Service="Restaurant Service"; Status=$restaurantSuccess; Expected="200 OK"},
    @{Service="Order Service"; Status=$orderSuccess; Expected="200 OK (or 404 if no orders)"},
    @{Service="Payment Service"; Status=$paymentSuccess; Expected="200 OK (or 404 if no payment)"}
)

$allPassed = $true
foreach ($result in $results) {
    if ($result.Status) {
        Write-Host "  ✓ $($result.Service): PASSED" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $($result.Service): FAILED" -ForegroundColor Red
        $allPassed = $false
    }
}

Write-Host ""

if ($allPassed) {
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "SUCCESS: JWT SECRET FIX VERIFIED!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "All services are now accepting JWT tokens generated by user-service." -ForegroundColor Green
    Write-Host "The 403 Forbidden errors have been resolved." -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Cyan
    Write-Host "  1. Run full test suite: mvn clean install" -ForegroundColor Yellow
    Write-Host "  2. Test with Postman or your frontend application" -ForegroundColor Yellow
    Write-Host "  3. Monitor logs for any JWT-related errors" -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "FAILURE: SOME TESTS FAILED" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Troubleshooting steps:" -ForegroundColor Cyan
    Write-Host "  1. Check service logs for JWT-related errors" -ForegroundColor Yellow
    Write-Host "  2. Verify Config Server is serving correct jwt.secret:" -ForegroundColor Yellow
    Write-Host "     curl http://localhost:8888/notification-service/default" -ForegroundColor Gray
    Write-Host "     curl http://localhost:8888/restaurant-service/default" -ForegroundColor Gray
    Write-Host "     curl http://localhost:8888/order-service/default" -ForegroundColor Gray
    Write-Host "     curl http://localhost:8888/payment-service/default" -ForegroundColor Gray
    Write-Host "  3. Restart services in correct order (Config Server first)" -ForegroundColor Yellow
    Write-Host "  4. Check JWT_SECRET_FIX_COMPLETE.md for detailed troubleshooting" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}
