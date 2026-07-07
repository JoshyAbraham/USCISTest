# Navigation & Input Features

## ✅ Features Implemented

### 1. Back Button Navigation (Android Hardware Back Button)

**Behavior**: Pressing the Android back button always navigates to the Home screen, regardless of which screen you're on.

**Implementation Details**:
- Uses `BackHandler` from Androidx Compose
- Intercepts back button presses on all screens except Home
- Provides consistent navigation experience
- Prevents accidental app exit from inner screens

**User Experience**:
- From **Settings** → Back button → **Home**
- From **Quiz** → Back button → **Home**
- From **Results** → Back button → **Home**
- From **Home** → Back button → Exit app (default Android behavior)

---

### 2. Voice Input on Text Fields

**Behavior**: All text input fields now support voice dictation via the keyboard's microphone button.

**Enabled on**:
- ✅ **ZIP Code field** (Settings screen)
- ✅ **President** (Settings screen)
- ✅ **Vice President** (Settings screen)
- ✅ **Speaker of the House** (Settings screen)
- ✅ **Chief Justice** (Settings screen)
- ✅ **U.S. Senators** (Settings screen)
- ✅ **U.S. Representative** (Settings screen)
- ✅ **State Governor** (Settings screen)
- ✅ **State Capital** (Settings screen)
- ✅ **Quiz answer field** (Quiz screen)

**Implementation Details**:
- Uses `KeyboardOptions` with appropriate IME actions and capitalization
- Name fields: `KeyboardCapitalization.Words` + `ImeAction.Next`
- ZIP code: `KeyboardType.Number` + `ImeAction.Done`
- Quiz answers: `KeyboardCapitalization.Sentences` + `ImeAction.Done`

**How to Use Voice Input**:
1. Tap on any text field
2. Keyboard appears with a microphone icon (🎤)
3. Tap the microphone button
4. Speak your answer
5. Voice is transcribed to text automatically

**Note**: Voice input availability depends on:
- Device keyboard app (Gboard, Samsung Keyboard, etc.)
- Internet connection (for cloud-based voice recognition)
- Microphone permissions
- Language settings

---

## Technical Implementation

### Back Button Handler
```kotlin
// In CivicApp.kt
BackHandler(enabled = ui.screen != Screen.Home) {
	viewModel.navigate(Screen.Home)
}
```

### Voice-Enabled Text Fields
```kotlin
// Example: Name field with voice support
OutlinedTextField(
	value = value,
	onValueChange = onValueChange,
	keyboardOptions = KeyboardOptions(
		capitalization = KeyboardCapitalization.Words,
		imeAction = ImeAction.Next
	),
	// ... other properties
)
```

---

## User Guide

### Using the Back Button
- **Quick Home**: Press back from any screen to return home
- **No more multi-step back navigation**: Single press takes you home
- **Exit protection**: Accidentally pressing back won't lose your quiz progress

### Using Voice Input

#### On Settings Screen:
1. Go to Settings
2. Tap any name field (e.g., "President")
3. When keyboard appears, locate microphone icon (usually bottom right or center)
4. Tap microphone and speak clearly: "Donald Trump"
5. Text appears automatically
6. Tap Next field or Done

#### On Quiz Screen:
1. Start a quiz
2. When answer field appears, tap it
3. Tap microphone on keyboard
4. Speak your answer: "Freedom of speech"
5. Tap Submit or self-marking buttons

**Tips for Best Results**:
- Speak clearly and at normal pace
- Use proper names (voice recognition handles "Donald Trump", "Joe Biden", etc.)
- Pause between words for better accuracy
- Edit manually if transcription is incorrect
- Works offline with some keyboards (Gboard offers offline voice typing)

---

## Keyboard Optimization Summary

| Field Type | Keyboard Type | Capitalization | IME Action | Voice Support |
|------------|---------------|----------------|------------|---------------|
| ZIP Code | Number | None | Done | ✅ Yes (numbers) |
| Name Fields | Text | Words | Next | ✅ Yes |
| State Capital | Text | Words | Done | ✅ Yes |
| Quiz Answer | Text | Sentences | Done | ✅ Yes |

---

## Testing

### Test Back Button:
1. Install app
2. Navigate to Settings → Press back → Should go to Home
3. Start Quiz → Press back → Should go to Home
4. View Results → Press back → Should go to Home
5. From Home → Press back → Should exit app

### Test Voice Input:
1. Open Settings
2. Tap "President" field
3. Look for microphone icon on keyboard
4. Tap microphone and say "Joe Biden"
5. Verify text appears correctly
6. Repeat for other fields

**Expected Results**:
- ✅ Back button consistently returns to Home
- ✅ Microphone icon appears on all text fields
- ✅ Voice input transcribes accurately
- ✅ Keyboard shows appropriate type for each field
- ✅ IME actions (Next/Done) work correctly

---

## Troubleshooting

### Voice Input Not Working?

**Issue**: Microphone icon not visible
- **Solution**: Update keyboard app (Gboard, SwiftKey, etc.)
- **Solution**: Check microphone permissions in Android Settings

**Issue**: Voice input produces no text
- **Solution**: Enable internet connection (required for cloud recognition)
- **Solution**: Check language settings match your speech language
- **Solution**: Try Gboard with offline voice typing enabled

**Issue**: Voice recognition inaccurate
- **Solution**: Speak more slowly and clearly
- **Solution**: Reduce background noise
- **Solution**: Switch to a better keyboard app
- **Solution**: Manually edit incorrect transcriptions

### Back Button Not Working?

**Issue**: Back button exits app from inner screens
- **Solution**: Ensure you're using the latest APK build (v1.0 with back handler)
- **Solution**: Reinstall app if behavior persists

---

## Build Information

**Features Added In**: v1.0  
**Build Date**: 2026-07-06  
**APK**: `App/build/outputs/apk/release/app-release.apk`

**Dependencies**:
- `androidx.activity:activity-compose` (for BackHandler)
- `androidx.compose.foundation` (for KeyboardOptions)

**Code Modified**:
- `MainActivity.kt` - Added activity parameter to CivicApp
- `CivicApp.kt` - Added BackHandler, KeyboardOptions to all text fields
