@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist "%~dp0collect-diagnostics.ps1" (
  echo collect-diagnostics.ps1 not found next to this file - put both files in one folder.
  pause
  exit /b 1
)
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0collect-diagnostics.ps1" %*
