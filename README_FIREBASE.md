# How to set up Firebase on your device for proper compiling
Follow these steps to configure Firebase and get your project compiling properly on your local machine. (P.S you need to do it for EVERY machine you use)

---

## Step 1: Get Your SHA-1 Key

1. Open the **Terminal** in Android Studio in the current project (`Alt + F12`).

2. Run the signing report Gradle task:
   ```bash
   ./gradlew signingReport
   ```
it would look like this:

```
Variant: debug
Config: debug
Store: C:\Users\...\.android\debug.keystore
Alias: AndroidDebugKey
MD5: 91:31:7A:67:93:78:33:CD:E4:59:CC:82:E9:4B:EF:A6
SHA1: D6:2D:8C:7B:77:F5:6A:FA:D3:6A:2C:BA:09:E2:91:20:17:02:9E:07 <--- copy this one!!
SHA-256: 67:32:78:9F:01:19:54:24:4A:75:42:84:9D:F1:4A:E5:54:7A:D9:54:46:C2:FC:EB:16:27:15:2B:EE:D5:35:D3 <- even though you can copy it, do not!! it's a lie
Valid until: Saturday
```
***Note***: Copy SHA1

## Step 2: Configure Firebase Console
1. Go to https://console.firebase.google.com
2. Choose Ezybag 
   On the current page you can see "2 apps"
3. Choose bag.ezy.ezybagapp
4. click on settings of the app
5. Add your SHA1 fingerprint from previous step
6. Download google-services.json
7. Download it into *androidApp* since the app is made for Android!
   📁 EzybagApp/
   ├── 📁 .github/
   ├── 📁 .gradle/
   ├── 📁 .idea/
   ├── 📁 .kotlin/
   └── 📁 androidApp/
        ├── 📁 build/
        ├── 📁 src/
        ├── 📄 build.gradle.kts
        ├── 📄 google-services.json  <-- Place it here!
        └── 📄 proguard-rules.pro
