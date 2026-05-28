@echo off
echo ========================================
echo   LFP Console Test Client
echo ========================================
echo.
echo Starting Console Client...
echo.
echo Make sure the SERVER is running first!
echo.
pause

mvn exec:java "-Dexec.mainClass=com.lfp.client.LFPClient"
