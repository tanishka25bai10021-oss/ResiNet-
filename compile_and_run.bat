@echo off
TITLE ResiNet - Smart Microgrid Energy Dispatcher (CSE2006)
echo =========================================================================
echo       BUILDING & LAUNCHING RESINET SMART MICROGRID PLATFORM              
echo =========================================================================

if not exist "bin" mkdir bin
if not exist "logs" mkdir logs
if not exist "data" mkdir data

echo [1/2] Compiling Java Source Files...
javac -cp "lib/*;src" -d bin src\com\smartgrid\resinet\interfaces\*.java src\com\smartgrid\resinet\exceptions\*.java src\com\smartgrid\resinet\model\*.java src\com\smartgrid\resinet\db\*.java src\com\smartgrid\resinet\engine\*.java src\com\smartgrid\resinet\util\*.java src\com\smartgrid\resinet\test\*.java src\com\smartgrid\resinet\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Check error messages above.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching ResiNet Interactive Dashboard...
java -cp "bin;lib/*" com.smartgrid.resinet.Main
pause
