@echo off
rem Arrete tout ce que start-erp.bat a lance (ports 8080 a 8086). Les bases ne sont pas touchees.
for %%p in (8080 8081 8082 8083 8084 8085 8086) do (
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr /r /c:":%%p .*LISTENING"') do (
        echo Arret du port %%p ^(PID %%i^)
        taskkill /PID %%i /F >nul 2>&1
    )
)
for %%t in ("auth-service :8084" "stock-service :8083" "vente-service :8082" "rh-service :8081" "crm-service :8085" "gateway :8080" "ai-service :8086") do (
    taskkill /FI "WINDOWTITLE eq %%~t*" /T /F >nul 2>&1
)
echo Termine.
