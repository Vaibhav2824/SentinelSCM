@echo off
echo Java version:
java -version
echo.
echo Compiling...
call mvnw.cmd clean compile
if %ERRORLEVEL% NEQ 0 (
    echo BUILD FAILED
    pause
    exit /b 1
)
echo BUILD SUCCESS
pause
