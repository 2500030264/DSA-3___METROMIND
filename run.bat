@echo off
echo ========================================================
echo Starting MetroMind Intelligent Public Transport Platform
echo ========================================================
if not exist out\Main.class (
    echo Binaries not found in out\. Compiling first...
    call build.bat
)
java -cp out Main
