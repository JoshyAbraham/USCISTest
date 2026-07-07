# Quick Setup Guide - Create Your First Android Virtual Device (AVD)

## You Have 2 Options:

### Option 1: Android Studio GUI (Recommended - Easier)

1. **Open Android Studio**

2. **Open Device Manager:**
   - If no project is open: Click **"More Actions"** → **"Virtual Device Manager"**
   - If project is open: **Tools** → **Device Manager**

3. **Create New Device:**
   - Click **"Create Device"** button
   - Select **"Pixel 6"** (or any device you prefer)
   - Click **"Next"**

4. **Download System Image:**
   - Select **"Tiramisu"** (API Level 33) or **"UpsideDownCake"** (API Level 34)
   - If not downloaded, click the **download icon** next to it
   - Wait for download to complete
   - Click **"Next"**

5. **Finish Setup:**
   - Name it: `Pixel_6_API_34` (or your preferred name)
   - Click **"Finish"**

6. **Launch It:**
   - Click the **▶ Play** button next to your new AVD
   - Wait for emulator to boot (first time takes 1-2 minutes)

---

### Option 2: Command Line (Advanced - Faster)

Run these commands in **PowerShell** (in your workspace):

```powershell
# Set paths
$sdkmanager = "$env:LOCALAPPDATA\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat"
$avdmanager = "$env:LOCALAPPDATA\Android\Sdk\cmdline-tools\latest\bin\avdmanager.bat"

# Download system image (choose one):
& $sdkmanager "system-images;android-34;google_apis_playstore;x86_64"  # API 34 with Play Store
# OR
& $sdkmanager "system-images;android-33;google_apis_playstore;x86_64"  # API 33 with Play Store

# Create AVD
& $avdmanager create avd -n "Pixel_6_API_34" -k "system-images;android-34;google_apis_playstore;x86_64" -d "pixel_6"

# Launch it
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd Pixel_6_API_34
```

---

## After Creating Your AVD

### Test in VS Code (Press Ctrl+Shift+P):

1. **Run Task** → **"Android: List Available AVDs"**
   - Should show: `Pixel_6_API_34`

2. **Run Task** → **"Android: Launch Emulator"**
   - Emulator should start

3. **Run Task** → **"Android: Build and Install"**
   - App should install and appear on emulator

---

## If You Named It Differently

If you used a different AVD name (not `Pixel_6_API_34`):

1. Open `.vscode/tasks.json`
2. Find the task: `"Android: Launch Emulator"`
3. Change this line:
   ```json
   "args": [
	   "-avd",
	   "YOUR_AVD_NAME_HERE"  ← Change this
   ]
   ```

---

## Troubleshooting

**Command not found errors:**
- Option 2 requires cmdline-tools to be installed
- Easier to just use Android Studio (Option 1)

**Emulator is slow:**
- First boot is always slow (1-2 min)
- Make sure hardware acceleration is enabled
- Close other heavy applications

**Black screen:**
- Wait 2-3 minutes for first boot
- Try: Tools → Device Manager → Cold Boot Now

---

## Next Steps After AVD is Running

Once your emulator is running:

```
VS Code: Ctrl+Shift+P → Run Task → "Android: Build and Install"
```

Or in terminal:
```powershell
.\gradlew.bat installDebug
```

Your app will appear on the emulator screen! 🎉
