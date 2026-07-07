# Installing Civic Test USCIS App on Android Devices

## Method 1: Install on Connected Device/Emulator via ADB

### Prerequisites
- Android SDK installed (with platform-tools)
- Device connected via USB with USB debugging enabled, OR emulator running

### Step 1: Check Connected Devices
```powershell
# Set Android SDK path
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"

# List connected devices
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices
```

You should see your device or emulator listed (e.g., `emulator-5554` or device serial number).

### Step 2: Install the APK

**For first-time installation:**
```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" install App\build\outputs\apk\release\app-release.apk
```

**To update/reinstall (keeps app data):**
```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r App\build\outputs\apk\release\app-release.apk
```

**If you get signature mismatch error:**
```powershell
# Uninstall existing version first
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" uninstall com.joshy.civictestuscis

# Then install the new APK
& "$env:ANDROID_HOME\platform-tools\adb.exe" install App\build\outputs\apk\release\app-release.apk
```

### Step 3: Launch the App
After installation, find "Civic Test USCIS" in your app drawer and launch it.

---

## Method 2: Install via Gradle (Development)

### Install Debug Version on Emulator/Device:
```powershell
.\gradlew.bat :App:installDebug
```

### Install Release Version on Emulator/Device:
```powershell
.\gradlew.bat :App:installRelease
```

---

## Method 3: Install on Physical Device via APK File Transfer

### Step 1: Transfer APK to Device
You can transfer the APK file to your phone using one of these methods:

**Option A: Via USB Cable**
1. Connect your phone to PC via USB
2. Copy `App\build\outputs\apk\release\app-release.apk` to your phone's Downloads folder
3. Disconnect phone

**Option B: Via Email/Cloud/Messaging**
1. Email the APK to yourself, or
2. Upload to Google Drive/Dropbox and download on phone, or
3. Send via WhatsApp/Telegram to yourself

**Option C: Via ADB Push**
```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" push App\build\outputs\apk\release\app-release.apk /sdcard/Download/
```

### Step 2: Enable "Install Unknown Apps" on Your Phone
1. Go to **Settings** > **Security** (or **Privacy**)
2. Enable **Install unknown apps** or **Unknown sources**
3. Select the app you'll use to install (e.g., Files, Chrome, Gmail)
4. Allow installation from that source

### Step 3: Install the APK
1. Open **Files** app or **Downloads** on your phone
2. Locate `app-release.apk`
3. Tap on it
4. Tap **Install**
5. Wait for installation to complete
6. Tap **Open** to launch the app

---

## Method 4: Install Multiple Devices at Once

If you have multiple devices/emulators connected:

```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"

# List all devices
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices

# Install on specific device
& "$env:ANDROID_HOME\platform-tools\adb.exe" -s <device-serial> install App\build\outputs\apk\release\app-release.apk

# Example:
& "$env:ANDROID_HOME\platform-tools\adb.exe" -s emulator-5554 install App\build\outputs\apk\release\app-release.apk
```

---

## Troubleshooting

### Problem: "adb not found"
**Solution**: Android SDK not installed or not in PATH. Use full path:
```powershell
C:\Users\<YourUsername>\AppData\Local\Android\Sdk\platform-tools\adb.exe devices
```

### Problem: "No devices found"
**Solution**:
- For physical device: Enable USB debugging in Developer Options
- For emulator: Start emulator first using Android Studio or emulator command

### Problem: "INSTALL_FAILED_UPDATE_INCOMPATIBLE"
**Solution**: Different signing keys. Uninstall existing app first:
```powershell
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" uninstall com.joshy.civictestuscis
```

### Problem: "Installation blocked" on phone
**Solution**: Enable "Install unknown apps" in Settings > Security for the source app.

### Problem: App crashes on startup
**Solution**: 
- Check minimum Android version (app requires Android 8.0 / API 26+)
- Clear app data: Settings > Apps > Civic Test USCIS > Storage > Clear Data
- Reinstall the app

---

## Enabling USB Debugging on Android Device

1. Go to **Settings** > **About Phone**
2. Tap **Build Number** 7 times (enables Developer Mode)
3. Go back to **Settings** > **System** > **Developer Options**
4. Enable **USB Debugging**
5. Connect device to PC via USB
6. Accept "Allow USB Debugging" prompt on phone

---

## APK Location

**Signed Release APK**: `App\build\outputs\apk\release\app-release.apk`  
**Debug APK**: `App\build\outputs\apk\debug\app-debug.apk`

---

## Quick Reference Commands

```powershell
# Setup (run once)
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"

# Check devices
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices

# Install
& "$env:ANDROID_HOME\platform-tools\adb.exe" install App\build\outputs\apk\release\app-release.apk

# Reinstall (keeps data)
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r App\build\outputs\apk\release\app-release.apk

# Uninstall
& "$env:ANDROID_HOME\platform-tools\adb.exe" uninstall com.joshy.civictestuscis

# Launch app
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.joshy.civictestuscis/.MainActivity
```
