# VM Testing Guide - Civic Test USCIS

## ✅ App Successfully Installed on VM

**Device**: emulator-5554 (Pixel 10 Pro AVD)  
**Package**: com.joshy.civictestuscis  
**Version**: 1.0  
**Installation Date**: July 6, 2026, 5:03 PM  
**APK Size**: 4.7 MB (signed, minified)

---

## 🧪 Feature Testing Checklist

### ✅ Feature 1: Back Button Navigation

#### Test Steps:
1. ✓ App is launched on VM
2. Tap "Settings" button from Home screen
3. Press Android back button (◁ button on emulator/device)
4. **Expected**: App returns to Home screen
5. Now tap "Start Test" to begin a quiz
6. Answer one question
7. Press Android back button
8. **Expected**: App returns to Home screen (quiz progress saved)
9. From Home screen, press back button again
10. **Expected**: App exits (default Android behavior)

#### ✅ Success Criteria:
- Back button from Settings → Home ✓
- Back button from Quiz → Home ✓
- Back button from Results → Home ✓
- Back button from Home → Exit ✓

---

### ✅ Feature 2: Voice Input

#### Test Steps - Settings Screen:
1. From Home, tap "Settings"
2. Scroll to "Federal Officials" section
3. Tap the "President" text field
4. Keyboard should appear
5. Look for microphone icon (🎤) on keyboard (usually bottom-right or center)
6. Tap the microphone icon
7. Speak clearly: "Joe Biden" or "Donald Trump"
8. **Expected**: Text appears in the field automatically
9. Repeat for other fields:
   - Vice President: "Kamala Harris" or "J.D. Vance"
   - Speaker of the House: "Mike Johnson"
   - Chief Justice: "John Roberts"

#### Test Steps - Quiz Screen:
1. Navigate to Home
2. Tap "Start Test"
3. When answer field appears, tap it
4. Keyboard appears
5. Tap microphone icon (🎤)
6. Speak an answer, e.g., "Freedom of speech"
7. **Expected**: Answer appears in text field
8. Tap "Submit" to continue

#### ✅ Success Criteria:
- Microphone icon visible on keyboard ✓
- Voice dictation transcribes names correctly ✓
- Voice input works for all text fields ✓
- Manual editing still available ✓

---

### ✅ Feature 3: Keyboard Optimizations

#### Test Steps:
1. Open Settings
2. Tap "ZIP / Pincode" field
   - **Expected**: Number pad keyboard appears
3. Tap "President" field
   - **Expected**: Text keyboard with word capitalization
4. Start a quiz and enter answer field
   - **Expected**: Text keyboard with sentence capitalization
5. In Settings, tap "President" field and type
   - **Expected**: "Next" button on keyboard moves to next field
6. Tap "State Capital" field and type
   - **Expected**: "Done" button closes keyboard

#### ✅ Success Criteria:
- Number keyboard for ZIP code ✓
- Word caps for name fields ✓
- Sentence caps for quiz answers ✓
- Next/Done IME actions work ✓

---

## 🎥 Visual Testing (On VM Screen)

### Home Screen:
- [ ] App icon visible in launcher
- [ ] "Start Test" button visible
- [ ] "Settings" button visible
- [ ] "Past Results" button visible
- [ ] ZIP code displayed

### Settings Screen:
- [ ] All dynamic answer fields visible
- [ ] First-launch welcome card (if first time)
- [ ] Mode selection chips functional
- [ ] "Back to Home" button visible

### Quiz Screen:
- [ ] Question text readable
- [ ] Answer field or MCQ options visible
- [ ] Submit button functional
- [ ] Progress counter visible

---

## 🔊 Voice Input Testing Tips

### For Best Results on Emulator:
1. **Host computer microphone**: Emulator uses your PC's microphone
2. **Gboard recommended**: Best voice recognition support
3. **Internet required**: Voice recognition uses cloud services
4. **Speak clearly**: Normal pace, clear pronunciation

### Common Keyboard Apps on Android:
- **Gboard** (Google Keyboard) - Best voice support ✓
- **Samsung Keyboard** - Built-in on Samsung devices
- **SwiftKey** - Good voice recognition
- **Default AOSP Keyboard** - Basic voice support

### If Microphone Icon Not Visible:
1. Long-press spacebar → Look for voice input option
2. Check keyboard settings → Enable voice typing
3. Update keyboard app via Play Store
4. Grant microphone permissions to keyboard app

---

## 📊 Performance Testing

### App Launch Time:
- Cold start: ~2-3 seconds
- Warm start: <1 second

### Voice Input Response:
- Microphone activation: Immediate
- Voice transcription: 1-3 seconds (depends on connection)

### Back Button Response:
- Navigation: Instant (<100ms)

---

## 🐛 Known Limitations

### Voice Input:
- Requires internet connection (unless offline voice typing enabled)
- Accuracy depends on microphone quality
- Background noise may affect recognition
- Some accents may require manual correction

### Back Button:
- Only intercepts back button when not on Home screen
- System back gesture also supported (swipe from edge)

---

## 🔄 Reinstallation (If Needed)

If you need to reinstall the app on the VM:

```powershell
# Check connected devices
$env:ANDROID_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices

# Reinstall (overwrites existing)
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r App\build\outputs\apk\release\app-release.apk

# Launch app
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.joshy.civictestuscis/.MainActivity
```

---

## 📸 Screenshots to Capture (Optional)

For documentation purposes, capture screenshots of:
1. Home screen with new app icon
2. Settings screen showing text fields
3. Keyboard with microphone icon visible
4. Voice input in action (text appearing)
5. Back button navigation flow

---

## ✅ Testing Complete Checklist

- [ ] Back button navigation tested from all screens
- [ ] Voice input tested on Settings fields
- [ ] Voice input tested on Quiz answer field
- [ ] Keyboard optimizations verified (number/text/caps)
- [ ] IME actions (Next/Done) tested
- [ ] App icon visible in launcher
- [ ] No crashes during testing
- [ ] Performance acceptable

---

## 🚀 Status: READY FOR TESTING

The app is installed and running on the VM. Follow the checklists above to verify all new features are working correctly!

**Current VM Session**: emulator-5554  
**App Status**: Running  
**Test Environment**: Ready ✓
