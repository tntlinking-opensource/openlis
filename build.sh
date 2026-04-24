#!/bin/bash
echo "========================================"
echo "LIS System - Source Build Script"
echo "========================================"
echo ""

echo "[1/4] Downloading Maven dependencies..."
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -f pom.xml
if [ $? -ne 0 ]; then
    echo "FAILED: Maven dependency download failed"
    exit 1
fi

echo ""
echo "[2/4] Compiling project..."
mvn clean compile -f pom.xml
if [ $? -ne 0 ]; then
    echo "FAILED: Compilation failed"
    exit 1
fi

echo ""
echo "[3/4] Packaging..."
mvn package -DskipTests -f pom.xml
if [ $? -ne 0 ]; then
    echo "FAILED: Packaging failed"
    exit 1
fi

echo ""
echo "========================================"
echo "Build SUCCESS!"
echo "JAR file: target/lis-open-java-1.0.0.jar"
echo ""
echo "Next steps:"
echo "1. Configure database in src/main/resources/application.yml"
echo "2. Run: java -jar target/lis-open-java-1.0.0.jar"
echo "========================================"
