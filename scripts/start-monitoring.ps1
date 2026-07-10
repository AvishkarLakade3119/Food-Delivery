# start-monitoring.ps1 - Persistent port-forward daemon
# Auto-restarts port-forwards when they die or when pods change

$ErrorActionPreference = "Continue"
$logFile = "$PSScriptRoot\port-forward.log"

function Write-Log {
    param($msg, $color = "White")
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    "$timestamp | $msg" | Out-File -FilePath $logFile -Append
    Write-Host "$timestamp | $msg" -ForegroundColor $color
}

$forwards = @(
    @{ N = "prometheus";  S = "prometheus";  L = 9090;  R = 9090  },
    @{ N = "grafana";     S = "grafana";     L = 3000;  R = 3000  },
    @{ N = "zipkin";      S = "zipkin";      L = 9411;  R = 9411  },
    @{ N = "rabbitmq";    S = "rabbitmq";    L = 15672; R = 15672 },
    @{ N = "api-gateway"; S = "api-gateway"; L = 8443;  R = 8443  }
)

Write-Log "=== PORT-FORWARD DAEMON STARTING ===" "Cyan"

# Wait for minikube ready
Write-Log "Waiting for minikube..." "Yellow"
$maxWait = 300
$waited = 0
while ($waited -lt $maxWait) {
    $mkStatus = & minikube status 2>&1 | Out-String
    if ($mkStatus -match "Running") {
        Write-Log "Minikube is Running" "Green"
        break
    }
    Start-Sleep -Seconds 5
    $waited += 5
}

# Wait for namespace
Write-Log "Waiting for food-delivery namespace..." "Yellow"
$waited = 0
while ($waited -lt $maxWait) {
    $ns = & kubectl get namespace food-delivery 2>&1 | Out-String
    if ($ns -match "Active") {
        Write-Log "Namespace food-delivery is Active" "Green"
        break
    }
    Start-Sleep -Seconds 5
    $waited += 5
}

# Main monitor loop
Write-Log "=== ENTERING MONITOR LOOP ===" "Cyan"

while ($true) {
    foreach ($f in $forwards) {
        $svcName = $f.S
        $localPort = $f.L
        $remotePort = $f.R
        $jobName = "pf-$($f.N)"

        # Check if service exists
        $svcCheck = & kubectl get svc -n food-delivery $svcName 2>&1 | Out-String
        if ($svcCheck -notmatch $svcName) {
            continue
        }

        # Check existing job health
        $existingJob = Get-Job -Name $jobName -ErrorAction SilentlyContinue

        if ($existingJob -and $existingJob.State -eq "Running") {
            # Verify port actually responds
            $portCheck = Test-NetConnection -ComputerName localhost -Port $localPort -InformationLevel Quiet -WarningAction SilentlyContinue -ErrorAction SilentlyContinue
            if ($portCheck) {
                continue
            } else {
                Write-Log "Port $localPort not responding, restarting $jobName" "Yellow"
                Stop-Job $existingJob -ErrorAction SilentlyContinue
                Remove-Job $existingJob -Force -ErrorAction SilentlyContinue
            }
        } elseif ($existingJob) {
            Remove-Job $existingJob -Force -ErrorAction SilentlyContinue
        }

        # Start fresh port-forward
        $portArg = "${localPort}:${remotePort}"
        Start-Job -Name $jobName -ScriptBlock {
            param($svc, $portArg)
            kubectl port-forward -n food-delivery svc/$svc $portArg
        } -ArgumentList $svcName, $portArg | Out-Null
        Write-Log "Started $jobName -> localhost:$localPort" "Green"
    }

    Start-Sleep -Seconds 30
}