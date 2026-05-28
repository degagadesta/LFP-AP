@echo off
title LFP Client
color 0B
echo.
echo ========================================
echo   Laptop Friendly Places - CLIENT
echo ========================================
echo.
echo Starting JavaFX Client...
echo.
echo Make sure the SERVER is running first!
echo.
pause
echo.
echo Launching application...
echo.

mvn exec:java "-Dexec.mainClass=com.lfp.client.LFPClientApp"

pause
