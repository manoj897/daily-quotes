# Android Release Plan: Fastlane for Daily Quotes App

## Overview
Set up automated Android release pipeline for Daily Quotes app to deploy to Google Play Store using Fastlane (manual trigger, no CI/CD).

## Prerequisites Checklist
- [ ] Google Play Developer Account ($25 one-time fee)
- [ ] Ruby 2.7+ installed (for Fastlane)
- [ ] Android SDK installed

---

## Phase 1: Google Play Console Account Setup

### 1.1 Create Developer Account
**Action Items:**
1. Go to https://play.google.com/console/signup
2. Pay $25 one-time registration fee
3. Complete account verification (may take 1-2 days)
4. Create new app listing for "Daily Quotes"
   - App name: Daily Quotes
   - Default language: English
   - App/Game: Application
   - Free/Paid: Free
5. Save the package name: `com.dailyquotes.app`

### 1.2 Create API Access Credentials
**Action Items:**
1. Go to Play Console → Settings → API access
2. Click "Create new service account"
3. You'll be redirected to Google Cloud Console
4. Click "Create Service Account"
5. Enter service account details:
   - Name: `daily-quotes-release`
   - ID: Auto-generated
   - Click "Create and Continue"
6. Grant role: "Service Account User"
7. Click "Done"
8. Find your service account in the list, click the 3-dot menu → "Manage keys"
9. Click "Add Key" → "Create new key"
10. Choose JSON format
11. Download JSON key file (save as `play-store-credentials.json`)
12. Back in Play Console → API access:
    - You should see the service account listed
    - Click "Grant access"
    - Grant "Release Manager" role (under Permissions → Releases)
    - Save

**Save securely:**
- File: `play-store-credentials.json` (DO NOT commit to git)

---

## Phase 2: Android Signing Configuration

### 2.1 Interactive Keystore Creation

We'll create a release keystore together. I'll need the following information from you:

**Required Information:**
1. **Keystore Password:** (Choose a strong password, you'll need this for every release)
2. **Key Password:** (Can be the same as keystore password or different)
3. **Your Name or Organization Name:** (e.g., "Your Name" or "Daily Quotes Team")
4. **Organizational Unit:** (e.g., "Development" or "Engineering")
5. **Organization:** (e.g., "Daily Quotes" or "Your Company")
6. **City/Locality:** (e.g., "San Francisco")
7. **State/Province:** (e.g., "California" or "CA")
8. **Country Code:** (2-letter code, e.g., "US", "IN", "UK")

**Keystore Details:**
- File name: `daily-quotes-release.keystore`
- Location: `/Users/manoj.krishnan/` (your home directory - keep secure!)
- Key alias: `dailyquotes`
- Validity: 10,000 days (~27 years)

### 2.2 Keystore Generation Command

Once you provide the above information, I'll generate a custom command for you. Here's the template:

```bash
keytool -genkey -v \
  -keystore ~/daily-quotes-release.keystore \
  -alias dailyquotes \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=[YOUR_NAME], OU=[ORG_UNIT], O=[ORGANIZATION], L=[CITY], ST=[STATE], C=[COUNTRY_CODE]" \
  -storepass [KEYSTORE_PASSWORD] \
  -keypass [KEY_PASSWORD]
```

**Important Notes:**
- **Back up this keystore file!** If you lose it, you can never update your app again
- Store passwords in a secure password manager
- Never commit keystore or passwords to git

### 2.3 Keystore Properties File

Create a local file to store signing credentials (DO NOT commit to git).

**File:** `keystore.properties` (at project root)

```properties
storeFile=/Users/manoj.krishnan/daily-quotes-release.keystore
storePassword=[YOUR_KEYSTORE_PASSWORD]
keyAlias=dailyquotes
keyPassword=[YOUR_KEY_PASSWORD]
```

I'll create this file for you with your actual passwords once you provide them.

### 2.4 Update Gradle Configuration

**File:** `composeApp/build.gradle.kts`

**Changes to make:**

1. Add keystore properties loading at the top:
```kotlin
import java.util.Properties
import java.io.FileInputStream

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
```

2. Add signing configuration:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 2.5 Create ProGuard Rules

**File:** `composeApp/proguard-rules.pro` (new file)

```proguard
# Keep Compose runtime
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }

# Keep Koin DI
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Keep SQLDelight
-keep class app.cash.sqldelight.** { *; }
-keep class com.dailyquotes.db.** { *; }

# Keep Ktor HTTP client
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep data classes and models
-keep class com.dailyquotes.shared.** { *; }
-keep class com.dailyquotes.app.** { *; }

# Keep Voyager navigation
-keep class cafe.adriel.voyager.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep generic signatures
-keepattributes Signature
-keepattributes Exceptions
```

---

## Phase 3: Version Management

### 3.1 Centralized Version Configuration

**File:** `gradle.properties`

**Add version properties:**
```properties
# App Version
VERSION_NAME=1.0.0
VERSION_CODE=1
```

### 3.2 Update Build File to Use Version Properties

**File:** `composeApp/build.gradle.kts`

**Modify android defaultConfig:**
```kotlin
android {
    defaultConfig {
        applicationId = "com.dailyquotes.app"
        minSdk = 24
        targetSdk = 34
        compileSdk = 34
        versionCode = project.property("VERSION_CODE").toString().toInt()
        versionName = project.property("VERSION_NAME").toString()
    }
}
```

### 3.3 Version Bump Strategy

For each new release, manually update `gradle.properties`:

```properties
# Example: Updating to version 1.1.0 (feature release)
VERSION_NAME=1.1.0
VERSION_CODE=2

# Or for a patch: 1.0.1
VERSION_NAME=1.0.1
VERSION_CODE=2
```

**Version Code Rules:**
- Must always increment (never reuse a number)
- Must be an integer
- Play Store uses this to determine which version is newer

---

## Phase 4: Fastlane Setup

### 4.1 Install Fastlane

```bash
# Option 1: Using Bundler (recommended)
sudo gem install bundler
bundle install

# Option 2: Direct installation
sudo gem install fastlane -NV

# Option 3: Using Homebrew
brew install fastlane
```

Verify installation:
```bash
fastlane --version
```

### 4.2 Initialize Fastlane for Android

```bash
cd /Users/manoj.krishnan/StudioProjects/Personal/daily-quotes

# Create fastlane directory
mkdir -p fastlane

# Initialize
cd fastlane
fastlane init

# When prompted:
# - Package Name: com.dailyquotes.app
# - Path to json secret file: ../play-store-credentials.json
# - Download metadata: Yes (if you want)
```

### 4.3 Directory Structure

After setup, you'll have:
```
daily-quotes/
├── fastlane/
│   ├── Fastfile           # Main lane definitions
│   ├── Appfile            # App configuration
│   ├── Gemfile            # Ruby dependencies
│   └── metadata/
│       └── android/
│           └── en-US/
│               ├── title.txt
│               ├── short_description.txt
│               ├── full_description.txt
│               ├── changelogs/
│               └── images/
│                   ├── icon.png
│                   ├── featureGraphic.png
│                   └── phoneScreenshots/
```

### 4.4 Fastfile Configuration

**File:** `fastlane/Fastfile`

```ruby
default_platform(:android)

platform :android do

  desc "Build a release AAB"
  lane :build_release do
    gradle(
      task: "clean bundle",
      build_type: "Release",
      print_command: false,
      properties: {
        "android.injected.signing.store.file" => ENV['KEYSTORE_FILE'],
        "android.injected.signing.store.password" => ENV['KEYSTORE_PASSWORD'],
        "android.injected.signing.key.alias" => ENV['KEY_ALIAS'],
        "android.injected.signing.key.password" => ENV['KEY_PASSWORD'],
      }
    )

    UI.success("Successfully built release AAB!")
    UI.message("AAB location: " + lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH])
  end

  desc "Upload to Google Play Console (Production)"
  lane :deploy_production do
    # First build the release
    build_release

    # Upload to Play Store
    upload_to_play_store(
      track: 'production',
      aab: lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH],
      skip_upload_apk: true,
      skip_upload_metadata: false,
      skip_upload_images: false,
      skip_upload_screenshots: false,
      release_status: 'completed',  # Auto-publish after review
      json_key: 'play-store-credentials.json'
    )

    UI.success("Successfully uploaded to Production!")
  end

  desc "Upload to Google Play Internal Testing"
  lane :deploy_internal do
    build_release

    upload_to_play_store(
      track: 'internal',
      aab: lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH],
      skip_upload_apk: true,
      skip_upload_metadata: true,
      skip_upload_images: true,
      skip_upload_screenshots: true,
      json_key: 'play-store-credentials.json'
    )

    UI.success("Successfully uploaded to Internal Testing!")
  end

  desc "Build and install debug version on connected device"
  lane :install_debug do
    gradle(task: "installDebug")
  end

end
```

### 4.5 Appfile Configuration

**File:** `fastlane/Appfile`

```ruby
json_key_file("play-store-credentials.json")
package_name("com.dailyquotes.app")
```

### 4.6 Store Metadata Setup

Create the following files for Play Store listing:

**File:** `fastlane/metadata/android/en-US/title.txt`
```
Daily Quotes
```

**File:** `fastlane/metadata/android/en-US/short_description.txt`
```
Inspiring quotes every day with personal reflections and journal
```

**File:** `fastlane/metadata/android/en-US/full_description.txt`
```
Daily Quotes brings you inspiring quotes every day to motivate and inspire your journey.

🌟 FEATURES
• Daily curated quotes from great thinkers and leaders
• Personal reflection journal to capture your thoughts
• Tag and organize your reflections
• Beautiful, minimalist design
• Share quotes with friends and social media
• Notification reminders (optional)

📝 REFLECT & GROW
Not just another quotes app - Daily Quotes encourages you to reflect on each quote and write down your own thoughts. Build a personal journal of wisdom and insights over time.

🎯 ORGANIZE YOUR THOUGHTS
Tag your reflections by themes like #motivation, #wisdom, #inspiration, or create your own custom tags. Easily find and revisit reflections that matter to you.

💡 DAILY INSPIRATION
Start your day with a thought-provoking quote. Enable optional notifications to get your daily dose of inspiration at a time that works for you.

Download Daily Quotes today and start your journey of reflection and personal growth!
```

**File:** `fastlane/metadata/android/en-US/changelogs/1.txt`
```
Initial release of Daily Quotes!

Features:
- Daily inspirational quotes
- Personal reflection journal
- Tag organization
- Share functionality
- Beautiful minimalist design
```

### 4.7 Screenshots and Graphics

Prepare the following (manually for now):

**Required:**
1. **App Icon** (512x512 PNG)
   - Save to: `fastlane/metadata/android/en-US/images/icon.png`

2. **Feature Graphic** (1024x500 PNG)
   - Save to: `fastlane/metadata/android/en-US/images/featureGraphic.png`

3. **Phone Screenshots** (at least 2, max 8)
   - Dimensions: 1080x1920 or 1440x2560 recommended
   - Save to: `fastlane/metadata/android/en-US/images/phoneScreenshots/`
   - Name: `1.png`, `2.png`, etc.

**How to take screenshots:**
```bash
# Run app on device/emulator
./build_android.sh

# Use Android Studio Device File Explorer or:
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

---

## Phase 5: Update .gitignore

**File:** `.gitignore`

**Add these lines:**
```gitignore
# Signing files - NEVER COMMIT THESE
*.keystore
*.jks
keystore.properties
play-store-credentials.json

# Fastlane
fastlane/report.xml
fastlane/Preview.html
fastlane/screenshots
fastlane/test_output
fastlane/.env
```

**Verify it's working:**
```bash
git status
# Should NOT show keystore or credentials files
```

---

## Phase 6: First Release Process

### 6.1 Pre-Release Checklist

- [ ] Google Play Developer account active
- [ ] Keystore created and backed up
- [ ] `keystore.properties` created (not committed to git)
- [ ] `play-store-credentials.json` downloaded (not committed to git)
- [ ] Fastlane installed and configured
- [ ] Store metadata filled in
- [ ] Screenshots prepared
- [ ] Privacy policy URL ready (create at https://privacypolicygenerator.info/)
- [ ] App tested thoroughly on multiple devices
- [ ] Version set to 1.0.0 / code 1

### 6.2 Build Test

First, test the build locally:

```bash
cd /Users/manoj.krishnan/StudioProjects/Personal/daily-quotes

# Test release build
fastlane android build_release

# Check output
ls -lh composeApp/build/outputs/bundle/release/
# Should see: composeApp-release.aab
```

### 6.3 Manual First Upload (Recommended)

For the first release, manually upload to Play Console:

1. Go to https://play.google.com/console/
2. Select "Daily Quotes" app
3. Navigate to "Production" → "Releases"
4. Click "Create new release"
5. Upload AAB file: `composeApp/build/outputs/bundle/release/composeApp-release.aab`
6. Fill in release notes
7. Review and roll out to production

**Complete the following required sections:**
- App content (privacy policy, ads, target audience)
- Content rating questionnaire
- Pricing & distribution (select countries)
- Store listing (should auto-fill from fastlane metadata)

Submit for review (typically takes 1-3 days).

### 6.4 Automated Upload (After First Release)

Once your app is approved and published, you can use Fastlane for subsequent releases:

```bash
# For production releases
fastlane android deploy_production

# For internal testing (optional)
fastlane android deploy_internal
```

---

## Phase 7: Ongoing Release Workflow

### 7.1 Version Update Process

**For each new release:**

1. **Update version in `gradle.properties`:**
   ```properties
   # Patch release (1.0.0 → 1.0.1)
   VERSION_NAME=1.0.1
   VERSION_CODE=2

   # Minor release (1.0.1 → 1.1.0)
   VERSION_NAME=1.1.0
   VERSION_CODE=3

   # Major release (1.1.0 → 2.0.0)
   VERSION_NAME=2.0.0
   VERSION_CODE=4
   ```

2. **Create changelog:**

   Create file: `fastlane/metadata/android/en-US/changelogs/[VERSION_CODE].txt`

   Example for version code 2:
   ```
   fastlane/metadata/android/en-US/changelogs/2.txt
   ```

   Content:
   ```
   Bug fixes and improvements:
   - Fixed reflection sharing issue
   - Improved performance
   - Updated UI design
   ```

3. **Commit changes:**
   ```bash
   git add gradle.properties fastlane/metadata/
   git commit -m "Release v1.0.1"
   git tag v1.0.1
   git push origin main --tags
   ```

4. **Deploy:**
   ```bash
   fastlane android deploy_production
   ```

### 7.2 Hotfix Release Process

For urgent bug fixes:

```bash
# 1. Fix the bug in code

# 2. Update version (patch increment)
# Edit gradle.properties:
# VERSION_NAME=1.0.1
# VERSION_CODE=2

# 3. Create changelog
echo "Critical bug fix: [description]" > fastlane/metadata/android/en-US/changelogs/2.txt

# 4. Commit and tag
git add .
git commit -m "Hotfix v1.0.1: [description]"
git tag v1.0.1
git push origin main --tags

# 5. Deploy immediately
fastlane android deploy_production
```

### 7.3 Testing Before Release

Always test the release build before deploying:

```bash
# 1. Build release AAB
fastlane android build_release

# 2. Install on device for testing (need to convert AAB to APK first)
# Use bundletool: https://developer.android.com/studio/command-line/bundletool

# Or use internal testing track:
fastlane android deploy_internal
```

---

## Phase 8: Troubleshooting

### Common Issues

**1. Keystore not found:**
```
Error: Keystore file not found
```
**Solution:** Check path in `keystore.properties`, ensure file exists at specified location.

**2. Wrong password:**
```
Error: Keystore was tampered with, or password was incorrect
```
**Solution:** Verify passwords in `keystore.properties` match what you set during keystore creation.

**3. Play Store API authentication failed:**
```
Error: Google Api Error: Invalid Credentials
```
**Solution:**
- Verify `play-store-credentials.json` is in project root
- Check service account has "Release Manager" role in Play Console

**4. Build fails with ProGuard errors:**
```
Error: Missing class referenced from...
```
**Solution:** Add keep rules to `composeApp/proguard-rules.pro` for the missing classes.

**5. Fastlane command not found:**
```
bash: fastlane: command not found
```
**Solution:**
```bash
sudo gem install fastlane
# Or add to PATH if installed via bundle
```

---

## Critical Files Summary

### Files to CREATE:
- [ ] `keystore.properties` (signing credentials - not in git)
- [ ] `composeApp/proguard-rules.pro` (ProGuard rules)
- [ ] `fastlane/Fastfile` (Fastlane lanes)
- [ ] `fastlane/Appfile` (App configuration)
- [ ] `fastlane/metadata/android/en-US/title.txt`
- [ ] `fastlane/metadata/android/en-US/short_description.txt`
- [ ] `fastlane/metadata/android/en-US/full_description.txt`
- [ ] `fastlane/metadata/android/en-US/changelogs/1.txt`
- [ ] `fastlane/metadata/android/en-US/images/icon.png`
- [ ] `fastlane/metadata/android/en-US/images/featureGraphic.png`
- [ ] `fastlane/metadata/android/en-US/images/phoneScreenshots/[1-8].png`

### Files to MODIFY:
- [ ] `composeApp/build.gradle.kts` (signing config, minification, version properties)
- [ ] `gradle.properties` (add VERSION_NAME and VERSION_CODE)
- [ ] `.gitignore` (add signing files, Fastlane artifacts)

### Files to KEEP SECURE (never commit):
- [ ] `~/daily-quotes-release.keystore` (Android signing key - BACKUP!)
- [ ] `keystore.properties` (local signing config)
- [ ] `play-store-credentials.json` (Play Store API key)

---

## Security Best Practices

1. **Keystore Backup:**
   - Copy `~/daily-quotes-release.keystore` to at least 2 secure locations
   - Consider encrypted cloud storage (Google Drive, iCloud with encryption)
   - Store passwords in a password manager (1Password, Bitwarden, etc.)

2. **Never Commit Secrets:**
   - Verify `.gitignore` includes all sensitive files
   - Run `git status` before every commit
   - Use `git diff` to review changes

3. **Service Account Security:**
   - Keep `play-store-credentials.json` secure
   - Never share publicly or commit to GitHub
   - Rotate keys annually

---

## Estimated Timeline

| Task | Time Required |
|------|---------------|
| Google Play account setup & verification | 1-3 days |
| Keystore creation & signing config | 30 minutes |
| Fastlane installation & setup | 1-2 hours |
| Store metadata & screenshots | 2-4 hours |
| Privacy policy creation | 30 minutes |
| First release submission | 1 hour |
| Play Store review | 1-3 days |
| **Total** | **~1 week** |

---

## Quick Reference Commands

```bash
# Build release AAB
fastlane android build_release

# Deploy to production
fastlane android deploy_production

# Deploy to internal testing
fastlane android deploy_internal

# Install debug build
fastlane android install_debug

# Check Fastlane version
fastlane --version

# Update Fastlane
sudo gem update fastlane
```

---

## Support Resources

- **Fastlane Documentation:** https://docs.fastlane.tools
- **Google Play Console Help:** https://support.google.com/googleplay/android-developer
- **Android App Bundle Guide:** https://developer.android.com/guide/app-bundle
- **ProGuard Documentation:** https://www.guardsquare.com/manual/home
- **Keystore Management:** https://developer.android.com/studio/publish/app-signing

---

## Next Steps

Ready to start? Here's the order of implementation:

1. ✅ Review this plan
2. Provide keystore information (passwords, organization details)
3. Create Google Play Developer account
4. Generate keystore together
5. Set up signing configuration
6. Install and configure Fastlane
7. Prepare store metadata and screenshots
8. Test release build
9. Submit first release manually
10. Set up automated releases with Fastlane

Let me know when you're ready to begin!
