# Quick CB reset script
Write-Host "Resetting Circuit Breakers..." -ForegroundColor Cyan

# Kill gateway
$line = netstat -ano | findstr ":8090" | findstr "LISTENING" | Select-Object -First 1
if ($line) {
    $tokens = $line -split '\s+' | Where-Object { $_ -ne '' }
    $gwPid = $tokens[-1]
    Write-Host "Killing gateway PID: $gwPid"
    taskkill /PID $gwPid /F | Out-Null
    Start-Sleep -Seconds 3
}

# Verify restaurant-service is up
$check = netstat -ano | findstr ":8082" | findstr "LISTENING"
if (-not $check) {
    Write-Host "Restaurant-service is DOWN. Starting it..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList @"
-NoExit -Command `
cd 'C:\Users\avilakad\OneDrive - Publicis Groupe\Documents\Food-Delivery-main\restaurant-service'; `
java -jar target\restaurant-service-1.0.0.jar
"@
    Start-Sleep -Seconds 30
}

# Start gateway
Write-Host "Starting fresh gateway..." -ForegroundColor Green
Start-Process powershell -ArgumentList @"
-NoExit -Command `
cd 'C:\Users\avilakad\OneDrive - Publicis Groupe\Documents\Food-Delivery-main\api-gateway'; `
java -jar target\api-gateway-1.0.0.jar
"@

Start-Sleep -Seconds 30

# Verify CB is CLOSED
Write-Host "`nFinal CB State:" -ForegroundColor Cyan
Invoke-RestMethod http://localhost:8090/actuator/circuitbreakers | ConvertTo-Json -Depth 5

Write-Host "`nReady for demo!" -ForegroundColor Green