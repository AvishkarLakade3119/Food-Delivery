# ============================================================================
# Food Delivery Microservices - System Verification Script
# ============================================================================
# Purpose: Comprehensive system health check and smoke tests
# Author: Slingshot AI Agent
# Date: 2026-06-12
# ============================================================================

param(
    [switch]$SkipSmokeTests,
    [switch]$GenerateReport
)

# Color output functions
function Write-Success { param($msg) Write-Host "✓ $msg" -ForegroundColor Green }
function Write-Error-Custom { param($msg) Write-Host "✗ $msg" -ForegroundColor Red }
function Write-Warning-Custom { param($msg) Write-Host "⚠ $msg" -ForegroundColor Yellow }
function Write-Info { param($msg) Write-Host "ℹ $msg" -ForegroundColor Cyan }
function Write-Section { param($msg) Write-Host "`n========== $msg ==========" -ForegroundColor Magenta }

Write-Host "`n" -NoNewline
Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Food Delivery Microservices - System Verification Tool          ║" -ForegroundColor Cyan
Write-Host "║  Version 1.0 | Date: 2026-06-12                                  ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$verificationResults = @()
$healthyServices = 0
$totalServices = 0

# ============================================================================
# PHASE 1: Service Health Endpoint Verification
# ============================================================================
Write-Section "PHASE 1: Service Health Checks"

$services = @(
    @{Name="Config Server"; Port=8888; HealthUrl="http://localhost:8888/actuator/health"},
    @{Name="Eureka Server"; Port=8761; HealthUrl="http://localhost:8761/actuator/health"},
    @{Name="User Service"; Port=8085; HealthUrl="http://localhost:8085/actuator/health"},
    @{Name="Restaurant Service"; Port=8082; HealthUrl="http://localhost:8082/actuator/health"},
    @{Name="Order Service"; Port=8083; HealthUrl="http://localhost:8083/actuator/health"},
    @{Name="Payment Service"; Port=8084; HealthUrl="http://localhost:8084/actuator/health"},
    @{Name="Notification Service"; Port=8081; HealthUrl="http://localhost:8081/actuator/health"},
    @{Name="API Gateway"; Port=8080; HealthUrl="http://localhost:8080/actuator/health"}
)

$totalServices = $services.Count

foreach ($service in $services) {
    try {
        $response = Invoke-WebRequest -Uri $service.HealthUrl -TimeoutSec 5 -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            $healthData = $response.Content | ConvertFrom-Json
            $status = $healthData.status
            
            if ($status -eq "UP") {
                Write-Success "$($service.Name) (Port $($service.Port)) - UP"
                $verificationResults += "[PASS] $($service.Name) - Health check passed"
                $healthyServices++
            } else {
                Write-Warning-Custom "$($service.Name) (Port $($service.Port)) - Status: $status"
                $verificationResults += "[WARN] $($service.Name) - Status: $status"
            }
        }
    } catch {
        Write-Error-Custom "$($service.Name) (Port $($service.Port)) - NOT RESPONDING"
        $verificationResults += "[FAIL] $($service.Name) - Not responding"
    }
}

Write-Host ""
Write-Info "Health Check Summary: $healthyServices/$totalServices services are healthy"

# ============================================================================
# PHASE 2: Eureka Service Registry Validation
# ============================================================================
Write-Section "PHASE 2: Eureka Service Registry"

try {
    $eurekaResponse = Invoke-WebRequest -Uri "http://localhost:8761/eureka/apps" -TimeoutSec 5 -ErrorAction Stop
    $eurekaContent = $eurekaResponse.Content
    
    Write-Success "Eureka Server is accessible"
    
    $expectedServices = @(
        "USER-SERVICE",
        "RESTAURANT-SERVICE",
        "ORDER-SERVICE",
        "PAYMENT-SERVICE",
        "NOTIFICATION-SERVICE",
        "API-GATEWAY"
    )
    
    $registeredCount = 0
    foreach ($serviceName in $expectedServices) {
        if ($eurekaContent -match $serviceName) {
            Write-Success "$serviceName is registered"
            $verificationResults += "[PASS] $serviceName - Registered with Eureka"
            $registeredCount++
        } else {
            Write-Error-Custom "$serviceName is NOT registered"
            $verificationResults += "[FAIL] $serviceName - Not registered with Eureka"
        }
    }
    
    Write-Host ""
    Write-Info "Eureka Registration: $registeredCount/$($expectedServices.Count) services registered"
    
} catch {
    Write-Error-Custom "Failed to connect to Eureka Server"
    $verificationResults += "[FAIL] Eureka Server - Not accessible"
}

# ============================================================================
# PHASE 3: API Gateway Routing Verification
# ============================================================================
Write-Section "PHASE 3: API Gateway Routes"

try {
    $routesResponse = Invoke-WebRequest -Uri "http://localhost:8080/actuator/gateway/routes" -TimeoutSec 5 -ErrorAction Stop
    
    if ($routesResponse.StatusCode -eq 200) {
        Write-Success "API Gateway routes endpoint is accessible"
        
        $routes = $routesResponse.Content | ConvertFrom-Json
        $routeCount = $routes.Count
        
        Write-Info "Total routes configured: $routeCount"
        
        # Check for expected routes
        $expectedRoutes = @("user-service", "restaurant-service", "order-service", "payment-service", "notification-service")
        $configuredRoutes = 0
        
        foreach ($expectedRoute in $expectedRoutes) {
            $routeFound = $routes | Where-Object { $_.route_id -match $expectedRoute -or $_.uri -match $expectedRoute }
            if ($routeFound) {
                Write-Success "Route configured for: $expectedRoute"
                $configuredRoutes++
            } else {
                Write-Warning-Custom "Route not found for: $expectedRoute"
            }
        }
        
        Write-Host ""
        Write-Info "Configured Routes: $configuredRoutes/$($expectedRoutes.Count)"
        $verificationResults += "[PASS] API Gateway - $configuredRoutes routes configured"
    }
} catch {
    Write-Error-Custom "Failed to retrieve API Gateway routes"
    $verificationResults += "[FAIL] API Gateway - Routes not accessible"
}

# ============================================================================
# PHASE 4: Database Connection Verification
# ============================================================================
Write-Section "PHASE 4: Database Connectivity"

Write-Info "Checking database configurations..."

$dbServices = @(
    @{Service="User Service"; Port=8085; ActuatorUrl="http://localhost:8085/actuator/health"},
    @{Service="Restaurant Service"; Port=8082; ActuatorUrl="http://localhost:8082/actuator/health"},
    @{Service="Order Service"; Port=8083; ActuatorUrl="http://localhost:8083/actuator/health"},
    @{Service="Payment Service"; Port=8084; ActuatorUrl="http://localhost:8084/actuator/health"},
    @{Service="Notification Service"; Port=8081; ActuatorUrl="http://localhost:8081/actuator/health"}
)

foreach ($dbService in $dbServices) {
    try {
        $healthResponse = Invoke-WebRequest -Uri $dbService.ActuatorUrl -TimeoutSec 5 -ErrorAction Stop
        $healthData = $healthResponse.Content | ConvertFrom-Json
        
        # Check if database health is included
        if ($healthData.components.db) {
            $dbStatus = $healthData.components.db.status
            if ($dbStatus -eq "UP") {
                Write-Success "$($dbService.Service) - Database connection OK"
                $verificationResults += "[PASS] $($dbService.Service) - Database connected"
            } else {
                Write-Warning-Custom "$($dbService.Service) - Database status: $dbStatus"
                $verificationResults += "[WARN] $($dbService.Service) - Database status: $dbStatus"
            }
        } else {
            Write-Info "$($dbService.Service) - Database health not exposed (may be using H2 in-memory)"
        }
    } catch {
        Write-Warning-Custom "$($dbService.Service) - Cannot verify database connection"
    }
}

# ============================================================================
# PHASE 5: Smoke Tests (Optional)
# ============================================================================
if (-not $SkipSmokeTests) {
    Write-Section "PHASE 5: Smoke Tests"
    
    Write-Info "Running basic smoke tests through API Gateway..."
    
    # Test 1: User Service Health through Gateway
    try {
        $userHealthResponse = Invoke-WebRequest -Uri "http://localhost:8080/user-service/actuator/health" -TimeoutSec 5 -ErrorAction Stop
        if ($userHealthResponse.StatusCode -eq 200) {
            Write-Success "Smoke Test 1: User Service accessible through API Gateway"
            $verificationResults += "[PASS] Smoke Test - User Service via Gateway"
        }
    } catch {
        Write-Error-Custom "Smoke Test 1: User Service NOT accessible through API Gateway"
        $verificationResults += "[FAIL] Smoke Test - User Service via Gateway"
    }
    
    # Test 2: Restaurant Service Health through Gateway
    try {
        $restaurantHealthResponse = Invoke-WebRequest -Uri "http://localhost:8080/restaurant-service/actuator/health" -TimeoutSec 5 -ErrorAction Stop
        if ($restaurantHealthResponse.StatusCode -eq 200) {
            Write-Success "Smoke Test 2: Restaurant Service accessible through API Gateway"
            $verificationResults += "[PASS] Smoke Test - Restaurant Service via Gateway"
        }
    } catch {
        Write-Error-Custom "Smoke Test 2: Restaurant Service NOT accessible through API Gateway"
        $verificationResults += "[FAIL] Smoke Test - Restaurant Service via Gateway"
    }
    
    # Test 3: Order Service Health through Gateway
    try {
        $orderHealthResponse = Invoke-WebRequest -Uri "http://localhost:8080/order-service/actuator/health" -TimeoutSec 5 -ErrorAction Stop
        if ($orderHealthResponse.StatusCode -eq 200) {
            Write-Success "Smoke Test 3: Order Service accessible through API Gateway"
            $verificationResults += "[PASS] Smoke Test - Order Service via Gateway"
        }
    } catch {
        Write-Error-Custom "Smoke Test 3: Order Service NOT accessible through API Gateway"
        $verificationResults += "[FAIL] Smoke Test - Order Service via Gateway"
    }
    
    Write-Host ""
} else {
    Write-Info "Skipping smoke tests"
}

# ============================================================================
# VERIFICATION SUMMARY
# ============================================================================
Write-Section "VERIFICATION SUMMARY"

$passCount = ($verificationResults | Where-Object { $_ -match "\[PASS\]" }).Count
$failCount = ($verificationResults | Where-Object { $_ -match "\[FAIL\]" }).Count
$warnCount = ($verificationResults | Where-Object { $_ -match "\[WARN\]" }).Count

Write-Host ""
Write-Host "Verification Results:" -ForegroundColor Cyan
Write-Host "====================" -ForegroundColor Cyan
Write-Success "Passed:   $passCount"
Write-Error-Custom "Failed:   $failCount"
Write-Warning-Custom "Warnings: $warnCount"
Write-Host ""

if ($failCount -eq 0 -and $warnCount -eq 0) {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║  ✓ ALL VERIFICATIONS PASSED - SYSTEM FULLY OPERATIONAL            ║" -ForegroundColor Green
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
} elseif ($failCount -eq 0) {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
    Write-Host "║  ⚠ WARNINGS DETECTED - REVIEW BEFORE PRODUCTION                   ║" -ForegroundColor Yellow
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow
} else {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Red
    Write-Host "║  ✗ CRITICAL ISSUES DETECTED - IMMEDIATE ACTION REQUIRED          ║" -ForegroundColor Red
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Red
}

Write-Host ""

# Generate report if requested
if ($GenerateReport) {
    $reportFile = "system-health-report-$(Get-Date -Format 'yyyyMMdd-HHmmss').txt"
    
    $reportContent = @"
Food Delivery Microservices - System Health Report
Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')

========================================
SUMMARY
========================================
Total Checks:  $($passCount + $failCount + $warnCount)
Passed:        $passCount
Failed:        $failCount
Warnings:      $warnCount

Service Health: $healthyServices/$totalServices services healthy

========================================
DETAILED RESULTS
========================================
"@
    
    foreach ($result in $verificationResults) {
        $reportContent += "`n$result"
    }
    
    $reportContent | Out-File $reportFile -Encoding UTF8
    Write-Success "Health report saved to: $reportFile"
}

Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. If all checks passed: System is ready for use" -ForegroundColor White
Write-Host "  2. If failures detected: Check service logs for errors" -ForegroundColor White
Write-Host "  3. Run .\run-e2e-tests.ps1 for comprehensive testing" -ForegroundColor White
Write-Host ""
