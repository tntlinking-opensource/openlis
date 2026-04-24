@echo off
chcp 65001 >nul
echo ========================================
echo LIS System - Source Build Script
echo ========================================
echo.

echo [1/4] Downloading Maven dependencies...
call mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -f pom.xml
if errorlevel 1 (
    echo FAILED: Maven dependency download failed
    pause
    exit /b 1
)

echo.
echo [2/4] Compiling project...
call mvn clean compile -f pom.xml
if errorlevel 1 (
    echo FAILED: Compilation failed
    pause
    exit /b 1
)

echo.
echo [3/4] Packaging...
call mvn package -DskipTests -f pom.xml
if errorlevel 1 (
    echo FAILED: Packaging failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build SUCCESS!
echo JAR file: target\lis-open-java-1.0.0.jar
echo.
echo Next steps:
echo 1. Configure database in src/main/resources/application.yml
echo 2. Run: java -jar target\lis-open-java-1.0.0.jar
echo ========================================
pause
