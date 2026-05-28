@echo off
echo ========================================
echo  Starting LFP Server
echo ========================================
echo.
echo Make sure MySQL is running in XAMPP!
echo.
pause

mvn exec:java -Dexec.mainClass=com.lfp.server.LFPServer
