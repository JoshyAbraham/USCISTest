# 🚀 Quick Start - Run Your App Now!

## ✅ What's Already Done

- ✓ Android SDK installed
- ✓ Build tools installed  
- ✓ Emulator installed
- ✓ VS Code tasks configured
- ✓ APK built successfully (11.28 MB)

## ⚠️ What You Need to Do

**You need to create one Android Virtual Device (AVD) to run the app.**

---

## 📱 Create AVD - 3 Minute Setup

### Open Android Studio → Device Manager

1. **Launch Android Studio**

2. **Click "More Actions"** → **"Virtual Device Manager"**
   (Or if project open: **Tools** → **Device Manager**)

3. **Click "Create Device"**

4. **Select "Pixel 6"** → **Next**

5. **Download API 34** (Android 14):
   - Click the download button next to "UpsideDownCake API 34"
   - Wait for download (~800 MB)
   - Click **Next**

6. **Name it:** `Pixel_6_API_34`

7. **Click Finish**

8. **Click the ▶ Play button** to launch it

**First boot takes 1-2 minutes - be patient!**

---

## 🎯 Run Your App (Choose One Method)

### Method 1: VS Code Tasks (Recommended)

**Press** `Ctrl+Shift+P`

**Type:** `Run Task`

**Select:** `Android: Build and Install`

### Method 2: Terminal Command

```powershell
.\gradlew.bat installDebug
```

### Method 3: Direct ADB Install

```powershell
adb install App\build\outputs\apk\debug\app-debug.apk
```

---

## 🐛 Debug Your App

### View Logs

**In VS Code:**
- `Ctrl+Shift+P` → `Run Task` → `Android: View Logcat (Filtered)`

**In Terminal:**
```powershell
adb logcat -s CivicTestUSCIS:* AndroidRuntime:E
```

---

## 📋 Complete Task List

Press `Ctrl+Shift+P` → Type "Run Task" to see all:

**Build & Deploy:**
- Android: Build Debug APK
- Android: Install to Device/Emulator  
- Android: Build and Install
- Android: Launch Emulator and Install App

**Device Management:**
- Android: Launch Emulator
- Android: List Connected Devices
- Android: List Available AVDs

**Debugging:**
- Android: View Logcat (Filtered)
- Android: View Logcat (All)
- Android: Clear Logcat

**Maintenance:**
- Android: Clean Build
- Android: Run Unit Tests
- Android: Uninstall App

---

## 🎓 Daily Workflow

1. **Start Emulator:**
   - From Android Studio Device Manager, or
   - VS Code: `Android: Launch Emulator`

2. **Make Code Changes**

3. **Deploy:**
   - VS Code: `Android: Build and Install`
   - Or terminal: `.\gradlew.bat installDebug`

4. **Debug (if needed):**
   - VS Code: `Android: View Logcat (Filtered)`

---

## 🔧 Troubleshooting

### "No devices found"
```powershell
adb devices
```
Make sure emulator is running

### "Build failed"
```powershell
.\gradlew.bat clean
.\gradlew.bat assembleDebug
```

### Emulator won't start
- Use Android Studio Device Manager
- Try "Cold Boot Now" option

### Want to use different AVD name?
Edit `.vscode/tasks.json` → Find `"Android: Launch Emulator"` → Change AVD name

---

## 📚 More Help

- **Full Task Guide:** [ANDROID_TASKS.md](ANDROID_TASKS.md)
- **AVD Setup Details:** [SETUP_AVD.md](SETUP_AVD.md)
- **Project Info:** [README.md](README.md)

---

## ⏭️ Next Steps

1. **Create AVD** in Android Studio (3 min)
2. **Launch emulator** (Play button)
3. **In VS Code:** `Ctrl+Shift+P` → `Run Task` → `Android: Build and Install`
4. **See your app running!** 🎉

The app will appear on the emulator home screen as **"Civic Test USCIS"**.
