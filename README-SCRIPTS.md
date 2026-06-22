# Food Delivery Microservices - Automation Scripts Guide

**Complete Solution for Diagnosis, Fixes, Startup, and Testing**

---

## 🚀 Quick Start (3 Commands)

```powershell
# 1. Diagnose your system
.\diagnose-all.ps1

# 2. Fix any issues found
.\fix-all.ps1

# 3. Start all services
.\start-all-services.ps1
```

**That's it!** Your entire microservices platform is now running.

---

## 📁 Available Scripts

| Script | Purpose | When to Use |
|--------|---------|-------------|
| `diagnose-all.ps1` | Complete system diagnostic | Before starting services |
| `fix-all.ps1` | Automated issue resolution | When diagnostics show errors |
| `start-all-services.ps1` | Start all services in order | To launch the platform |
| `verify-system.ps1` | Health check all services | After services are running |
| `run-e2e-tests.ps1` | Execute E2E tests | To validate functionality |

---

## 🔍 diagnose-all.ps1

**Purpose:** Comprehensive pre-flight check before starting services

### What It Checks

✅ Java 17+ installation  
✅ Maven installation  
✅ Port availability (8888, 8761, 8080, 8081-8085, 9090)  
✅ All pom.xml files validity  
✅ Spring Boot/Cloud version compatibility  
✅ Configuration files existence  
✅ Database configurations  
✅ WireMock directory structure  
✅ Maven compilation validation  
✅ Service health (if already running)  

### Usage

```powershell
# Basic diagnostic
.\diagnose-all.ps1

# Verbose output with detailed logs
.\diagnose-all.ps1 -Verbose

# Export diagnostic report to file
.\diagnose-all.ps1 -ExportReport
```

### Output Example

```
✓ Java 17 detected (Required: Java 17+)
✓ Maven detected: 3.9.5
✓ Port 8888 (Config Server) is AVAILABLE
✓ Port 8761 (Eureka Server) is AVAILABLE
✓ Valid POM: pom.xml (food-delivery-system)
✓ Spring Boot and Cloud versions are compatible
✓ Configuration file exists: config-repo/application.yml

╔═══════════════════════════════════════════════════════════════════╗
║  ✓ ALL CHECKS PASSED - SYSTEM READY FOR STARTUP                  ║
╚═══════════════════════════════════════════════════════════════════╝
```

---

## 🔧 fix-all.ps1

**Purpose:** Automatically fix common issues detected by diagnostics

### What It Fixes

✅ Kills processes on conflicting ports  
✅ Creates missing directories (logs, wiremock/*)  
✅ Sets up config-repo in user home directory  
✅ Validates database configurations  
✅ Creates backup before making changes  
✅ Optionally runs clean Maven build  
✅ Verifies environment variables  

### Usage

```powershell
# Standard usage (with confirmation and backup)
.\fix-all.ps1

# Skip backup (not recommended)
.\fix-all.ps1 -SkipBackup

# Force execution without prompts
.\fix-all.ps1 -Force
```

### Safety Features

- **Automatic Backup**: Creates timestamped backup before changes
- **Confirmation Prompts**: Asks before making changes
- **Rollback Support**: Backup can be restored if needed

### Output Example

```
✓ Backup completed: backup-20260612-143022
✓ Port 8888 freed successfully
✓ Created directory: e2e-tests/wiremock/mappings
✓ Config repository exists in user home directory

╔═══════════════════════════════════════════════════════════════════╗
║  ✓ FIXES COMPLETED - READY TO START SERVICES                      ║
╚═══════════════════════════════════════════════════════════════════╝
```

---

## 🚀 start-all-services.ps1

**Purpose:** Start all microservices in correct order with health monitoring

### Startup Sequence

1. **Config Server** (Port 8888) → Wait for health endpoint
2. **Eureka Server** (Port 8761) → Wait for dashboard
3. **Business Services** (Parallel) → Wait for Eureka registration
   - User Service (8085)
   - Restaurant Service (8082)
   - Order Service (8083)
   - Payment Service (8084)
   - Notification Service (8081)
4. **API Gateway** (Port 8080) → Wait for route configuration

### Features

✅ Health check after each service starts  
✅ Eureka registration verification  
✅ Automatic Eureka Dashboard opening  
✅ Color-coded real-time status updates  
✅ Timeout handling with graceful failures  
✅ Process tracking for easy shutdown  

### Usage

```powershell
# Full startup with build
.\start-all-services.ps1

# Skip build (faster, uses existing JARs)
.\start-all-services.ps1 -SkipBuild

# Verbose output
.\start-all-services.ps1 -Verbose
```

### Output Example

```
ℹ Starting Config Server...
✓ Config Server process started (PID: 12345)
ℹ Waiting for Config Server to become healthy...
✓ Config Server is UP and healthy

ℹ Starting Eureka Server...
✓ Eureka Server is UP and healthy
ℹ Opening Eureka Dashboard...

ℹ Starting all business services in parallel...
✓ User Service is UP and healthy
✓ USER-SERVICE registered with Eureka

╔═══════════════════════════════════════════════════════════════════╗
║  ✓ STARTUP COMPLETE - ALL SERVICES LAUNCHED                       ║
╚═══════════════════════════════════════════════════════════════════╝
```

### Important URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Config Server**: http://localhost:8888

---

## ✔️ verify-system.ps1

**Purpose:** Comprehensive health check and smoke testing

### Verification Phases

1. **Service Health Checks** - All actuator/health endpoints
2. **Eureka Registration** - Verify all services registered
3. **API Gateway Routes** - Check route configuration
4. **Database Connectivity** - Verify database connections
5. **Smoke Tests** - Test services through API Gateway

### Usage

```powershell
# Full verification
.\verify-system.ps1

# Skip smoke tests
.\verify-system.ps1 -SkipSmokeTests

# Generate health report file
.\verify-system.ps1 -GenerateReport
```

### Output Example

```
✓ Config Server (Port 8888) - UP
✓ Eureka Server (Port 8761) - UP
✓ User Service (Port 8085) - UP
✓ USER-SERVICE is registered
✓ API Gateway routes endpoint is accessible
✓ User Service - Database connection OK
✓ Smoke Test 1: User Service accessible through API Gateway

╔═══════════════════════════════════════════════════════════════════╗
║  ✓ ALL VERIFICATIONS PASSED - SYSTEM FULLY OPERATIONAL            ║
╚═══════════════════════════════════════════════════════════════════╝
```

---

## 🧪 run-e2e-tests.ps1

**Purpose:** Execute comprehensive end-to-end tests

### Test Categories

1. **Integration Tests** - Service integration verification
2. **Workflow Tests** - Order placement workflows
3. **Performance Tests** - Load and performance testing

### Features

✅ Pre-test service verification  
✅ Eureka registration check  
✅ WireMock setup validation  
✅ Test execution with detailed results  
✅ HTML report generation  
✅ Failure stack trace capture  
✅ Test coverage summary  

### Usage

```powershell
# Run all E2E tests
.\run-e2e-tests.ps1

# Run only integration tests
.\run-e2e-tests.ps1 -IntegrationOnly

# Run only workflow tests
.\run-e2e-tests.ps1 -WorkflowOnly

# Run only performance tests
.\run-e2e-tests.ps1 -PerformanceOnly

# Generate HTML report
.\run-e2e-tests.ps1 -GenerateReport

# Skip service verification (not recommended)
.\run-e2e-tests.ps1 -SkipServiceCheck
```

### Output Example

```
✓ Config Server is UP
✓ Eureka Server is UP
✓ USER-SERVICE is registered with Eureka
✓ WireMock directory exists: e2e-tests/wiremock/mappings

ℹ Running test: ServiceIntegrationTest
✓ ServiceIntegrationTest: PASSED (15 tests)

ℹ Running test: OrderPlacementWorkflowE2ETest
✓ OrderPlacementWorkflowE2ETest: PASSED (8 tests)

Test Execution Summary:
======================
Total Tests:   23
✓ Passed:        23
✗ Failed:        0
⚠ Skipped:       0

Success Rate:  100%

╔═══════════════════════════════════════════════════════════════════╗
║  ✓ ALL E2E TESTS PASSED - SYSTEM VERIFIED                        ║
╚═══════════════════════════════════════════════════════════════════╝
```

---

## 📊 Complete Workflow

### First Time Setup

```powershell
# Step 1: Diagnose system
.\diagnose-all.ps1 -ExportReport

# Step 2: Fix any issues
.\fix-all.ps1

# Step 3: Start services
.\start-all-services.ps1

# Step 4: Verify everything is working
.\verify-system.ps1 -GenerateReport

# Step 5: Run E2E tests
.\run-e2e-tests.ps1 -GenerateReport
```

### Daily Development Workflow

```powershell
# Quick start (skip build if no code changes)
.\start-all-services.ps1 -SkipBuild

# Verify services are healthy
.\verify-system.ps1

# Run specific tests
.\run-e2e-tests.ps1 -IntegrationOnly
```

### Troubleshooting Workflow

```powershell
# Step 1: Run diagnostics
.\diagnose-all.ps1 -Verbose -ExportReport

# Step 2: Review diagnostic report
Get-Content diagnostic-report-*.txt

# Step 3: Apply fixes
.\fix-all.ps1

# Step 4: Verify fixes
.\diagnose-all.ps1

# Step 5: Restart services
.\start-all-services.ps1
```

---

## 📝 Documentation Files

| File | Description |
|------|-------------|
| `COMPLETE-SETUP-GUIDE.md` | Comprehensive setup, troubleshooting, and API documentation |
| `README-SCRIPTS.md` | This file - script usage guide |
| `API_TESTING_REPORT.md` | API endpoint testing results |
| `COMPREHENSIVE_HEALTH_CHECK_REPORT.md` | System health check results |

---

## ⚙️ Configuration Files

### Config Repository Structure

```
config-repo/
├── application.yml              # Shared configuration
├── user-service.yml            # User service config
├── restaurant-service.yml      # Restaurant service config
├── order-service.yml           # Order service config
├── payment-service.yml         # Payment service config
├── notification-service.yml    # Notification service config
├── api-gateway.yml             # API Gateway config
└── eureka-server.yml           # Eureka Server config
```

### Important Configuration Locations

- **Config Server**: `config-server/src/main/resources/application.yml`
- **Service Configs**: `{service}/src/main/resources/application.yml`
- **Test Configs**: `{service}/src/test/resources/application-test.yml`
- **E2E Test Config**: `e2e-tests/src/test/resources/application-e2e-test.yml`

---

## 🐞 Common Issues and Quick Fixes

### Port Already in Use

```powershell
.\fix-all.ps1  # Automatically kills processes on conflicting ports
```

### Config Server Not Found

```powershell
.\fix-all.ps1  # Sets up config-repo in user home directory
```

### Services Not Registering with Eureka

```powershell
# Wait 30-60 seconds after Eureka starts
# Check Eureka Dashboard: http://localhost:8761
```

### Database Connection Failed

```powershell
# Ensure PostgreSQL is running
Get-Service -Name postgresql*

# Or use H2 in-memory database (default for tests)
```

### Maven Build Failures

```powershell
# Clean and rebuild
mvn clean install -DskipTests

# Or use fix-all.ps1 with build option
.\fix-all.ps1  # Select 'Y' when prompted for clean build
```

---

## 🛡️ Safety Features

### Automatic Backups

- `fix-all.ps1` creates timestamped backups before making changes
- Backup location: `backup-{timestamp}/`
- Includes config-repo and all service application.yml files

### Confirmation Prompts

- Scripts ask for confirmation before destructive operations
- Use `-Force` flag to skip prompts (use with caution)

### Graceful Failure Handling

- Scripts continue with warnings instead of failing completely
- Color-coded output: Green (success), Yellow (warning), Red (error)
- Detailed error messages with suggested fixes

---

## 📊 Monitoring and Logs

### Service Logs

Each service runs in its own PowerShell window with real-time logs.

### Actuator Endpoints

```powershell
# Health check
curl http://localhost:8085/actuator/health

# Service info
curl http://localhost:8085/actuator/info

# Metrics
curl http://localhost:8085/actuator/metrics

# Environment
curl http://localhost:8085/actuator/env
```

### Eureka Dashboard

Monitor all registered services: http://localhost:8761

### API Gateway Routes

```powershell
curl http://localhost:8080/actuator/gateway/routes
```

---

## 🎯 Performance Tips

### Skip Build for Faster Startup

```powershell
.\start-all-services.ps1 -SkipBuild
```

### Run Tests in Parallel

```powershell
# Run different test categories in separate windows
Start-Process powershell -ArgumentList ".\run-e2e-tests.ps1 -IntegrationOnly"
Start-Process powershell -ArgumentList ".\run-e2e-tests.ps1 -WorkflowOnly"
```

### Increase Maven Memory

```powershell
$env:MAVEN_OPTS = "-Xmx2048m"
```

---

## 🔒 Security Notes

1. **Never commit** database passwords to config-repo
2. Use **environment variables** for sensitive data
3. Rotate **JWT secrets** regularly
4. Enable **HTTPS** in production
5. Implement **rate limiting** on API Gateway

---

## 📞 Support

For issues or questions:

1. Check `COMPLETE-SETUP-GUIDE.md` for detailed troubleshooting
2. Run `diagnose-all.ps1 -Verbose -ExportReport` and review the report
3. Check service logs in PowerShell windows
4. Verify Eureka Dashboard for service registration

---

## 📝 Version Information

- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Java**: 17+
- **Maven**: 3.8+
- **PostgreSQL**: 12+ (optional, H2 available)

---

**Last Updated:** 2026-06-12  
**Script Version:** 1.0  
**Maintained By:** Slingshot AI Agent
