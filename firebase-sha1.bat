@echo off
setlocal
rem Mostra a impressao digital (SHA-1) da chave que assina o app neste PC.
set "KS=%USERPROFILE%\.android\debug.keystore"
set "KT="
for %%P in (
  "%ProgramFiles%\Android\Android Studio\jbr\bin\keytool.exe"
  "%LOCALAPPDATA%\Programs\Android Studio\jbr\bin\keytool.exe"
  "%LOCALAPPDATA%\Programs\Android Studio\jre\bin\keytool.exe"
  "%ProgramFiles%\Android\Android Studio\jre\bin\keytool.exe"
  "%ProgramFiles(x86)%\Android\Android Studio\jbr\bin\keytool.exe"
) do if not defined KT if exist "%%~P" set "KT=%%~P"
if not defined KT if defined JAVA_HOME if exist "%JAVA_HOME%\bin\keytool.exe" set "KT=%JAVA_HOME%\bin\keytool.exe"
if not defined KT for /f "delims=" %%K in ('where keytool 2^>nul') do if not defined KT set "KT=%%K"

echo.
echo Chave procurada em: %KS%
if not exist "%KS%" (
  echo [ERRO] A chave nao existe nesse lugar. Gere o APK uma vez no Android Studio e rode de novo,
  echo        ou use o metodo pelo Android Studio descrito no CONFIGURAR-FIREBASE.md.
  goto fim
)
if not defined KT (
  echo [ERRO] Nao achei o programa keytool.
  echo        Use o metodo pelo Android Studio descrito no CONFIGURAR-FIREBASE.md.
  goto fim
)
echo Usando: %KT%
echo ------------------------------------------------------------
"%KT%" -list -v -keystore "%KS%" -alias androiddebugkey -storepass android
echo ------------------------------------------------------------
echo.
echo Procure acima a linha que comeca com "SHA1:" e copie o codigo depois dela
echo (formato AB:CD:12:...  com 20 pares separados por dois-pontos).

:fim
echo.
pause
