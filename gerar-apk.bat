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
if not defined JAVA_HOME for %%P in (
  "%ProgramFiles%\Android\Android Studio\jbr"
  "%LOCALAPPDATA%\Programs\Android Studio\jbr"
  "%ProgramFiles(x86)%\Android\Android Studio\jbr"
) do if not defined JAVA_HOME if exist "%%~P\bin\java.exe" set "JAVA_HOME=%%~P"
call gradlew.bat assembleRelease
echo.
echo APK gerado na pasta APK
explorer APK
pause
