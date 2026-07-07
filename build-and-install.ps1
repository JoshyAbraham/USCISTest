# Quick Android Build and Install Script
# Run this with: .\build-and-install.ps1

Write-Host "`n🔨 Building Debug APK..." -ForegroundColor Cyan
& ".\gradlew.bat" assembleDebug

if ($LASTEXITCODE -eq 0) {
	Write-Host "✓ Build successful!" -ForegroundColor Green

	Write-Host "`n📱 Installing to device/emulator..." -ForegroundColor Cyan
	& ".\gradlew.bat" installDebug

	if ($LASTEXITCODE -eq 0) {
		Write-Host "`n✓ App installed successfully!" -ForegroundColor Green
		Write-Host "`nCheck your emulator/device for 'Civic Test USCIS' app" -ForegroundColor Yellow
	} else {
		Write-Host "`n✗ Installation failed!" -ForegroundColor Red
		Write-Host "Make sure a device/emulator is running" -ForegroundColor Yellow
		Write-Host "Run: adb devices" -ForegroundColor Yellow
	}
} else {
	Write-Host "`n✗ Build failed!" -ForegroundColor Red
}
