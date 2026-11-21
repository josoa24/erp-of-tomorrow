@echo off
setlocal
rem ===========================================================================
rem  Demarre tout l'ERP en local (hors Docker) + l'assistant IA, puis le teste.
rem  A placer a la racine du depot erp-of-tomorrow et lancer : start-erp.bat
rem  Options : start-erp.bat noseed   (ne pas recreer les donnees de demo)
rem            start-erp.bat notest   (ne pas lancer le test de bout en bout)
rem  Prerequis : bases PostgreSQL deja demarrees, Java 17, Maven, Python 3.10+,
rem              fichier services\ai-service\.env avec GEMINI_API_KEY.
rem ===========================================================================
set ROOT=%~dp0
cd /d "%ROOT%"

rem ---- 1. Verifications --------------------------------------------------------
if not exist "services\ai-service\.env" (
    echo [ERREUR] services\ai-service\.env manquant.
    echo          Copier services\ai-service\.env.example en .env et renseigner GEMINI_API_KEY.
    exit /b 1
)
for /f "tokens=5" %%p in ('netstat -ano ^| findstr /r /c:":8080 .*LISTENING"') do (
    echo [ERREUR] Le port 8080 est deja utilise par le processus %%p.
    echo          Arretez-le ^(taskkill /PID %%p /F^) puis relancez ce script.
    exit /b 1
)
if exist "gateway\src\main\java\com\hackaton\gateway\security\RoleAccessPolicyTest.java" (
    echo [INFO] Deplacement de RoleAccessPolicyTest.java vers src\test\java...
    move /y "gateway\src\main\java\com\hackaton\gateway\security\RoleAccessPolicyTest.java" ^
             "gateway\src\test\java\com\hackaton\gateway\security\RoleAccessPolicyTest.java" >nul
)

rem ---- Python : "python" ou, a defaut, le lanceur "py" de Windows
set PY=python
where python >nul 2>&1 || set PY=py -3
%PY% --version >nul 2>&1 || (
    echo [ERREUR] Python introuvable. Installer Python 3.10+ depuis python.org en cochant "Add to PATH".
    exit /b 1
)

rem ---- 2. Environnement Python de l'assistant (premiere fois seulement) --------
if not exist "services\ai-service\venv\Scripts\activate.bat" (
    echo [INFO] Creation de l'environnement Python de l'assistant...
    pushd services\ai-service
    %PY% -m venv venv || (echo [ERREUR] Creation du venv impossible & exit /b 1)
    call venv\Scripts\activate.bat
    pip install -q -r requirements-dev.txt || (echo [ERREUR] Installation des dependances & exit /b 1)
    popd
)

rem ---- 3. Services Java (une fenetre chacun) ------------------------------------
echo [INFO] Demarrage des services Java...
start "auth-service :8084"  /d "%ROOT%services\auth-service"  cmd /k mvn -q spring-boot:run
start "stock-service :8083" /d "%ROOT%services\stock-service" cmd /k mvn -q spring-boot:run
start "vente-service :8082" /d "%ROOT%services\vente-service" cmd /k mvn -q spring-boot:run
start "rh-service :8081"    /d "%ROOT%services\rh-service"    cmd /k mvn -q spring-boot:run
start "crm-service :8085"   /d "%ROOT%services\crm-service"   cmd /k mvn -q spring-boot:run
start "gateway :8080"       /d "%ROOT%gateway"                cmd /k mvn -q spring-boot:run

rem ---- 4. Assistant IA ------------------------------------------------------------
start "ai-service :8086" /d "%ROOT%services\ai-service" cmd /k "call venv\Scripts\activate.bat && uvicorn app.main:app --port 8086"

rem ---- 5. Attente : gateway + auth pretes (login qui repond) ----------------------
echo [INFO] Attente des services (peut prendre 1 a 3 minutes au premier lancement)...
set /a TRIES=0
:wait
timeout /t 5 /nobreak >nul
set /a TRIES+=1
curl -s -o nul -w "%%{http_code}" -X POST http://localhost:8080/api/auth/login ^
     -H "Content-Type: application/json" -d "{\"email\":\"x@x.x\",\"password\":\"x\"}" > "%TEMP%\erp_code.txt" 2>nul
set /p CODE=<"%TEMP%\erp_code.txt"
if "%CODE%"=="401" goto ready
if "%CODE%"=="400" goto ready
if %TRIES% GEQ 60 (
    echo [ERREUR] La gateway ou l'auth-service ne repond pas apres 5 minutes. Voir leurs fenetres.
    exit /b 1
)
echo         ... pas encore pret (essai %TRIES%, code %CODE%)
goto wait

:ready
rem Laisse le temps a stock et vente de finir de demarrer
timeout /t 15 /nobreak >nul
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/ai/ui > "%TEMP%\erp_code.txt"
set /p CODE=<"%TEMP%\erp_code.txt"
if not "%CODE%"=="200" (
    echo [AVERTISSEMENT] /api/ai/ui renvoie %CODE% : verifier la fenetre ai-service et la route /api/ai de la gateway.
)
echo [OK] ERP demarre.

rem ---- 6. Donnees de demo et test --------------------------------------------------
pushd services\ai-service
call venv\Scripts\activate.bat
if /i not "%1"=="noseed" (
    echo.
    python scripts\seed_demo.py
)
if /i not "%1"=="notest" if /i not "%2"=="notest" (
    echo.
    python scripts\test_erp_agent.py
)
popd

echo.
echo ============================================================
echo  Page de test : http://localhost:8080/api/ai/ui
echo  Compte admin : admin@erp.local / admin1234
echo  Comptes demo : stock@erp.local et vente@erp.local / demo12345
echo  Swagger      : http://localhost:8080/swagger-ui.html
echo  Pour tout arreter : stop-erp.bat
echo ============================================================
start "" http://localhost:8080/api/ai/ui
endlocal
