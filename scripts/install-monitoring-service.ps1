# install-monitoring-service.ps1
# One-time setup: registers start-monitoring.ps1 to run on Windows boot
# Run this ONCE as Administrator

if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Host "This script must be run as Administrator!" -ForegroundColor Red
    Write-Host "Right-click PowerShell -> Run as Administrator, then run this script." -ForegroundColor Yellow
    exit 1
}

$scriptPath = Join-Path $PSScriptRoot "start-monitoring.ps1"
$taskName = "FoodDelivery-PortForwards"

Write-Host "=== REGISTERING WINDOWS TASK ===" -ForegroundColor Cyan
Write-Host "Task name: $taskName"
Write-Host "Script:    $scriptPath"

# Remove existing task if any
Unregister-ScheduledTask -TaskName $taskName -Confirm:$false -ErrorAction SilentlyContinue

# Create triggers: at logon AND at startup
$trigger1 = New-ScheduledTaskTrigger -AtLogOn
$trigger2 = New-ScheduledTaskTrigger -AtStartup

# Action: run PowerShell with the daemon script (hidden window)
$action = New-ScheduledTaskAction -Execute "powershell.exe" -Argument "-NoProfile -WindowStyle Hidden -ExecutionPolicy Bypass -File `"$scriptPath`""

# Principal: run as current user, highest privileges
$principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -LogonType Interactive -RunLevel Highest

# Settings (PS 5.1 compatible - no RestartOnFailure param)
$settings = New-ScheduledTaskSettingsSet `
    -AllowStartIfOnBatteries `
    -DontStopIfGoingOnBatteries `
    -StartWhenAvailable `
    -MultipleInstances IgnoreNew `
    -ExecutionTimeLimit (New-TimeSpan -Hours 0)

# Register the task
Register-ScheduledTask `
    -TaskName $taskName `
    -Trigger @($trigger1, $trigger2) `
    -Action $action `
    -Principal $principal `
    -Settings $settings `
    -Description "Auto-manages port-forwards for Food Delivery K8s stack" | Out-Null

# Configure restart-on-failure via COM API (works on all Windows versions)
Write-Host "Configuring auto-restart on failure..." -ForegroundColor Cyan
$service = New-Object -ComObject Schedule.Service
$service.Connect()
$folder = $service.GetFolder("\")
$registeredTask = $folder.GetTask($taskName)
$def = $registeredTask.Definition
$def.Settings.RestartCount = 5
$def.Settings.RestartInterval = "PT5M"  # 5 minutes ISO 8601
$folder.RegisterTaskDefinition($taskName, $def, 6, $null, $null, 3) | Out-Null

Write-Host "`nOK - Task registered!" -ForegroundColor Green
Write-Host ""
Write-Host "The daemon will:" -ForegroundColor Yellow
Write-Host "  - Start automatically when you log in" -ForegroundColor White
Write-Host "  - Start automatically when Windows boots" -ForegroundColor White
Write-Host "  - Restart automatically if it dies (5 retries, 5 min apart)" -ForegroundColor White
Write-Host "  - Wait for minikube + namespace to be ready" -ForegroundColor White
Write-Host "  - Restart any port-forward that stops working" -ForegroundColor White
Write-Host ""

Write-Host "Starting task NOW..." -ForegroundColor Cyan
Start-ScheduledTask -TaskName $taskName
Start-Sleep -Seconds 3

$task = Get-ScheduledTask -TaskName $taskName
Write-Host "Task state: $($task.State)" -ForegroundColor Green

Write-Host ""
Write-Host "Check status any time with:" -ForegroundColor Yellow
Write-Host "  .\scripts\check-monitoring.ps1" -ForegroundColor Cyan
Write-Host ""
Write-Host "To UNINSTALL later:" -ForegroundColor Gray
Write-Host "  Unregister-ScheduledTask -TaskName '$taskName' -Confirm:`$false" -ForegroundColor Gray
