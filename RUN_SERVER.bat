@echo off
title LFP Server
color 0A
echo.
echo ========================================
echo   Laptop Friendly Places - SERVER
echo ========================================
echo.
echo Starting server...
echo.
echo Make sure PostgreSQL is running!
echo.
pause
echo.
echo Starting RMI Server...
echo.

mvn exec:java "-Dexec.mainClass=com.lfp.server.LFPServer"

pause
