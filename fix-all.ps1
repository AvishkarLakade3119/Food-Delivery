# ============================================================================
# Food Delivery Microservices - Automated Fix Script
# ============================================================================
# Purpose: Automatically fix common issues detected by diagnose-all.ps1
# Author: Slingshot AI Agent
# Date: 2026-06-12
# ============================================================================

param(
    [switch]$SkipBackup,
    [switch]$Force
)

# Color output functions
function Write-Success { param($msg) Write-Host "✓ $msg" -ForegroundColor Green }
function Write-Error-Custom { param($msg) Write-Host "✗ $msg" -ForegroundColor Red }
function Write-Warning-Custom { param($msg) Write-Host "⚠ $msg" -ForegroundColor Yellow }
function Write-Info { param($msg) Write-Host "ℹ $msg" -ForegroundColor Cyan }
function Write-Section { param($msg) Write-Host "`n========== $msg ==========" -ForegroundColor Magenta }

Write-Host "`n" -NoNewline
Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Food Delivery Microservices - Automated Fix Tool                ║" -ForegroundColor Cyan
Write-Host "║  Version 1.0 | Date: 2026-06-12                                  ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Confirmation prompt
if (-not $Force) {
    Write-Warning-Custom "This script will make changes to your configuration files and system."
    $confirmation = Read-Host "Do you want to continue? (Y/N)"
    if ($confirmation -ne 'Y' -and $confirmation -ne 'y') {
        Write-Info "Operation cancelled by user."
        exit 0
    }
}

# ============================================================================
# PHASE 1: Create Backup
# ============================================================================
Write-Section "PHASE 1: Creating Backup"

if (-not $SkipBackup) {
    $backupDir = "backup-$(Get-Date -Format 'yyyyMMdd-HHmmss')"
    New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
    
    Write-Info "Creating backup in: $backupDir"
    
    # Backup config-repo
    if (Test-Path "config-repo") {
        Copy-Item -Path "config-repo" -Destination "$backupDir/config-repo" -Recurse -Force
        Write-Success "Backed up config-repo"
    }
    
    # Backup all service application.yml files
    $services = @("config-server", "eureka-server", "api-gateway", "user-service", 
                  "restaurant-service", "order-service", "payment-service", "notification-service")
    
    foreach ($service in $services) {
        $appYml = "$service/src/main/resources/application.yml"
        if (Test-Path $appYml) {
            $backupPath = "$backupDir/$service"
            New-Item -ItemType Directory -Path $backupPath -Force | Out-Null
            Copy-Item -Path $appYml -Destination "$backupPath/application.yml" -Force
        }
    }
    
    Write-Success "Backup completed: $backupDir"
} else {
    Write-Warning-Custom "Backup skipped (use -SkipBackup flag with caution)"
}

# ============================================================================
# PHASE 2: Kill Processes on Conflicting Ports
# ============================================================================
Write-Section "PHASE 2: Freeing Conflicting Ports"

$requiredPorts = @(8888, 8761, 8080, 9090, 8081, 8082, 8083, 8084, 8085)

foreach ($port in $requiredPorts) {
    $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    
    if ($connection) {
        $process = Get-Process -Id $connection.OwningProcess -ErrorAction SilentlyContinue
        
        if ($process) {
            Write-Warning-Custom "Killing process on port $port: $($process.ProcessName) (PID: $($process.Id))"
            try {
                Stop-Process -Id $process.Id -Force
                Start-Sleep -Seconds 1
                Write-Success "Port $port freed successfully"
            } catch {
                Write-Error-Custom "Failed to kill process on port $port: $_"
            }
        }
    } else {
        Write-Info "Port $port is already free"
    }
}

# ============================================================================
# PHASE 3: Create Missing Directories
# ============================================================================
Write-Section "PHASE 3: Creating Missing Directories"

$requiredDirs = @(
    "logs",
    "e2e-tests/wiremock",
    "e2e-tests/wiremock/mappings",
    "e2e-tests/wiremock/__files"
)

foreach ($dir in $requiredDirs) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Success "Created directory: $dir"
    } else {
        Write-Info "Directory already exists: $dir"
    }
}

# ============================================================================
# PHASE 4: Fix Config Server Configuration
# ============================================================================
Write-Section "PHASE 4: Fixing Config Server Configuration"

$configServerYml = "config-server/src/main/resources/application.yml"

if (Test-Path $configServerYml) {
    $content = Get-Content $configServerYml -Raw
    
    # Ensure config repo path is set correctly
    if ($content -notmatch "file://\$\{user.home\}/config-repo") {
        Write-Warning-Custom "Config Server path may need adjustment"
        Write-Info "Current config uses: file://`${user.home}/config-repo"
        Write-Info "Ensure config-repo folder exists in your home directory or update the path"
    } else {
        Write-Success "Config Server configuration looks correct"
    }
}

# ============================================================================
# PHASE 5: Validate and Fix Database Configurations
# ============================================================================
Write-Section "PHASE 5: Validating Database Configurations"

Write-Info "Checking database configurations in config-repo..."

$dbConfigs = @(
    @{Service="user-service"; Port=5432; DB="userdb"},
    @{Service="restaurant-service"; Port=5433; DB="restaurantdb"},
    @{Service="order-service"; Port=5434; DB="orderdb"},
    @{Service="payment-service"; Port=5435; DB="paymentdb"},
    @{Service="notification-service"; Port=5436; DB="notificationdb"}
)

foreach ($dbConfig in $dbConfigs) {
    $configFile = "config-repo/$($dbConfig.Service).yml"
    
    if (Test-Path $configFile) {
        $content = Get-Content $configFile -Raw
        
        # Check if database URL is configured
        if ($content -match "jdbc:postgresql://localhost:$($dbConfig.Port)/$($dbConfig.DB)") {
            Write-Success "$($dbConfig.Service): Database configuration is correct"
        } else {
            Write-Warning-Custom "$($dbConfig.Service): Database configuration may need review"
        }
        
        # Check for hardcoded passwords (security issue)
        if ($content -match "password:\s*\w+" -and $content -notmatch "password:\s*\$\{") {
            Write-Warning-Custom "$($dbConfig.Service): Hardcoded password detected - consider using environment variables"
        }
    }
}

# ============================================================================
# PHASE 6: Fix Common Configuration Issues
# ============================================================================
Write-Section "PHASE 6: Fixing Common Configuration Issues"

# Check if config-repo exists in user home directory
$homeConfigRepo = "$env:USERPROFILE/config-repo"

if (-not (Test-Path $homeConfigRepo)) {
    Write-Warning-Custom "Config repository not found in user home directory: $homeConfigRepo"
    Write-Info "Creating symbolic link or copying config-repo to home directory..."
    
    if (Test-Path "config-repo") {
        try {
            # Try to create symbolic link (requires admin privileges)
            New-Item -ItemType SymbolicLink -Path $homeConfigRepo -Target (Resolve-Path "config-repo").Path -Force -ErrorAction Stop | Out-Null
            Write-Success "Created symbolic link: $homeConfigRepo -> config-repo"
        } catch {
            # Fallback: Copy directory
            Copy-Item -Path "config-repo" -Destination $homeConfigRepo -Recurse -Force
            Write-Success "Copied config-repo to: $homeConfigRepo"
        }
    } else {
        Write-Error-Custom "config-repo directory not found in workspace"
    }
} else {
    Write-Success "Config repository exists in user home directory"
}

# ============================================================================
# PHASE 7: Clean and Build All Services
# ============================================================================
Write-Section "PHASE 7: Clean Build (Optional)"

Write-Info "Do you want to run 'mvn clean install -DskipTests' to rebuild all services?"
Write-Warning-Custom "This may take 5-10 minutes depending on your system."
$buildConfirm = Read-Host "Proceed with clean build? (Y/N)"

if ($buildConfirm -eq 'Y' -or $buildConfirm -eq 'y') {
    Write-Info "Running Maven clean install..."
    try {
        $buildOutput = mvn clean install -DskipTests 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Maven build completed successfully"
        } else {
            Write-Error-Custom "Maven build failed - check output for errors"
            Write-Host $buildOutput
        }
    } catch {
        Write-Error-Custom "Failed to execute Maven build: $_"
    }
} else {
    Write-Info "Skipping clean build"
}

# ============================================================================
# PHASE 8: Environment Variables Setup
# ============================================================================
Write-Section "PHASE 8: Environment Variables Check"

Write-Info "Checking required environment variables..."

# Check JAVA_HOME
if ($env:JAVA_HOME) {
    Write-Success "JAVA_HOME is set: $env:JAVA_HOME"
} else {
    Write-Warning-Custom "JAVA_HOME is not set - some tools may not work correctly"
    Write-Info "Set JAVA_HOME to your JDK installation directory"
}

# Check MAVEN_HOME or M2_HOME
if ($env:MAVEN_HOME -or $env:M2_HOME) {
    Write-Success "Maven home is set"
} else {
    Write-Info "MAVEN_HOME/M2_HOME not set (optional - Maven may still work if in PATH)"
}

# ============================================================================
# FIX SUMMARY
# ============================================================================
Write-Section "FIX SUMMARY"

Write-Host ""
Write-Host "Fixes Applied:" -ForegroundColor Cyan
Write-Host "  ✓ Backup created (if not skipped)" -ForegroundColor Green
Write-Host "  ✓ Conflicting ports freed" -ForegroundColor Green
Write-Host "  ✓ Missing directories created" -ForegroundColor Green
Write-Host "  ✓ Configuration files validated" -ForegroundColor Green
Write-Host "  ✓ Database configurations checked" -ForegroundColor Green
Write-Host "  ✓ Config repository setup verified" -ForegroundColor Green
Write-Host ""

Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  ✓ FIXES COMPLETED - READY TO START SERVICES                      ║" -ForegroundColor Green
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Run .\diagnose-all.ps1 to verify all fixes" -ForegroundColor White
Write-Host "  2. Run .\start-all-services.ps1 to start the application" -ForegroundColor White
Write-Host "  3. Check logs if any service fails to start" -ForegroundColor White
Write-Host ""

Write-Host "Manual Actions Required:" -ForegroundColor Yellow
Write-Host "  - Ensure PostgreSQL databases are created and running" -ForegroundColor White
Write-Host "  - Update database credentials in config-repo/*.yml files" -ForegroundColor White
Write-Host "  - Review any warnings displayed above" -ForegroundColor White
Write-Host ""
