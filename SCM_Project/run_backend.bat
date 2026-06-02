@echo off
title SentinelSCM - Backend
color 0B

echo.
echo  ====================================================
echo    SentinelSCM - Supply Chain Risk Management
echo    Starting on http://localhost:8080
echo  ====================================================
echo.

echo [INFO] Working directory: %CD%
echo [INFO] Java version:
java -version
echo.
echo [INFO] Building project...
call mvnw.cmd package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed. Make sure Java 17+ is installed and in PATH.
    pause
    exit /b 1
)

echo [INFO] Starting server...
echo.
java -jar "target\supply-chain-risk-1.0.0.jar"

pause
