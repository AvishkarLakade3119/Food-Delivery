param([string]$Service, [int]$Port)
$Host.UI.RawUI.WindowTitle = "pf-$Service"

while ($true) {
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Starting port-forward for $Service:$Port..." -ForegroundColor Green
    kubectl port-forward -n food-delivery svc/$Service ${Port}:${Port} 2>&1
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Port-forward died. Restarting in 3s..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
}