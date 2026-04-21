@echo off
title SupplyChainOS - Backend
color 0B

set "PROJECT_DIR=C:\Users\Vaibhav\Desktop\OOAD_PROJECT(1)\SCM_Project"
set "JAVA_HOME=C:\Users\Vaibhav\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo  ====================================================
echo    SupplyChainOS - Supply Chain Risk Management
echo    Starting on http://localhost:8080
echo  ====================================================
echo.

cd /d "%PROJECT_DIR%"

echo [INFO] Working directory: %CD%
echo [INFO] Java version:
java -version
echo.
echo [INFO] Starting server...
echo.

mvnw.cmd exec:java -Dexec.mainClass="com.scm.Main"

pause
