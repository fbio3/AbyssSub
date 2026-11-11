@echo off
cd /d "%~dp0"
if not exist local.properties (
  if exist "%LOCALAPPDATA%\Android\Sdk" (
    echo sdk.dir=%LOCALAPPDATA:\=\\%\\Android\\Sdk> local.properties
  ) else (
    echo Android SDK nao encontrado. Instale o Android Studio primeiro.
    pause & exit /b 1
  )
)
if "%JAVA_HOME%"=="" if exist "%ProgramFiles%\Android\Android Studio\jbr" set "JAVA_HOME=%ProgramFiles%\Android\Android Studio\jbr"
call gradlew.bat assembleRelease
echo.
echo APK gerado na pasta APK
explorer APK
pause
