# Simple auto start script - just check ports

Write-Host "Starting services..." -ForegroundColor Cyan

# Kill existing processes on ports
Write-Host "Cleaning ports..." -ForegroundColor Yellow
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
Start-Sleep -Seconds 2

# Start backend
Write-Host "Starting backend..." -ForegroundColor Green
$backendPath = Join-Path $PSScriptRoot "backend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendPath'; mvn spring-boot:run" -WindowStyle Minimized

# Start frontend  
Write-Host "Starting frontend..." -ForegroundColor Green
$frontendPath = Join-Path $PSScriptRoot "frontend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; npm run dev" -WindowStyle Minimized

Write-Host ""
Write-Host "Services starting in background windows..." -ForegroundColor Yellow
Write-Host "Please wait 30-60 seconds for services to start" -ForegroundColor Yellow
Write-Host ""
Write-Host "Then visit: http://localhost:3000" -ForegroundColor Cyan
Write-Host ""

# Simple port check (non-blocking)
Write-Host "Checking ports (will continue checking in background)..." -ForegroundColor Gray
$checkCount = 0
while ($checkCount -lt 30) {
    Start-Sleep -Seconds 2
    $checkCount++
    
    $backendPort = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    $frontendPort = Get-NetTCPConnection -LocalPort 3000 -State Listen -ErrorAction SilentlyContinue
    
    if ($backendPort -and $frontendPort) {
        Write-Host ""
        Write-Host "SUCCESS! Both services are ready!" -ForegroundColor Green
        Write-Host "Backend: http://localhost:8080/api" -ForegroundColor Green
        Write-Host "Frontend: http://localhost:3000" -ForegroundColor Green
        Write-Host ""
        Write-Host "Please test login at http://localhost:3000" -ForegroundColor Cyan
        break
    }
    
    if ($checkCount % 5 -eq 0) {
        $status = ""
        if ($backendPort) { $status += "[Backend OK] " } else { $status += "[Backend...] " }
        if ($frontendPort) { $status += "[Frontend OK]" } else { $status += "[Frontend...]" }
        Write-Host "Status: $status ($($checkCount*2)s)" -ForegroundColor Gray
    }
}

if ($checkCount -ge 30) {
    Write-Host ""
    Write-Host "Services may still be starting. Check the minimized windows for status." -ForegroundColor Yellow
    Write-Host "Visit http://localhost:3000 when ready." -ForegroundColor Yellow
}
