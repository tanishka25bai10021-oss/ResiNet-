#!/usr/bin/env bash
echo "========================================================================="
echo "       BUILDING & LAUNCHING RESINET SMART MICROGRID PLATFORM              "
echo "========================================================================="

mkdir -p bin logs data

echo "[1/2] Compiling Java Source Files..."
javac -cp "lib/*:src" -d bin $(find src -name "*.java")

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed!"
    exit 1
fi

echo "[2/2] Launching ResiNet Interactive Dashboard..."
java -cp "bin:lib/*" com.smartgrid.resinet.Main
