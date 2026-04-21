@echo off
set JAVA_HOME=C:\Users\Vaibhav\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.5.11-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
echo Java version:
java -version
echo.
echo Compiling...
mvnw.cmd clean compile
if %ERRORLEVEL% NEQ 0 (
    echo BUILD FAILED
    pause
    exit /b 1
)
echo BUILD SUCCESS
pause
