@echo off
title LFP Database Setup
color 0E
echo.
echo ========================================
echo   LFP Database Setup
echo ========================================
echo.
echo This will create test users in the database:
echo.
echo   Admin: admin@lfp.com / admin123
echo   User:  user@lfp.com / user123
echo.
echo Make sure PostgreSQL is running!
echo.
pause
echo.
echo Running database setup...
echo.

mvn exec:java "-Dexec.mainClass=com.lfp.util.DatabaseSetup"

echo.
pause
