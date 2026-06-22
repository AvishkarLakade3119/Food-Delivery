# ============================================================
# Phase 4 Demo: Resilience4j Circuit Breaker Pattern
# ============================================================

function Show-Section {
    param([string]$Title)
    Write-Host ""
    Write-Host ("=" * 70) -ForegroundColor Cyan
    Write-Host "  $Title" -ForegroundColor Cyan
    Write-Host ("=" * 70) -ForegroundColor Cyan
}

function Test-GatewayUp {
    try {
        $h = Invoke-RestMethod http://localhost:8090/actuator/health -TimeoutSec 5
        return $h.status -eq "UP"
    } catch {
        return $false
    }
}

function Show-CBState {
    param([string]$CbName)
    try {
        $cb = (Invoke-RestMethod http://localhost:8090/actuator/circuitbreakers -TimeoutSec 5).circuitBreakers.$CbName
        Write-Host "  $CbName" -ForegroundColor Yellow
        Write-Host "    State          : $($cb.state)" -ForegroundColor White
        Write-Host "    BufferedCalls  : $($cb.bufferedCalls)" -ForegroundColor White
        Write-Host "    FailedCalls    : $($cb.failedCalls)" -ForegroundColor White
        Write-Host "    FailureRate    : $($cb.failureRate)" -ForegroundColor White
        Write-Host "    NotPermitted   : $($cb.notPermittedCalls)" -ForegroundColor White
    } catch {
        Write-Host "  Could not fetch state for $CbName" -ForegroundColor Red
    }
}

# ============================================================
# Pre-flight Check
# ============================================================
Show-Section "Pre-flight Check: Verifying Services"

if (-not (Test-GatewayUp)) {
    Write-Host "API Gateway is DOWN on port 8090" -ForegroundColor Red
    Write-Host "Please start gateway first:" -ForegroundColor Yellow
    Write-Host "  cd 'C:\Users\avilakad\OneDrive - Publicis Groupe\Documents\Food-Delivery-main\api-gateway'" -ForegroundColor Yellow
    Write-Host "  java -jar target\api-gateway-1.0.0.jar" -ForegroundColor Yellow
    exit
}

Write-Host "API Gateway is UP" -ForegroundColor Green

# Check restaurant
$rCheck = netstat -ano | findstr ":8082" | findstr "LISTENING"
if (-not $rCheck) {
    Write-Host "Restaurant-service is DOWN" -ForegroundColor Red
    Write-Host "Please start restaurant-service first" -ForegroundColor Yellow
    exit
}
Write-Host "Restaurant-service is UP" -ForegroundColor Green


# ============================================================
# STEP 1: Initial State
# ============================================================
Show-Section "STEP 1: Initial System State - All CBs CLOSED"

Write-Host "`nGateway Health:" -ForegroundColor Green
Invoke-RestMethod http://localhost:8090/actuator/health | ConvertTo-Json -Depth 3

Write-Host "`nAll 5 Circuit Breakers:" -ForegroundColor Green
Show-CBState "restaurantServiceCB"
Show-CBState "orderServiceCB"
Show-CBState "paymentServiceCB"
Show-CBState "userServiceCB"
Show-CBState "notificationServiceCB"

Read-Host "`nPress Enter to simulate restaurant outage..."


# ============================================================
# STEP 2: Kill Restaurant Service
# ============================================================
Show-Section "STEP 2: Killing Restaurant Service"

$line = netstat -ano | findstr ":8082" | findstr "LISTENING" | Select-Object -First 1
$tokens = $line -split '\s+' | Where-Object { $_ -ne '' }
$rPid = $tokens[-1]
Write-Host "`nKilling PID: $rPid" -ForegroundColor Red
taskkill /PID $rPid /F
Start-Sleep -Seconds 5

Write-Host "Restaurant-service DOWN" -ForegroundColor Green
Read-Host "`nPress Enter to hit gateway..."


# ============================================================
# STEP 3: Trigger CB
# ============================================================
Show-Section "STEP 3: Hitting Gateway 15 Times - Watch CB Open"

for ($i=1; $i -le 15; $i++) {
    try {
        $r = Invoke-RestMethod http://localhost:8090/api/restaurants/1 -TimeoutSec 10
        Write-Host "  Call $($i.ToString().PadLeft(2)) : SUCCESS" -ForegroundColor Green
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        if (-not $code) { $code = "ERR" }
        Write-Host "  Call $($i.ToString().PadLeft(2)) : HTTP $code" -ForegroundColor Yellow
    }
    Start-Sleep -Milliseconds 400
}


# ============================================================
# STEP 4: Show CB OPEN
# ============================================================
Show-Section "STEP 4: CB State After Failures"

Show-CBState "restaurantServiceCB"

Write-Host "`n  Phase 4 Demo Complete!" -ForegroundColor Green
Write-Host "  CB transitioned from CLOSED to OPEN" -ForegroundColor Green
Write-Host "  System protected from cascade failure" -ForegroundColor Green