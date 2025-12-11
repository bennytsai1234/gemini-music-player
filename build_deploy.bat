@echo off
echo Starting Debug Build...
call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo Debug Build Failed!
    exit /b %ERRORLEVEL%
)

echo.
echo Debug Build Success. Committing changes...
git add .
git commit -m "feat: optimize search, integrate equalizer, and update docs"
git push

echo.
echo Starting Release Build...
call gradlew.bat assembleRelease
if %ERRORLEVEL% NEQ 0 (
    echo Release Build Failed!
    exit /b %ERRORLEVEL%
)

echo.
echo All tasks completed successfully!
