# Sync user kubeconfig to ALL Jenkins-readable locations
$src = "$env:USERPROFILE\.kube\config"

if (-not (Test-Path $src)) {
    Write-Host "ERROR: No kubeconfig at $src" -ForegroundColor Red
    exit 1
}

& minikube update-context 2>&1 | Out-Null

$flatPath = "$src.flat"
& kubectl config view --flatten --raw | Out-File $flatPath -Encoding ascii

# All 4 locations Jenkins might read from
$targets = @(
    "C:\ProgramData\Jenkins\.jenkins\.kube",
    "C:\ProgramData\Jenkins\.kube",
    "C:\Windows\System32\config\systemprofile\.kube"
)

foreach ($t in $targets) {
    if (-not (Test-Path $t)) {
        New-Item -ItemType Directory -Path $t -Force -ErrorAction SilentlyContinue | Out-Null
    }
    Copy-Item $flatPath "$t\config" -Force -ErrorAction SilentlyContinue
}

Write-Host "OK - Synced kubeconfig to all Jenkins locations"
$mainConfig = "C:\ProgramData\Jenkins\.jenkins\.kube\config"
if (Test-Path $mainConfig) {
    $serverLine = Get-Content $mainConfig | Select-String "server:" | Select-Object -First 1
    Write-Host "    $($serverLine.Line.Trim())"
}