# View Android Logcat
# Run this with: .\view-logs.ps1

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

Write-Host "📱 Viewing Android Logcat (filtered for your app)" -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop" -ForegroundColor Yellow
Write-Host ""

& $adb logcat -s "CivicTestUSCIS:*" "AndroidRuntime:E"
