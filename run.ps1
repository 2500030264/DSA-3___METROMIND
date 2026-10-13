Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "Starting MetroMind Intelligent Public Transport Platform" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (!(Test-Path out\Main.class)) {
    Write-Host "Binaries not found. Running build.ps1 first..." -ForegroundColor Yellow
    .\build.ps1
}

# Run with java (or JDK 17 java if path configured)
java -cp out Main
