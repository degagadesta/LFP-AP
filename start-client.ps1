Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Laptop Friendly Places - Client" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Starting JavaFX Client Application..." -ForegroundColor Green
Write-Host ""
Write-Host "Make sure the SERVER is running first!" -ForegroundColor Yellow
Write-Host ""
Read-Host "Press Enter to continue"

mvn exec:java "-Dexec.mainClass=com.lfp.client.LFPClientApp"
