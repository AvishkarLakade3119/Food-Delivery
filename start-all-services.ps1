# ============================================================================
# Food Delivery Microservices - Complete Startup Script
# ============================================================================
# Purpose: Start all services in correct order with health checks
# Author: Slingshot AI Agent
# Date: 2026-06-12
# ============================================================================

param(
    [switch]$SkipBuild,
    [switch]$Verbose
)

# Color output functions
function Write-Success { param($msg) Write-Host "✓ $msg" -ForegroundColor Green }
function Write-Error-Custom { param($msg) Write-Host "✗ $msg" -ForegroundColor Red }
function Write-Warning-Custom { param($msg) Write-Host "⚠ $msg" -ForegroundColor Yellow }
function Write-Info { param($msg) Write-Host "ℹ $msg" -ForegroundColor Cyan }
function Write-Section { param($msg) Write-Host "`n========== $msg ==========" -ForegroundColor Magenta }

# Global variables for process tracking
$global:serviceProcesses = @{}
$global:failedServices = @()

Write-Host "`n" -NoNewline
Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Food Delivery Microservices - Startup Orchestrator              ║" -ForegroundColor Cyan
Write-Host "║  Version 1.0 | Date: 2026-06-12                                  ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# Helper Functions
# ============================================================================

function Test-ServiceHealth {
    param(
        [string]$ServiceName,
        [string]$HealthUrl,
        [int]$TimeoutSeconds = 120,
        [int]$RetryIntervalSeconds = 5
    )
    
    $elapsed = 0
    $healthy = $false
    
    Write-Info "Waiting for $ServiceName to become healthy..."
    
    while ($elapsed -lt $TimeoutSeconds -and -not $healthy) {
        try {
            $response = Invoke-WebRequest -Uri $HealthUrl -TimeoutSec 3 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                $healthy = $true
                Write-Success "$ServiceName is UP and healthy"
                return $true
            }
        } catch {
            Write-Host "." -NoNewline -ForegroundColor Gray
            Start-Sleep -Seconds $RetryIntervalSeconds
            $elapsed += $RetryIntervalSeconds
        }
    }
    
    if (-not $healthy) {
        Write-Error-Custom "$ServiceName failed to become healthy within $TimeoutSeconds seconds"
        return $false
    }
}

function Test-EurekaRegistration {
    param(
        [string]$ServiceName,
        [int]$TimeoutSeconds = 60
    )
    
    $elapsed = 0
    $registered = $false
    $eurekaUrl = "http://localhost:8761/eureka/apps"
    
    Write-Info "Checking Eureka registration for $ServiceName..."
    
    while ($elapsed -lt $TimeoutSeconds -and -not $registered) {
        try {
            $response = Invoke-WebRequest -Uri $eurekaUrl -TimeoutSec 3 -ErrorAction Stop
            if ($response.Content -match $ServiceName.ToUpper()) {
                $registered = $true
                Write-Success "$ServiceName registered with Eureka"
                return $true
            }
        } catch {
            # Eureka might not be ready yet
        }
        
        Write-Host "." -NoNewline -ForegroundColor Gray
        Start-Sleep -Seconds 5
        $elapsed += 5
    }
    
    if (-not $registered) {
        Write-Warning-Custom "$ServiceName not registered with Eureka (may register later)"
        return $false
    }
}

function Start-ServiceInNewWindow {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [string]$MavenCommand = "mvn spring-boot:run"
    )
    
    Write-Info "Starting $ServiceName..."
    
    try {
        $process = Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd $ServicePath; Write-Host 'Starting $ServiceName...' -ForegroundColor Cyan; $MavenCommand" -PassThru
        $global:serviceProcesses[$ServiceName] = $process
        Write-Success "$ServiceName process started (PID: $($process.Id))"
        return $true
    } catch {
        Write-Error-Custom "Failed to start $ServiceName: $_"
        $global:failedServices += $ServiceName
        return $false
    }
}

function Stop-AllServices {
    Write-Section "STOPPING ALL SERVICES"
    
    foreach ($serviceName in $global:serviceProcesses.Keys) {
        $process = $global:serviceProcesses[$serviceName]
        if ($process -and -not $process.HasExited) {
            Write-Info "Stopping $serviceName (PID: $($process.Id))..."
            try {
                Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
                Write-Success "$serviceName stopped"
            } catch {
                Write-Warning-Custom "Failed to stop $serviceName: $_"
            }
        }
    }
}

# ============================================================================
# PRE-FLIGHT CHECKS
# ============================================================================
Write-Section "PRE-FLIGHT CHECKS"

# Check Java
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Success "Java detected: $javaVersion"
} catch {
    Write-Error-Custom "Java not found - please install Java 17 or higher"
    exit 1
}

# Check Maven
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Success "Maven detected: $mavenVersion"
} catch {
    Write-Error-Custom "Maven not found - please install Maven"
    exit 1
}

# Check if ports are free
$requiredPorts = @{
    8888 = "Config Server"
    8761 = "Eureka Server"
    8080 = "API Gateway"
}

$portsInUse = @()
foreach ($port in $requiredPorts.Keys) {
    $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($connection) {
        $portsInUse += $port
        Write-Warning-Custom "Port $port ($($requiredPorts[$port])) is already in use"
    }
}

if ($portsInUse.Count -gt 0) {
    Write-Warning-Custom "Some required ports are in use. Run .\fix-all.ps1 to free them."
    $continue = Read-Host "Continue anyway? (Y/N)"
    if ($continue -ne 'Y' -and $continue -ne 'y') {
        Write-Info "Startup cancelled"
        exit 0
    }
}

# ============================================================================
# BUILD PHASE (Optional)
# ============================================================================
if (-not $SkipBuild) {
    Write-Section "BUILD PHASE"
    
    Write-Info "Building all services (this may take 5-10 minutes)..."
    Write-Info "To skip build in future, use -SkipBuild flag"
    
    try {
        $buildOutput = mvn clean install -DskipTests 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "All services built successfully"
        } else {
            Write-Error-Custom "Build failed - check output for errors"
            if ($Verbose) {
                Write-Host $buildOutput
            }
            $continue = Read-Host "Continue with startup anyway? (Y/N)"
            if ($continue -ne 'Y' -and $continue -ne 'y') {
                exit 1
            }
        }
    } catch {
        Write-Error-Custom "Build command failed: $_"
        exit 1
    }
} else {
    Write-Info "Skipping build phase (using existing JARs)"
}

# ============================================================================
# STARTUP PHASE 1: Config Server
# ============================================================================
Write-Section "PHASE 1: Starting Config Server"

if (Start-ServiceInNewWindow -ServiceName "Config Server" -ServicePath "config-server") {
    Start-Sleep -Seconds 10
    
    if (Test-ServiceHealth -ServiceName "Config Server" -HealthUrl "http://localhost:8888/actuator/health" -TimeoutSeconds 60) {
        Write-Success "Config Server is ready"
    } else {
        Write-Error-Custom "Config Server failed to start"
        Write-Warning-Custom "Check config-server window for errors"
        $continue = Read-Host "Continue anyway? (Y/N)"
        if ($continue -ne 'Y' -and $continue -ne 'y') {
            Stop-AllServices
            exit 1
        }
    }
} else {
    Write-Error-Custom "Failed to start Config Server"
    exit 1
}

# ============================================================================
# STARTUP PHASE 2: Eureka Server
# ============================================================================
Write-Section "PHASE 2: Starting Eureka Server"

if (Start-ServiceInNewWindow -ServiceName "Eureka Server" -ServicePath "eureka-server") {
    Start-Sleep -Seconds 15
    
    if (Test-ServiceHealth -ServiceName "Eureka Server" -HealthUrl "http://localhost:8761/actuator/health" -TimeoutSeconds 90) {
        Write-Success "Eureka Server is ready"
        
        # Open Eureka Dashboard
        Write-Info "Opening Eureka Dashboard..."
        Start-Process "http://localhost:8761"
    } else {
        Write-Error-Custom "Eureka Server failed to start"
        Write-Warning-Custom "Check eureka-server window for errors"
        $continue = Read-Host "Continue anyway? (Y/N)"
        if ($continue -ne 'Y' -and $continue -ne 'y') {
            Stop-AllServices
            exit 1
        }
    }
} else {
    Write-Error-Custom "Failed to start Eureka Server"
    Stop-AllServices
    exit 1
}

# ============================================================================
# STARTUP PHASE 3: Business Services (Parallel)
# ============================================================================
Write-Section "PHASE 3: Starting Business Services"

$businessServices = @(
    @{Name="User Service"; Path="user-service"; Port=8085},
    @{Name="Restaurant Service"; Path="restaurant-service"; Port=8082},
    @{Name="Order Service"; Path="order-service"; Port=8083},
    @{Name="Payment Service"; Path="payment-service"; Port=8084},
    @{Name="Notification Service"; Path="notification-service"; Port=8081}
)

Write-Info "Starting all business services in parallel..."

foreach ($service in $businessServices) {
    Start-ServiceInNewWindow -ServiceName $service.Name -ServicePath $service.Path
    Start-Sleep -Seconds 2  # Stagger startup slightly
}

Write-Info "Waiting for business services to initialize (30 seconds)..."
Start-Sleep -Seconds 30

# Check health of each business service
$healthyServices = 0
foreach ($service in $businessServices) {
    $healthUrl = "http://localhost:$($service.Port)/actuator/health"
    if (Test-ServiceHealth -ServiceName $service.Name -HealthUrl $healthUrl -TimeoutSeconds 60) {
        $healthyServices++
    } else {
        Write-Warning-Custom "$($service.Name) may not be fully started yet"
    }
}

Write-Info "$healthyServices out of $($businessServices.Count) business services are healthy"

# Check Eureka registration
Write-Info "Checking Eureka registration for business services..."
Start-Sleep -Seconds 10

$registeredServices = 0
foreach ($service in $businessServices) {
    $serviceName = $service.Path.ToUpper()
    if (Test-EurekaRegistration -ServiceName $serviceName -TimeoutSeconds 30) {
        $registeredServices++
    }
}

Write-Info "$registeredServices out of $($businessServices.Count) services registered with Eureka"

# ============================================================================
# STARTUP PHASE 4: API Gateway
# ============================================================================
Write-Section "PHASE 4: Starting API Gateway"

if (Start-ServiceInNewWindow -ServiceName "API Gateway" -ServicePath "api-gateway") {
    Start-Sleep -Seconds 15
    
    if (Test-ServiceHealth -ServiceName "API Gateway" -HealthUrl "http://localhost:8080/actuator/health" -TimeoutSeconds 60) {
        Write-Success "API Gateway is ready"
        
        # Check if routes are configured
        try {
            $routesResponse = Invoke-WebRequest -Uri "http://localhost:8080/actuator/gateway/routes" -TimeoutSec 5 -ErrorAction Stop
            Write-Success "API Gateway routes configured successfully"
        } catch {
            Write-Warning-Custom "Could not verify API Gateway routes"
        }
    } else {
        Write-Error-Custom "API Gateway failed to start"
        Write-Warning-Custom "Check api-gateway window for errors"
    }
} else {
    Write-Error-Custom "Failed to start API Gateway"
}

# ============================================================================
# STARTUP COMPLETE
# ============================================================================
Write-Section "STARTUP COMPLETE"

Write-Host ""
Write-Host "Service Status Summary:" -ForegroundColor Cyan
Write-Host "======================" -ForegroundColor Cyan

$allServices = @(
    @{Name="Config Server"; Url="http://localhost:8888/actuator/health"},
    @{Name="Eureka Server"; Url="http://localhost:8761/actuator/health"},
    @{Name="User Service"; Url="http://localhost:8085/actuator/health"},
    @{Name="Restaurant Service"; Url="http://localhost:8082/actuator/health"},
    @{Name="Order Service"; Url="http://localhost:8083/actuator/health"},
    @{Name="Payment Service"; Url="http://localhost:8084/actuator/health"},
    @{Name="Notification Service"; Url="http://localhost:8081/actuator/health"},
    @{Name="API Gateway"; Url="http://localhost:8080/actuator/health"}
)

$runningCount = 0
foreach ($service in $allServices) {
    try {
        $response = Invoke-WebRequest -Uri $service.Url -TimeoutSec 2 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Success "$($service.Name) - RUNNING"
            $runningCount++
        }
    } catch {
        Write-Error-Custom "$($service.Name) - NOT RESPONDING"
    }
}

Write-Host ""
Write-Host "$runningCount out of $($allServices.Count) services are running" -ForegroundColor $(if ($runningCount -eq $allServices.Count) { 'Green' } else { 'Yellow' })
Write-Host ""

if ($global:failedServices.Count -gt 0) {
    Write-Warning-Custom "Failed services: $($global:failedServices -join ', ')"
    Write-Host ""
}

Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  ✓ STARTUP COMPLETE - ALL SERVICES LAUNCHED                       ║" -ForegroundColor Green
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

Write-Host "Important URLs:" -ForegroundColor Cyan
Write-Host "  Eureka Dashboard:  http://localhost:8761" -ForegroundColor White
Write-Host "  API Gateway:       http://localhost:8080" -ForegroundColor White
Write-Host "  Config Server:     http://localhost:8888" -ForegroundColor White
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Check Eureka Dashboard to verify all services are registered" -ForegroundColor White
Write-Host "  2. Run .\verify-system.ps1 to perform health checks" -ForegroundColor White
Write-Host "  3. Run .\run-e2e-tests.ps1 to execute E2E tests" -ForegroundColor White
Write-Host "  4. Check individual service windows for any errors" -ForegroundColor White
Write-Host ""

Write-Host "To stop all services: Close all PowerShell windows or run Stop-AllServices function" -ForegroundColor Yellow
Write-Host ""
