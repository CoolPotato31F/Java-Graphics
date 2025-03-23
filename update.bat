@echo off
setlocal enabledelayedexpansion

:: Get the batch file name
for %%I in ("%~f0") do set BatchFileName=%%~nxI

:: Delete all files except this batch script
for %%F in (*) do (
    if not "%%F"=="%BatchFileName%" del /f /q "%%F"
)

:: Delete all folders
for /d %%D in (*) do rd /s /q "%%D"

:: Download the GitHub repository zip
echo Downloading repository...
curl -L -o repo.zip https://github.com/CoolPotato31F/Java-Graphics/archive/refs/heads/main.zip

:: Extract the repository
echo Extracting repository...
powershell -Command "Expand-Archive -Path repo.zip -DestinationPath . -Force"

:: Move extracted files to the main directory
for /d %%D in ("Java-Graphics-main") do (
    move "%%D\*" .
    rd /s /q "%%D"
)

:: Delete the zip file
del repo.zip

echo Done!
pause