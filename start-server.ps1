Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Starting LFP Server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Make sure MySQL is running in XAMPP!" -ForegroundColor Yellow
Write-Host ""
Read-Host "Press Enter to continue"

mvn exec:java "-Dexec.mainClass=com.lfp.server.LFPServer"
