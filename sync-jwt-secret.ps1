# ========================================
# JWT Secret Sync Script
# Updates jwt.secret in ALL config files
# ========================================

$OLD_SECRET = "food-delivery-platform-jwt-secret-key-2026-must-be-at-least-256-bits-long-for-hmac-sha256"
$NEW_SECRET = "a-very-long-secure-secret-key-that-is-at-least-256-bits-for-hs256-algorithm-food-delivery-platform-2026"

$PROJECT_ROOT = "C:\Users\avilakad\OneDrive - Publicis Groupe\Documents\Food-Delivery-main"
$CONFIG_REPO = "C:\Users\avilakad\config-repo"

$updatedCount = 0
$skippedCount = 0
$errorCount = 0

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  JWT Secret Sync - All Services" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# List of all files to update
$filesToUpdate = @(
    # Config repo files
    "$CONFIG_REPO\application.yml",
    "$CONFIG_REPO\order-service.yml",
    "$CONFIG_REPO\payment-service.yml",
    "$CONFIG_REPO\restaurant-service.yml",
    "$CONFIG_REPO\notification-service.yml",
    "$CONFIG_REPO\user-service.yml",
    "$CONFIG_REPO\api-gateway.yml",

    # Service main application.yml files
    "$PROJECT_ROOT\order-service\src\main\resources\application.yml",
    "$PROJECT_ROOT\payment-service\src\main\resources\application.yml",
    "$PROJECT_ROOT\restaurant-service\src\main\resources\application.yml",
    "$PROJECT_ROOT\notification-service\src\main\resources\application.yml",
    "$PROJECT_ROOT\user-service\src\main\resources\application.yml",

    # Service local profile files
    "$PROJECT_ROOT\order-service\src\main\resources\application-local.yml",
    "$PROJECT_ROOT\payment-service\src\main\resources\application-local.yml",
    "$PROJECT_ROOT\restaurant-service\src\main\resources\application-local.yml",
    "$PROJECT_ROOT\notification-service\src\main\resources\application-local.yml",
    "$PROJECT_ROOT\user-service\src\main\resources\application-local.yml",

    # Service test yml files
    "$PROJECT_ROOT\order-service\src\test\resources\application-test.yml",
    "$PROJECT_ROOT\payment-service\src\test\resources\application-test.yml",
    "$PROJECT_ROOT\restaurant-service\src\test\resources\application-test.yml",
    "$PROJECT_ROOT\notification-service\src\test\resources\application-test.yml",
    "$PROJECT_ROOT\user-service\src\test\resources\application-test.yml"
)

# Test .properties files
$propertiesFiles = @(
    "$PROJECT_ROOT\order-service\src\test\resources\application.properties",
    "$PROJECT_ROOT\payment-service\src\test\resources\application.properties",
    "$PROJECT_ROOT\restaurant-service\src\test\resources\application.properties",
    "$PROJECT_ROOT\notification-service\src\test\resources\application.properties",
    "$PROJECT_ROOT\user-service\src\test\resources\application.properties"
)

Write-Host "[STEP 1] Updating YAML config files..." -ForegroundColor Yellow
Write-Host ""

foreach ($file in $filesToUpdate) {
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        if ($content -match [regex]::Escape($OLD_SECRET)) {
            $newContent = $content -replace [regex]::Escape($OLD_SECRET), $NEW_SECRET
            Set-Content -Path $file -Value $newContent -NoNewline
            Write-Host "  UPDATED: $file" -ForegroundColor Green
            $updatedCount++
        } elseif ($content -match [regex]::Escape($NEW_SECRET)) {
            Write-Host "  ALREADY OK: $file" -ForegroundColor DarkGray
            $skippedCount++
        } else {
            Write-Host "  NO JWT SECRET FOUND: $file" -ForegroundColor DarkYellow
            $skippedCount++
        }
    } else {
        Write-Host "  NOT FOUND (skip): $file" -ForegroundColor DarkGray
        $skippedCount++
    }
}

Write-Host ""
Write-Host "[STEP 2] Updating .properties test files..." -ForegroundColor Yellow
Write-Host ""

$OLD_PROP1 = "jwt.secret=food-delivery-platform-jwt-secret-key-2026-must-be-at-least-256-bits-long-for-hmac-sha256"
$OLD_PROP2 = "jwt.secret=test-secret-key-for-unit-testing-must-be-long-enough-for-hs256-algorithm-food-delivery"
$OLD_PROP3 = "jwt.secret=test-secret-key-that-is-at-least-256-bits-for-hs256-algorithm-testing-purpose-only"
$NEW_PROP = "jwt.secret=a-very-long-secure-secret-key-that-is-at-least-256-bits-for-hs256-algorithm-food-delivery-platform-2026"

foreach ($file in $propertiesFiles) {
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        $changed = $false

        if ($content -match [regex]::Escape($OLD_PROP1)) {
            $content = $content -replace [regex]::Escape($OLD_PROP1), $NEW_PROP
            $changed = $true
        }
        if ($content -match [regex]::Escape($OLD_PROP2)) {
            $content = $content -replace [regex]::Escape($OLD_PROP2), $NEW_PROP
            $changed = $true
        }
        if ($content -match [regex]::Escape($OLD_PROP3)) {
            $content = $content -replace [regex]::Escape($OLD_PROP3), $NEW_PROP
            $changed = $true
        }

        if ($changed) {
            Set-Content -Path $file -Value $content -NoNewline
            Write-Host "  UPDATED: $file" -ForegroundColor Green
            $updatedCount++
        } elseif ($content -match "jwt.secret=a-very-long-secure") {
            Write-Host "  ALREADY OK: $file" -ForegroundColor DarkGray
            $skippedCount++
        } else {
            Write-Host "  NO JWT SECRET FOUND: $file" -ForegroundColor DarkYellow
            $skippedCount++
        }
    } else {
        Write-Host "  NOT FOUND (skip): $file" -ForegroundColor DarkGray
        $skippedCount++
    }
}

Write-Host ""
Write-Host "[STEP 3] Committing config-repo changes..." -ForegroundColor Yellow
Write-Host ""

try {
    Push-Location $CONFIG_REPO
    git add -A
    $status = git status --porcelain
    if ($status) {
        git commit -m "Sync JWT secret across all services to match user-service"
        Write-Host "  Git commit successful" -ForegroundColor Green
    } else {
        Write-Host "  No changes to commit in config-repo" -ForegroundColor DarkGray
    }
    Pop-Location
} catch {
    Write-Host "  Git commit failed: $_" -ForegroundColor Red
    $errorCount++
    Pop-Location
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Files Updated : $updatedCount" -ForegroundColor Green
Write-Host "  Files Skipped : $skippedCount" -ForegroundColor DarkGray
Write-Host "  Errors        : $errorCount" -ForegroundColor Red
Write-Host ""
Write-Host "  JWT Secret (all services):" -ForegroundColor White
Write-Host "  $NEW_SECRET" -ForegroundColor Yellow
Write-Host ""
Write-Host "  NEXT STEPS:" -ForegroundColor White
Write-Host "  1. Restart order-service" -ForegroundColor White
Write-Host "  2. Restart payment-service" -ForegroundColor White
Write-Host "  3. Restart restaurant-service" -ForegroundColor White
Write-Host "  4. Restart notification-service" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan