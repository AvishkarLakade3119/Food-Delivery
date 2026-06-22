# ============================================================================
# Food Delivery Microservices - Complete Diagnostic Script
# ============================================================================
# Purpose: Comprehensive health check for all services, configurations, and dependencies
# Author: Slingshot AI Agent
# Date: 2026-06-12
# ============================================================================

param(
    [switch]$Verbose,
    [switch]$ExportReport
)

# Color output functions
function Write-Success { param($msg) Write-Host "✓ $msg" -ForegroundColor Green }
function Write-Error-Custom { param($msg) Write-Host "✗ $msg" -ForegroundColor Red }
function Write-Warning-Custom { param($msg) Write-Host "⚠ $msg" -ForegroundColor Yellow }
function Write-Info { param($msg) Write-Host "ℹ $msg" -ForegroundColor Cyan }
function Write-Section { param($msg) Write-Host "`n========== $msg ==========" -ForegroundColor Magenta }

# Initialize diagnostic report
$diagnosticReport = @()
$errorCount = 0
$warningCount = 0
$successCount = 0

Write-Host "`n" -NoNewline
Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Food Delivery Microservices - Complete Diagnostic Tool          ║" -ForegroundColor Cyan
Write-Host "║  Version 1.0 | Date: 2026-06-12                                  ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# PHASE 1: Java and Maven Installation Check
# ============================================================================
Write-Section "PHASE 1: Java and Maven Installation"

try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    if ($javaVersion -match 'version "(\d+)') {
        $javaVersionNumber = $matches[1]
        if ([int]$javaVersionNumber -ge 17) {
            Write-Success "Java $javaVersionNumber detected (Required: Java 17+)"
            $diagnosticReport += "[SUCCESS] Java Version: $javaVersion"
            $successCount++
        } else {
            Write-Error-Custom "Java $javaVersionNumber detected (Required: Java 17+)"
            $diagnosticReport += "[ERROR] Java Version: $javaVersion - Upgrade to Java 17 or higher"
            $errorCount++
        }
    }
} catch {
    Write-Error-Custom "Java not found or not in PATH"
    $diagnosticReport += "[ERROR] Java not installed or not in PATH"
    $errorCount++
}

try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    if ($mavenVersion -match 'Apache Maven (\d+\.\d+\.\d+)') {
        Write-Success "Maven detected: $($matches[1])"
        $diagnosticReport += "[SUCCESS] Maven Version: $mavenVersion"
        $successCount++
    }
} catch {
    Write-Error-Custom "Maven not found or not in PATH"
    $diagnosticReport += "[ERROR] Maven not installed or not in PATH"
    $errorCount++
}

# ============================================================================
# PHASE 2: Port Availability Check
# ============================================================================
Write-Section "PHASE 2: Port Availability Check"

$requiredPorts = @{
    8888 = "Config Server"
    8761 = "Eureka Server"
    8080 = "API Gateway (Primary)"
    9090 = "API Gateway (Secondary)"
    8081 = "Notification Service"
    8082 = "Restaurant Service"
    8083 = "Order Service"
    8084 = "Payment Service"
    8085 = "User Service"
}

foreach ($port in $requiredPorts.Keys | Sort-Object) {
    $serviceName = $requiredPorts[$port]
    $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    
    if ($connection) {
        $process = Get-Process -Id $connection.OwningProcess -ErrorAction SilentlyContinue
        Write-Warning-Custom "Port $port ($serviceName) is IN USE by process: $($process.ProcessName) (PID: $($process.Id))"
        $diagnosticReport += "[WARNING] Port $port ($serviceName) occupied by $($process.ProcessName)"
        $warningCount++
    } else {
        Write-Success "Port $port ($serviceName) is AVAILABLE"
        $diagnosticReport += "[SUCCESS] Port $port ($serviceName) available"
        $successCount++
    }
}

# ============================================================================
# PHASE 3: POM.xml Validation
# ============================================================================
Write-Section "PHASE 3: POM.xml Dependency Validation"

$pomFiles = @(
    "pom.xml",
    "config-server/pom.xml",
    "eureka-server/pom.xml",
    "api-gateway/pom.xml",
    "user-service/pom.xml",
    "restaurant-service/pom.xml",
    "order-service/pom.xml",
    "payment-service/pom.xml",
    "notification-service/pom.xml",
    "e2e-tests/pom.xml"
)

foreach ($pomFile in $pomFiles) {
    if (Test-Path $pomFile) {
        try {
            [xml]$pomContent = Get-Content $pomFile
            $artifactId = $pomContent.project.artifactId
            Write-Success "Valid POM: $pomFile ($artifactId)"
            $diagnosticReport += "[SUCCESS] POM validated: $pomFile"
            $successCount++
        } catch {
            Write-Error-Custom "Invalid XML in $pomFile"
            $diagnosticReport += "[ERROR] Invalid POM: $pomFile"
            $errorCount++
        }
    } else {
        Write-Error-Custom "Missing POM file: $pomFile"
        $diagnosticReport += "[ERROR] Missing POM: $pomFile"
        $errorCount++
    }
}

# Check for version conflicts
Write-Info "Checking Spring Boot and Cloud versions..."
try {
    [xml]$parentPom = Get-Content "pom.xml"
    $springBootVersion = $parentPom.project.properties.'spring-boot.version'
    $springCloudVersion = $parentPom.project.properties.'spring-cloud.version'
    
    Write-Info "Spring Boot Version: $springBootVersion"
    Write-Info "Spring Cloud Version: $springCloudVersion"
    
    # Expected: Spring Boot 3.2.0 and Spring Cloud 2023.0.0
    if ($springBootVersion -eq "3.2.0" -and $springCloudVersion -eq "2023.0.0") {
        Write-Success "Spring Boot and Cloud versions are compatible"
        $diagnosticReport += "[SUCCESS] Compatible Spring versions detected"
        $successCount++
    } else {
        Write-Warning-Custom "Spring versions may have compatibility issues"
        $diagnosticReport += "[WARNING] Spring Boot: $springBootVersion, Spring Cloud: $springCloudVersion"
        $warningCount++
    }
} catch {
    Write-Error-Custom "Failed to parse parent pom.xml"
    $diagnosticReport += "[ERROR] Cannot validate Spring versions"
    $errorCount++
}

# ============================================================================
# PHASE 4: Configuration Files Validation
# ============================================================================
Write-Section "PHASE 4: Configuration Files Validation"

$configFiles = @(
    "config-repo/application.yml",
    "config-repo/user-service.yml",
    "config-repo/restaurant-service.yml",
    "config-repo/order-service.yml",
    "config-repo/payment-service.yml",
    "config-repo/notification-service.yml",
    "config-repo/api-gateway.yml",
    "config-repo/eureka-server.yml",
    "config-server/src/main/resources/application.yml",
    "eureka-server/src/main/resources/application.yml",
    "api-gateway/src/main/resources/application.yml",
    "user-service/src/main/resources/application.yml",
    "restaurant-service/src/main/resources/application.yml",
    "order-service/src/main/resources/application.yml",
    "payment-service/src/main/resources/application.yml",
    "notification-service/src/main/resources/application.yml",
    "e2e-tests/src/test/resources/application-e2e-test.yml"
)

foreach ($configFile in $configFiles) {
    if (Test-Path $configFile) {
        Write-Success "Configuration file exists: $configFile"
        $diagnosticReport += "[SUCCESS] Config file found: $configFile"
        $successCount++
    } else {
        Write-Error-Custom "Missing configuration file: $configFile"
        $diagnosticReport += "[ERROR] Missing config: $configFile"
        $errorCount++
    }
}

# ============================================================================
# PHASE 5: Database Configuration Check
# ============================================================================
Write-Section "PHASE 5: Database Configuration Check"

Write-Info "Checking database configurations in config-repo..."

$dbConfigs = @(
    @{Service="User Service"; File="config-repo/user-service.yml"; Port=5432; DB="userdb"},
    @{Service="Restaurant Service"; File="config-repo/restaurant-service.yml"; Port=5433; DB="restaurantdb"},
    @{Service="Order Service"; File="config-repo/order-service.yml"; Port=5434; DB="orderdb"},
    @{Service="Payment Service"; File="config-repo/payment-service.yml"; Port=5435; DB="paymentdb"},
    @{Service="Notification Service"; File="config-repo/notification-service.yml"; Port=5436; DB="notificationdb"}
)

foreach ($dbConfig in $dbConfigs) {
    if (Test-Path $dbConfig.File) {
        $content = Get-Content $dbConfig.File -Raw
        if ($content -match "jdbc:postgresql://localhost:$($dbConfig.Port)/$($dbConfig.DB)") {
            Write-Success "$($dbConfig.Service): Database config correct (Port: $($dbConfig.Port), DB: $($dbConfig.DB))"
            $diagnosticReport += "[SUCCESS] DB config valid: $($dbConfig.Service)"
            $successCount++
        } else {
            Write-Warning-Custom "$($dbConfig.Service): Database config may be incorrect"
            $diagnosticReport += "[WARNING] DB config issue: $($dbConfig.Service)"
            $warningCount++
        }
    }
}

# ============================================================================
# PHASE 6: WireMock Directory Structure
# ============================================================================
Write-Section "PHASE 6: WireMock Directory Structure"

$wiremockDirs = @(
    "e2e-tests/wiremock",
    "e2e-tests/wiremock/mappings",
    "e2e-tests/wiremock/__files"
)

foreach ($dir in $wiremockDirs) {
    if (Test-Path $dir) {
        Write-Success "WireMock directory exists: $dir"
        $diagnosticReport += "[SUCCESS] WireMock dir found: $dir"
        $successCount++
    } else {
        Write-Error-Custom "Missing WireMock directory: $dir"
        $diagnosticReport += "[ERROR] Missing WireMock dir: $dir"
        $errorCount++
    }
}

# ============================================================================
# PHASE 7: Compilation Check (Quick Validation)
# ============================================================================
Write-Section "PHASE 7: Compilation Validation"

Write-Info "Running quick compilation check (mvn validate)..."
try {
    $validateOutput = mvn validate -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Maven validation passed - no obvious compilation errors"
        $diagnosticReport += "[SUCCESS] Maven validation passed"
        $successCount++
    } else {
        Write-Error-Custom "Maven validation failed - compilation errors detected"
        $diagnosticReport += "[ERROR] Maven validation failed"
        $errorCount++
        if ($Verbose) {
            Write-Host $validateOutput
        }
    }
} catch {
    Write-Error-Custom "Failed to run Maven validation"
    $diagnosticReport += "[ERROR] Maven validation command failed"
    $errorCount++
}

# ============================================================================
# PHASE 8: Service Health Endpoint Check (if services are running)
# ============================================================================
Write-Section "PHASE 8: Service Health Endpoint Check"

$healthEndpoints = @{
    "Config Server" = "http://localhost:8888/actuator/health"
    "Eureka Server" = "http://localhost:8761/actuator/health"
    "API Gateway" = "http://localhost:8080/actuator/health"
    "User Service" = "http://localhost:8085/actuator/health"
    "Restaurant Service" = "http://localhost:8082/actuator/health"
    "Order Service" = "http://localhost:8083/actuator/health"
    "Payment Service" = "http://localhost:8084/actuator/health"
    "Notification Service" = "http://localhost:8081/actuator/health"
}

Write-Info "Checking if services are running..."

foreach ($service in $healthEndpoints.Keys) {
    $endpoint = $healthEndpoints[$service]
    try {
        $response = Invoke-WebRequest -Uri $endpoint -TimeoutSec 2 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Success "$service is UP and healthy"
            $diagnosticReport += "[SUCCESS] $service is running"
            $successCount++
        }
    } catch {
        Write-Info "$service is not running (expected if not started yet)"
        $diagnosticReport += "[INFO] $service not running"
    }
}

# ============================================================================
# DIAGNOSTIC REPORT SUMMARY
# ============================================================================
Write-Section "DIAGNOSTIC REPORT SUMMARY"

Write-Host ""
Write-Host "Total Checks Performed: $($successCount + $warningCount + $errorCount)" -ForegroundColor White
Write-Success "Successful: $successCount"
Write-Warning-Custom "Warnings: $warningCount"
Write-Error-Custom "Errors: $errorCount"
Write-Host ""

if ($errorCount -eq 0 -and $warningCount -eq 0) {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║  ✓ ALL CHECKS PASSED - SYSTEM READY FOR STARTUP                  ║" -ForegroundColor Green
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
} elseif ($errorCount -eq 0) {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
    Write-Host "║  ⚠ WARNINGS DETECTED - REVIEW BEFORE STARTUP                      ║" -ForegroundColor Yellow
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow
} else {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Red
    Write-Host "║  ✗ CRITICAL ERRORS DETECTED - RUN fix-all.ps1 BEFORE STARTUP      ║" -ForegroundColor Red
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Red
}

Write-Host ""

# Export report if requested
if ($ExportReport) {
    $reportFile = "diagnostic-report-$(Get-Date -Format 'yyyyMMdd-HHmmss').txt"
    $diagnosticReport | Out-File $reportFile
    Write-Success "Diagnostic report exported to: $reportFile"
}

Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. If errors detected: Run .\fix-all.ps1" -ForegroundColor White
Write-Host "  2. If all clear: Run .\start-all-services.ps1" -ForegroundColor White
Write-Host "  3. For detailed logs: Run with -Verbose flag" -ForegroundColor White
Write-Host ""
