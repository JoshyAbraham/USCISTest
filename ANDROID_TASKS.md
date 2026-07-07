# Android Development Tasks - VS Code

## Quick Start

Press **Ctrl+Shift+P** (or **Cmd+Shift+P** on Mac) and type "Run Task" to see all available tasks.

## Available Tasks

### 🚀 **Most Used Tasks**

1. **Android: Build and Install**
   - Builds the APK and installs it to connected device/emulator
   - Use this after making code changes

2. **Android: Launch Emulator and Install App**
   - Starts the emulator, builds, and installs the app
   - Complete workflow in one task

3. **Android: View Logcat (Filtered)**
   - Shows app logs and errors only
   - Best for debugging your app

### 📦 **Build Tasks**

- **Android: Build Debug APK** (Default: Ctrl+Shift+B)
  - Compiles the project and creates APK
  - Output: `App/build/outputs/apk/debug/app-debug.apk`

- **Android: Clean Build**
  - Removes all build artifacts
  - Run this if you encounter build issues

- **Android: Run Unit Tests** (Default test task)
  - Runs all unit tests in the project

### 📱 **Device/Emulator Tasks**

- **Android: List Connected Devices**
  - Shows all connected devices and emulators
  - Run this to verify your device is connected

- **Android: List Available AVDs**
  - Shows virtual devices you can launch
  - Lists all configured Android Virtual Devices

- **Android: Launch Emulator**
  - Starts the Android emulator
  - ⚠️ **Important:** Edit `.vscode/tasks.json` and change `Pixel_6_API_34` to your AVD name

- **Android: Install to Device/Emulator**
  - Installs the already-built APK
  - Device must be connected first

- **Android: Uninstall App**
  - Removes the app from device/emulator
  - Useful for clean reinstalls

### 🐛 **Debugging Tasks**

- **Android: View Logcat (Filtered)**
  - Shows only your app logs and errors
  - Best for focused debugging

- **Android: View Logcat (All)**
  - Shows all system logs
  - Useful for low-level debugging

- **Android: Clear Logcat**
  - Clears the log buffer
  - Run before testing to see only new logs

## Setup Instructions

### 1. Configure Your AVD Name

If you want to use the "Launch Emulator" task:

1. Open Android Studio → **Tools → Device Manager**
2. Note the name of your virtual device (e.g., `Pixel_6_API_34`)
3. Open `.vscode/tasks.json`
4. Find the task `"Android: Launch Emulator"`
5. Change the AVD name in the `args` section:
   ```json
   "args": [
	   "-avd",
	   "YOUR_AVD_NAME_HERE"
   ]
   ```

### 2. Create an AVD (if you don't have one)

1. Open **Android Studio**
2. Go to **Tools → Device Manager**
3. Click **"Create Device"**
4. Select **Pixel 6** → **Next**
5. Download **API 34 (Android 14)** → **Next**
6. Name it (e.g., `Pixel_6_API_34`) → **Finish**

### 3. Quick Test

To verify everything works:

1. Press **Ctrl+Shift+P**
2. Type "Run Task"
3. Select **"Android: List Available AVDs"**
4. You should see your AVD name listed

## Recommended Workflow

### First Time Setup
```
1. Create AVD in Android Studio
2. Update AVD name in tasks.json
3. Run: "Android: Launch Emulator"
4. Run: "Android: Build and Install"
```

### Daily Development
```
1. Start emulator (manually or via task)
2. Make code changes
3. Run: "Android: Build and Install"
4. Run: "Android: View Logcat (Filtered)" (if debugging)
```

### Testing
```
1. Run: "Android: Run Unit Tests"
2. Check output in terminal
```

### Clean Build (if issues)
```
1. Run: "Android: Clean Build"
2. Run: "Android: Build Debug APK"
```

## Keyboard Shortcuts

- **Build:** `Ctrl+Shift+B` → Builds the debug APK
- **Run Task:** `Ctrl+Shift+P` → Type "Run Task"
- **Test:** `Ctrl+Shift+P` → Type "Test Task" → Select "Android: Run Unit Tests"

## Troubleshooting

### "AVD not found" error
- Open Android Studio and verify the AVD name
- Update the name in `.vscode/tasks.json`

### "No devices found" error
- Run: "Android: List Connected Devices"
- Make sure emulator is running or device is connected
- Try: `adb devices` in terminal

### Build fails
- Run: "Android: Clean Build"
- Check that `local.properties` exists with SDK path
- Verify Android SDK is properly installed

### Emulator won't start
- Open Android Studio → Tools → Device Manager
- Start emulator from there first
- Once running, use VS Code tasks to install

## Additional Commands

If you prefer terminal commands:

```powershell
# Build
.\gradlew.bat assembleDebug

# Install
.\gradlew.bat installDebug

# Uninstall
adb uninstall com.joshy.civictestuscis

# Launch specific emulator
$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe -avd Pixel_6_API_34

# View logs
adb logcat

# List devices
adb devices -l

# List AVDs
$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe -list-avds
```

## Next Steps

1. **Create an AVD** in Android Studio if you haven't already
2. **Update the AVD name** in tasks.json
3. **Test the workflow** by running "Android: Launch Emulator and Install App"
4. **Start coding!** Use "Android: Build and Install" after each change

Happy coding! 🎉
