@echo off
echo ========================================
echo   FOOD DELIVERY E2E TESTS RUNNER
echo ========================================
echo.
echo Starting comprehensive E2E test suite...
echo.

REM Check if Docker is running
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running or not installed!
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)

echo [1/4] Docker is running ✓
echo.

REM Clean and compile
echo [2/4] Cleaning and compiling E2E tests...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful ✓
echo.

REM Run all E2E tests
echo [3/4] Running comprehensive E2E test suite...
echo.
echo Running Order Placement Workflow Tests...
mvn test -Dtest=OrderPlacementWorkflowE2ETest -Dspring.profiles.active=e2e-test

echo.
echo Running Service Integration Tests...
mvn test -Dtest=ServiceIntegrationTest -Dspring.profiles.active=e2e-test

echo.
echo Running Performance and Load Tests...
mvn test -Dtest=PerformanceAndLoadE2ETest -Dspring.profiles.active=e2e-test

echo.
echo [4/4] Generating test reports...
mvn surefire-report:report site -q

echo.
echo ========================================
echo   E2E TEST EXECUTION COMPLETED!
echo ========================================
echo.
echo Test reports available at:
echo - target\surefire-reports\index.html
echo - target\site\index.html
echo.
echo Opening test reports...
if exist "target\surefire-reports\index.html" (
    start target\surefire-reports\index.html
) else (
    echo Test report not found. Check console output above for errors.
)

echo.
pause