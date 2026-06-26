# Sync user kubeconfig to Jenkins-readable locations
$src = "$env:USERPROFILE\.kube\config"

if (-not (Test-Path $src)) {
    Write-Host "ERROR: No kubeconfig at $src" -ForegroundColor Red
    exit 1
}

& minikube update-context 2>&1 | Out-Null

$flatPath = "$src.flat"
& kubectl config view --flatten --raw | Out-File $flatPath -Encoding ascii

$targets = @(
    "C:\ProgramData\Jenkins\.jenkins\.kube",
    "C:\Windows\System32\config\systemprofile\.kube"
)

foreach ($t in $targets) {
    if (-not (Test-Path $t)) {
        New-Item -ItemType Directory -Path $t -Force -ErrorAction SilentlyContinue | Out-Null
    }
    Copy-Item $flatPath "$t\config" -Force -ErrorAction SilentlyContinue
}

$jenkinsConfig = "C:\ProgramData\Jenkins\.jenkins\.kube\config"
if (Test-Path $jenkinsConfig) {
    $serverLine = Get-Content $jenkinsConfig | Select-String "server:" | Select-Object -First 1
    Write-Host "OK - Jenkins kubeconfig updated"
    Write-Host "    $($serverLine.Line.Trim())"
} else {
    Write-Host "ERROR: Could not write to Jenkins kubeconfig"
    exit 1
}