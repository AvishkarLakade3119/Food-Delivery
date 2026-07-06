# check-monitoring.ps1 — Quick status of the daemon + URLs

Write-Host "`n=== JOBS ===" -ForegroundColor Cyan
Get-Job | Where-Object { $_.Name -like "pf-*" } | Format-Table Name, State -AutoSize

Write-Host "`n=== ACCESS URLS ===" -ForegroundColor Yellow
$endpoints = @(
    @{ N = "Grafana";     U = "http://localhost:3000";  A = "admin/admin" },
    @{ N = "Prometheus";  U = "http://localhost:9090";  A = "" },
    @{ N = "Zipkin";      U = "http://localhost:9411/zipkin/"; A = "" },
    @{ N = "RabbitMQ";    U = "http://localhost:15672"; A = "guest/guest" },
    @{ N = "API Gateway"; U = "https://localhost:8443"; A = "" }
)

foreach ($e in $endpoints) {
    $urlOnly = $e.U -replace ".*://", "" -replace "/.*", ""
    $port = ($urlOnly -split ":")[1]
    $up = Test-NetConnection -ComputerName localhost -Port $port -InformationLevel Quiet -WarningAction SilentlyContinue -ErrorAction SilentlyContinue
    $status = if ($up) { "OK " } else { "DOWN" }
    $color = if ($up) { "Green" } else { "Red" }
    Write-Host ("  [{0}] {1,-12} : {2,-40} {3}" -f $status, $e.N, $e.U, $e.A) -ForegroundColor $color
}

Write-Host "`n=== LATEST LOG (last 10 lines) ===" -ForegroundColor Cyan
$logFile = "$PSScriptRoot\port-forward.log"
if (Test-Path $logFile) {
    Get-Content $logFile -Tail 10
} else {
    Write-Host "  No log yet - daemon may not have run" -ForegroundColor Yellow
}
