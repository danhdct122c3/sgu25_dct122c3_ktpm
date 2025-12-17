# Automated API Testing Script
# Usage: .\run-api-tests.ps1

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "   Automated API Testing Suite   " -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Check if Newman is installed
Write-Host "[1/5] Checking Newman installation..." -ForegroundColor Yellow
$newmanInstalled = Get-Command newman -ErrorAction SilentlyContinue
if (-not $newmanInstalled) {
    Write-Host "Newman not found. Installing..." -ForegroundColor Red
    npm install -g newman newman-reporter-htmlextra
}
Write-Host "✓ Newman is ready" -ForegroundColor Green
Write-Host ""

# Check if backend is running
Write-Host "[2/5] Checking backend server..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/actuator/health" -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✓ Backend is running" -ForegroundColor Green
} catch {
    Write-Host "✗ Backend not running. Starting backend..." -ForegroundColor Red
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd back-end; .\mvnw.cmd spring-boot:run"
    Write-Host "Waiting 60 seconds for backend to start..." -ForegroundColor Yellow
    Start-Sleep -Seconds 60
}
Write-Host ""

# Run Newman tests
Write-Host "[3/5] Running Postman collection tests..." -ForegroundColor Yellow
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$reportFile = "test-reports/newman-report-$timestamp.html"

newman run postman_collection.json `
    -e postman_environment_local.json `
    -r cli,htmlextra `
    --reporter-htmlextra-export $reportFile `
    --timeout-request 10000 `
    --bail

$exitCode = $LASTEXITCODE
Write-Host ""

# Run Spring Boot tests
Write-Host "[4/5] Running Spring Boot integration tests..." -ForegroundColor Yellow
cd back-end
.\mvnw.cmd test -Dtest=*ControllerTest
cd ..
Write-Host ""

# Generate summary
Write-Host "[5/5] Generating test summary..." -ForegroundColor Yellow
Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "       Test Results Summary       " -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

if ($exitCode -eq 0) {
    Write-Host "✓ ALL TESTS PASSED" -ForegroundColor Green
} else {
    Write-Host "✗ SOME TESTS FAILED" -ForegroundColor Red
}

Write-Host ""
Write-Host "Reports generated:" -ForegroundColor Cyan
Write-Host "  - Newman Report: $reportFile" -ForegroundColor White
Write-Host "  - JUnit Report: back-end/target/surefire-reports/" -ForegroundColor White
Write-Host ""

# Open report in browser
$openReport = Read-Host "Open HTML report? (y/n)"
if ($openReport -eq "y") {
    Start-Process $reportFile
}

Write-Host ""
Write-Host "Testing completed!" -ForegroundColor Cyan
exit $exitCode
