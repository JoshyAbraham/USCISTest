# Release Signing Configuration

## Keystore Details

**Location**: `App/release-keystore.jks`  
**Alias**: `civictest`  
**Store Password**: `civictest2025`  
**Key Password**: `civictest2025`  
**Validity**: 10,000 days  
**Key Algorithm**: RSA 2048-bit  

**Certificate DN**: CN=Civic Test USCIS, OU=Education, O=CivicTest, L=Unknown, ST=Unknown, C=US

## Important Security Notes

⚠️ **SECURITY WARNING**: The keystore and passwords in this project are for DEVELOPMENT/TESTING purposes only.

For production releases:
1. **Generate a new secure keystore** with strong passwords
2. **Store keystore and passwords securely** (e.g., password manager, secure vault)
3. **NEVER commit keystore files or passwords to version control**
4. **Keep multiple secure backups** of your production keystore
5. **If you lose the production keystore**, you cannot update the app on Google Play Store

## Building Signed APK

```bash
# Build signed release APK
.\gradlew.bat :App:assembleRelease

# Output location
App/build/outputs/apk/release/app-release.apk
```

## Verifying Signature

```bash
# Using apksigner (recommended)
%ANDROID_HOME%\build-tools\[version]\apksigner.bat verify --print-certs App\build\outputs\apk\release\app-release.apk

# Using keytool to view keystore
keytool -list -v -keystore App\release-keystore.jks -alias civictest -storepass civictest2025
```

## Configuration Files

- **Build configuration**: `App/build.gradle.kts` (signingConfigs section)
- **Keystore properties**: `App/keystore.properties` (gitignored for security)
- **Keystore file**: `App/release-keystore.jks` (gitignored for security)

## For Production Deployment

Before releasing to Google Play Store:

1. Generate a production keystore with secure passwords:
```bash
keytool -genkeypair -v -keystore production-keystore.jks -alias production_key -keyalg RSA -keysize 2048 -validity 10000
```

2. Update `App/keystore.properties` with production credentials

3. Build and test the signed APK thoroughly

4. Consider using Google Play App Signing for additional security and key management
