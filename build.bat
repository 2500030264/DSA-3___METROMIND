@echo off
echo ========================================================
echo Compiling MetroMind Intelligent Public Transport Platform
echo ========================================================
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Compilation complete. All classes compiled into out/ directory.
) else (
    echo [ERROR] Compilation failed.
)
del sources.txt
