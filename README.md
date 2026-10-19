This is a Kotlin Multiplatform project targeting Android, iOS, Web.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Web app:
  - Wasm target (faster, modern browsers): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :webApp:jsBrowserDevelopmentRun`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- Web tests:
  - Wasm target: `./gradlew :shared:wasmJsTest`
  - JS target: `./gradlew :shared:jsTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://kotlinlang.org/compose-multiplatform/),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).

---

## Customer Create Job Screen

### Overview

Implemented the initial customer-facing Create Job screen using Kotlin and Compose Multiplatform.

### Features

- Collection address, postcode, and phone number fields.
- Selectable waste categories: Household, Garden, Construction, Furniture, Electrical, Mixed, and Other.
- Estimated waste volume selection.
- Multiple photo and video file selection using the device file picker.
- Ability to remove selected files.
- Basic validation for required address, postcode, and phone fields.
- Dark teal interface with green accents.

### Dependency Added

- **FileKit Compose — `io.github.vinceglb:filekit-compose:0.8.8`**
  - Added to `shared/build.gradle.kts` under `commonMain.dependencies`.
  - Used to open the device file picker and select multiple photos or videos.

### Current Limitations

- Selected filenames are displayed locally; media is not uploaded to a backend yet.
- The Continue button validates the required fields but does not navigate to another screen yet.
- Booking submission, data persistence, pricing, and payment integration remain future work.

### Naming
- The customer-facing thing is a Booking.
- The flow to make one is NewBooking (feature/newbooking).
- “Job” is only used as UI text in the contractor app.
- Feature packages are lowercase nouns.
- Actions are verbs (createBooking, submitReview)