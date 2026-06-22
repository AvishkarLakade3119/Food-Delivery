Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   FOOD DELIVERY MONITORING STATUS" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. Service Health
Write-Host "[1] Services (Eureka registered):" -ForegroundColor Yellow
try {
    $eureka = Invoke-RestMethod http://localhost:8761/eureka/apps -Headers @{Accept = "application/json" }
    $eureka.applications.application | ForEach-Object {
        $count = $_.instance.Count
        if ($count -eq $null) { $count = 1 }
        Write-Host "    $($_.name.PadRight(25)) : UP ($count instance)" -ForegroundColor Green
    }
}
catch {
    Write-Host "    Eureka not reachable" -ForegroundColor Red
}

# 2. Prometheus Targets
Write-Host "`n[2] Prometheus Targets:" -ForegroundColor Yellow
try {
    $targets = Invoke-RestMethod http://localhost:9090/api/v1/targets
    $up = ($targets.data.activeTargets | Where-Object { $_.health -eq "up" }).Count
    $down = ($targets.data.activeTargets | Where-Object { $_.health -eq "down" }).Count
    $total = $targets.data.activeTargets.Count
    Write-Host "    $up/$total UP" -ForegroundColor Green
    if ($down -gt 0) {
        Write-Host "    Down targets:" -ForegroundColor Red
        $targets.data.activeTargets | Where-Object { $_.health -eq "down" } | ForEach-Object {
            $app = if ($_.labels.application) { $_.labels.application } else { $_.labels.job }
            Write-Host "      - $app : $($_.lastError)" -ForegroundColor Red
        }
    }
}
catch {
    Write-Host "    Prometheus not reachable" -ForegroundColor Red
}

# 3. Grafana
Write-Host "`n[3] Grafana:" -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod http://localhost:3000/api/health
    Write-Host "    Status: $($health.database)" -ForegroundColor Green
}
catch {
    Write-Host "    Grafana not reachable" -ForegroundColor Red
}

# 4. RabbitMQ Queues
Write-Host "`n[4] RabbitMQ Queue Stats:" -ForegroundColor Yellow
docker exec rabbitmq rabbitmqctl list_queues name messages consumers 2>&1 | findstr "queue" | findstr -v "dlq" | ForEach-Object {
    Write-Host "    $_" -ForegroundColor White
}

# 5. URLs
Write-Host "`n[5] Quick Links:" -ForegroundColor Yellow
Write-Host "    Eureka:     http://localhost:8761" -ForegroundColor Cyan
Write-Host "    Prometheus: http://localhost:9090/targets" -ForegroundColor Cyan
Write-Host "    Grafana:    http://localhost:3000" -ForegroundColor Cyan
Write-Host "    RabbitMQ:   http://localhost:15672 (guest/guest)" -ForegroundColor Cyan

Write-Host "`n========================================`n" -ForegroundColor Cyan