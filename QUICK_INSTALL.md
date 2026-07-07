# Quick Install Commands - Civic Test USCIS

## ⚡ Quick Install on Connected Device/Emulator

### One-Line Install Command
```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" install -r App\build\outputs\apk\release\app-release.apk
```

### Fresh Install (uninstall first)
```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" uninstall com.joshy.civictestuscis; & "$env:ANDROID_HOME\platform-tools\adb.exe" install App\build\outputs\apk\release\app-release.apk
```

### Install and Launch
```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" install -r App\build\outputs\apk\release\app-release.apk; & "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.joshy.civictestuscis/.MainActivity
```

---

## 📱 Installing on Physical Phone (No Computer Needed)

1. **Transfer APK to phone** → Email/Cloud/USB
2. **Enable installation** → Settings > Security > Install unknown apps
3. **Open APK file** → Tap Install

**APK Location**: `App\build\outputs\apk\release\app-release.apk`

---

## 🔧 Common Commands

```powershell
# Check connected devices
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices

# Install release APK
& "$env:ANDROID_HOME\platform-tools\adb.exe" install App\build\outputs\apk\release\app-release.apk

# Reinstall (keeps data)
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r App\build\outputs\apk\release\app-release.apk

# Uninstall
& "$env:ANDROID_HOME\platform-tools\adb.exe" uninstall com.joshy.civictestuscis

# Launch app
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.joshy.civictestuscis/.MainActivity

# View logs (real-time)
& "$env:ANDROID_HOME\platform-tools\adb.exe" logcat | Select-String "CivicTest"

# Transfer APK to phone's Downloads
& "$env:ANDROID_HOME\platform-tools\adb.exe" push App\build\outputs\apk\release\app-release.apk /sdcard/Download/
```

---

## 🛠️ Build + Install Shortcuts

```powershell
# Build and install debug version
.\gradlew.bat :App:installDebug

# Build and install release version
.\gradlew.bat :App:installRelease

# Build, install, and run
.\gradlew.bat :App:installRelease; $env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.joshy.civictestuscis/.MainActivity
```

---

## 📋 App Information

- **Package Name**: `com.joshy.civictestuscis`
- **Version**: 1.0
- **Min Android**: 8.0 (API 26)
- **Target Android**: 14 (API 34)
- **APK Size**: 4.7 MB (signed, minified)

---

## 🚨 Troubleshooting

| Error | Solution |
|-------|----------|
| `adb not found` | Use full path: `C:\Users\<You>\AppData\Local\Android\Sdk\platform-tools\adb.exe` |
| `No devices found` | Enable USB debugging / Start emulator |
| `Signature mismatch` | Uninstall existing app first |
| `Installation blocked` | Settings > Security > Install unknown apps |

---

**For detailed instructions, see**: `INSTALLATION_GUIDE.md`
