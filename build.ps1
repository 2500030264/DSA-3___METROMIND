Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "Compiling MetroMind Intelligent Public Transport Platform" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (!(Test-Path out)) {
    New-Item -ItemType Directory -Path out | Out-Null
}

$sources = Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName
javac -d out $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Compilation successful! Binaries compiled into out/ directory." -ForegroundColor Green
} else {
    Write-Host "[ERROR] Compilation failed with exit code $LASTEXITCODE." -ForegroundColor Red
}
