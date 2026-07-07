# Civic Test USCIS Android App

Android app scaffold for USCIS 2025 civics preparation using all 128 questions.

## 🚀 Quick Start

**New to the project?** → See **[QUICK_START.md](QUICK_START.md)** for step-by-step setup

**VS Code Developer?** → See **[ANDROID_TASKS.md](ANDROID_TASKS.md)** for all available tasks

### Run the App (after AVD setup):

Press **Ctrl+Shift+P** → Type "Run Task" → Select:
- **Android: Build and Install** - Build and deploy to device/emulator
- **Android: View Logcat (Filtered)** - View app logs

## Included Features

- Full 128-question dataset loaded from `app/src/main/assets/questions_2025.json`
- One-question-at-a-time quiz UX
- Modes:
  - Interview simulation: ask up to 20, pass at 12
  - 65/20 simulation: ask up to 10 from starred set, pass at 6
  - Practice MCQ mode for study
- ZIP-based answer enrichment for location/time-sensitive items:
  - your U.S. representative
  - your state's U.S. senators
  - president and vice president
  - governor and state capital
- Local Room history of attempts

## Project Structure

- `app/src/main/java/com/joshy/civictestuscis/data`: JSON models, repository, APIs, Room
- `app/src/main/java/com/joshy/civictestuscis/domain`: quiz engine and scoring rules
- `app/src/main/java/com/joshy/civictestuscis/ui`: Compose UI + ViewModel
- `scripts/extract_civics_qa.py`: PDF-to-JSON extractor/validator
- `data/questions_2025.json`: canonical extracted content

## Build Requirements

- JDK 17+ (JDK 21 works)
- Android SDK installed
- `local.properties` with SDK path, for example:

```
sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

## Commands

**Build the debug APK:**

```
./gradlew assembleDebug
```

Output: `App/build/outputs/apk/debug/app-debug.apk`

**List Gradle tasks:**

```
./gradlew tasks
```

**Run unit tests:**

```
./gradlew testDebugUnitTest
```

## Installation

To install the app on a device or emulator:

```
adb install App/build/outputs/apk/debug/app-debug.apk
```

## Notes on Up-to-Date Answers

The baseline 128 Q/A set comes from the official USCIS 2025 PDF. The app injects ZIP-based current answers for questions that can change. If officials change, answers refresh from API sources on next quiz start.

## Disclaimer

### Educational Purposes Only
This application is an unofficial, independent study tool developed for educational purposes. It is not affiliated with, endorsed by, or approved by the United States Citizenship and Immigration Services (USCIS) or the U.S. Department of Homeland Security. 

### No Warranty (As-Is)
This software is provided "as is" and "with all faults," without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, accuracy, or non-infringement. The author makes no guarantees that the questions, answers, or study materials provided in this app are completely up-to-date, accurate, or reflective of the actual naturalization exam. 

### Limitation of Liability
In no event shall the author or copyright holders be liable for any claim, damages, or other liability, whether in an action of contract, tort, or otherwise, arising from, out of, or in connection with the software or the use or other dealings in the software. 

**By using this software, you agree that you are solely responsible for verifying the accuracy of the study material against official sources (such as uscis.gov). The author is not responsible for any failed exam outcomes, immigration delays, or damages resulting from the use of this application.**

