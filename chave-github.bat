@echo off
rem Copia a chave de assinatura deste PC (em texto base64) para a area de transferencia,
rem para colar no segredo ABYSS_DEBUG_KEYSTORE do GitHub.
powershell -NoProfile -Command "[Convert]::ToBase64String([IO.File]::ReadAllBytes(\"$env:USERPROFILE\.android\debug.keystore\")) | Set-Clipboard"
if errorlevel 1 (echo Nao foi possivel ler a chave. & pause & exit /b 1)
echo.
echo Pronto! A chave foi copiada. No GitHub: Settings ^> Secrets and variables ^> Actions ^> New repository secret
echo Nome: ABYSS_DEBUG_KEYSTORE    Valor: cole (Ctrl+V)
echo.
pause
