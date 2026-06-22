# ============================================================================
# Food Delivery Microservices - E2E Test Execution Script
# ============================================================================
# Purpose: Execute E2E tests with service verification and reporting
# Author: Slingshot AI Agent
# Date: 2026-06-12
# ============================================================================

param(
    [switch]$SkipServiceCheck,
    [switch]$IntegrationOnly,
    [switch]$WorkflowOnly,
    [switch]$PerformanceOnly,
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
Write-Host "║  Food Delivery Microservices - E2E Test Runner                   ║" -ForegroundColor Cyan
Write-Host "║  Version 1.0 | Date: 2026-06-12                                  ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# PHASE 1: Pre-Test Service Verification
# ============================================================================
Write-Section "PHASE 1: Service Verification"

if (-not $SkipServiceCheck) {
    Write-Info "Verifying all services are running..."
    
    $requiredServices = @(
        @{Name="Config Server"; Url="http://localhost:8888/actuator/health"},
        @{Name="Eureka Server"; Url="http://localhost:8761/actuator/health"},
        @{Name="User Service"; Url="http://localhost:8085/actuator/health"},
        @{Name="Restaurant Service"; Url="http://localhost:8082/actuator/health"},
        @{Name="Order Service"; Url="http://localhost:8083/actuator/health"},
        @{Name="Payment Service"; Url="http://localhost:8084/actuator/health"},
        @{Name="Notification Service"; Url="http://localhost:8081/actuator/health"},
        @{Name="API Gateway"; Url="http://localhost:8080/actuator/health"}
    )
    
    $allHealthy = $true
    $healthyCount = 0
    
    foreach ($service in $requiredServices) {
        try {
            $response = Invoke-WebRequest -Uri $service.Url -TimeoutSec 3 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Success "$($service.Name) is UP"
                $healthyCount++
            }
        } catch {
            Write-Error-Custom "$($service.Name) is NOT responding"
            $allHealthy = $false
        }
    }
    
    Write-Host ""
    Write-Info "$healthyCount out of $($requiredServices.Count) services are healthy"
    
    if (-not $allHealthy) {
        Write-Error-Custom "Not all services are running!"
        Write-Warning-Custom "Please start all services using .\start-all-services.ps1"
        $continue = Read-Host "Continue with tests anyway? (Y/N)"
        if ($continue -ne 'Y' -and $continue -ne 'y') {
            Write-Info "Test execution cancelled"
            exit 1
        }
    } else {
        Write-Success "All required services are healthy and ready for testing"
    }
} else {
    Write-Warning-Custom "Skipping service verification (use with caution)"
}

# ============================================================================
# PHASE 2: Eureka Registration Check
# ============================================================================
Write-Section "PHASE 2: Eureka Registration Check"

Write-Info "Checking service registration with Eureka..."

try {
    $eurekaResponse = Invoke-WebRequest -Uri "http://localhost:8761/eureka/apps" -TimeoutSec 5 -ErrorAction Stop
    $eurekaContent = $eurekaResponse.Content
    
    $expectedServices = @("USER-SERVICE", "RESTAURANT-SERVICE", "ORDER-SERVICE", "PAYMENT-SERVICE", "NOTIFICATION-SERVICE", "API-GATEWAY")
    $registeredCount = 0
    
    foreach ($serviceName in $expectedServices) {
        if ($eurekaContent -match $serviceName) {
            Write-Success "$serviceName is registered with Eureka"
            $registeredCount++
        } else {
            Write-Warning-Custom "$serviceName is NOT registered with Eureka"
        }
    }
    
    Write-Host ""
    Write-Info "$registeredCount out of $($expectedServices.Count) services registered with Eureka"
    
    if ($registeredCount -lt $expectedServices.Count) {
        Write-Warning-Custom "Some services are not registered - tests may fail"
    }
} catch {
    Write-Error-Custom "Failed to connect to Eureka Server"
    Write-Warning-Custom "Service discovery may not work during tests"
}

# ============================================================================
# PHASE 3: WireMock Setup Check
# ============================================================================
Write-Section "PHASE 3: WireMock Setup Verification"

Write-Info "Checking WireMock directory structure..."

$wiremockDirs = @(
    "e2e-tests/wiremock",
    "e2e-tests/wiremock/mappings",
    "e2e-tests/wiremock/__files"
)

$wiremockReady = $true
foreach ($dir in $wiremockDirs) {
    if (Test-Path $dir) {
        Write-Success "WireMock directory exists: $dir"
    } else {
        Write-Error-Custom "Missing WireMock directory: $dir"
        $wiremockReady = $false
    }
}

if (-not $wiremockReady) {
    Write-Warning-Custom "WireMock setup incomplete - creating missing directories..."
    foreach ($dir in $wiremockDirs) {
        if (-not (Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
            Write-Success "Created: $dir"
        }
    }
}

# ============================================================================
# PHASE 4: Test Execution
# ============================================================================
Write-Section "PHASE 4: Executing E2E Tests"

$testResults = @()
$totalTests = 0
$passedTests = 0
$failedTests = 0
$skippedTests = 0

# Determine which tests to run
$testsToRun = @()

if ($IntegrationOnly) {
    $testsToRun += "ServiceIntegrationTest"
} elseif ($WorkflowOnly) {
    $testsToRun += "OrderPlacementWorkflowE2ETest"
    $testsToRun += "AdvancedOrderWorkflowE2ETest"
} elseif ($PerformanceOnly) {
    $testsToRun += "PerformanceAndLoadE2ETest"
} else {
    # Run all tests in order
    $testsToRun += "ServiceIntegrationTest"
    $testsToRun += "OrderPlacementWorkflowE2ETest"
    $testsToRun += "AdvancedOrderWorkflowE2ETest"
    $testsToRun += "PerformanceAndLoadE2ETest"
}

Write-Info "Tests to execute: $($testsToRun -join ', ')"
Write-Host ""

# Navigate to e2e-tests directory
Set-Location e2e-tests

foreach ($testClass in $testsToRun) {
    Write-Info "Running test: $testClass"
    Write-Host "" 
    
    try {
        # Run Maven test for specific test class
        $testOutput = mvn test -Dtest=$testClass -Dspring.profiles.active=e2e-test 2>&1
        
        # Parse test results
        if ($testOutput -match "Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)") {
            $testsRun = [int]$matches[1]
            $failures = [int]$matches[2]
            $errors = [int]$matches[3]
            $skipped = [int]$matches[4]
            
            $totalTests += $testsRun
            $failedTests += ($failures + $errors)
            $skippedTests += $skipped
            $passedTests += ($testsRun - $failures - $errors - $skipped)
            
            if ($failures -eq 0 -and $errors -eq 0) {
                Write-Success "$testClass: PASSED ($testsRun tests)"
                $testResults += "[PASS] $testClass - $testsRun tests passed"
            } else {
                Write-Error-Custom "$testClass: FAILED ($failures failures, $errors errors)"
                $testResults += "[FAIL] $testClass - $failures failures, $errors errors"
                
                # Extract failure details
                if ($testOutput -match "(?s)Failed tests:(.*?)Tests run:") {
                    Write-Host ""
                    Write-Warning-Custom "Failure Details:"
                    Write-Host $matches[1] -ForegroundColor Red
                }
            }
        } else {
            Write-Warning-Custom "$testClass: Could not parse test results"
            $testResults += "[UNKNOWN] $testClass - Unable to parse results"
        }
    } catch {
        Write-Error-Custom "$testClass: Test execution failed - $_"
        $testResults += "[ERROR] $testClass - Execution failed"
        $failedTests++
    }
    
    Write-Host ""
}

# Return to root directory
Set-Location ..

# ============================================================================
# PHASE 5: Test Report Generation
# ============================================================================
Write-Section "PHASE 5: Test Results Summary"

Write-Host ""
Write-Host "Test Execution Summary:" -ForegroundColor Cyan
Write-Host "======================" -ForegroundColor Cyan
Write-Host "Total Tests:   $totalTests" -ForegroundColor White
Write-Success "Passed:        $passedTests"
Write-Error-Custom "Failed:        $failedTests"
Write-Warning-Custom "Skipped:       $skippedTests"
Write-Host ""

if ($totalTests -gt 0) {
    $successRate = [math]::Round(($passedTests / $totalTests) * 100, 2)
    Write-Host "Success Rate:  $successRate%" -ForegroundColor $(if ($successRate -ge 80) { 'Green' } elseif ($successRate -ge 50) { 'Yellow' } else { 'Red' })
}

Write-Host ""
Write-Host "Detailed Results:" -ForegroundColor Cyan
foreach ($result in $testResults) {
    if ($result -match "\[PASS\]") {
        Write-Host $result -ForegroundColor Green
    } elseif ($result -match "\[FAIL\]") {
        Write-Host $result -ForegroundColor Red
    } else {
        Write-Host $result -ForegroundColor Yellow
    }
}

Write-Host ""

# Generate HTML report if requested
if ($GenerateReport) {
    Write-Info "Generating HTML test report..."
    
    $reportFile = "e2e-test-report-$(Get-Date -Format 'yyyyMMdd-HHmmss').html"
    
    $htmlReport = @"
<!DOCTYPE html>
<html>
<head>
    <title>E2E Test Report - Food Delivery Microservices</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        h1 { color: #333; }
        .summary { background-color: white; padding: 20px; border-radius: 5px; margin-bottom: 20px; }
        .pass { color: green; font-weight: bold; }
        .fail { color: red; font-weight: bold; }
        .skip { color: orange; font-weight: bold; }
        table { width: 100%; border-collapse: collapse; background-color: white; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; }
        .timestamp { color: #666; font-size: 0.9em; }
    </style>
</head>
<body>
    <h1>E2E Test Report - Food Delivery Microservices</h1>
    <p class="timestamp">Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')</p>
    
    <div class="summary">
        <h2>Test Summary</h2>
        <p>Total Tests: <strong>$totalTests</strong></p>
        <p class="pass">Passed: $passedTests</p>
        <p class="fail">Failed: $failedTests</p>
        <p class="skip">Skipped: $skippedTests</p>
        <p>Success Rate: <strong>$successRate%</strong></p>
    </div>
    
    <h2>Test Results</h2>
    <table>
        <tr>
            <th>Test Class</th>
            <th>Status</th>
            <th>Details</th>
        </tr>
"@
    
    foreach ($result in $testResults) {
        if ($result -match "\[(\w+)\]\s+(.+?)\s+-\s+(.+)") {
            $status = $matches[1]
            $testName = $matches[2]
            $details = $matches[3]
            
            $statusClass = switch ($status) {
                "PASS" { "pass" }
                "FAIL" { "fail" }
                default { "skip" }
            }
            
            $htmlReport += @"
        <tr>
            <td>$testName</td>
            <td class="$statusClass">$status</td>
            <td>$details</td>
        </tr>
"@
        }
    }
    
    $htmlReport += @"
    </table>
</body>
</html>
"@
    
    $htmlReport | Out-File $reportFile -Encoding UTF8
    Write-Success "HTML report generated: $reportFile"
    
    # Open report in browser
    Start-Process $reportFile
}

# ============================================================================
# TEST EXECUTION COMPLETE
# ============================================================================

if ($failedTests -eq 0) {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║  ✓ ALL E2E TESTS PASSED - SYSTEM VERIFIED                        ║" -ForegroundColor Green
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
} else {
    Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Red
    Write-Host "║  ✗ SOME E2E TESTS FAILED - REVIEW RESULTS ABOVE                  ║" -ForegroundColor Red
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Red
}

Write-Host ""
Write-Host "Test Logs Location:" -ForegroundColor Cyan
Write-Host "  e2e-tests/target/surefire-reports/" -ForegroundColor White
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Review detailed test logs in e2e-tests/target/surefire-reports/" -ForegroundColor White
Write-Host "  2. Check service logs for any errors during test execution" -ForegroundColor White
Write-Host "  3. Run specific test categories using flags: -IntegrationOnly, -WorkflowOnly, -PerformanceOnly" -ForegroundColor White
Write-Host ""
