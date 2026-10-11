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


## Contractor Signup Feature

The Contractor Signup feature is implemented in `shared/src/commonMain/kotlin/bag/ezy/ezybagapp/ContractorSignupScreen.kt`. It provides a registration form for waste-removal contractors using Compose Multiplatform.

### Dependencies Used

The feature uses the following Kotlin and Compose libraries:

| Dependency | Purpose |
|---|---|
| Kotlin Standard Library | Provides core Kotlin language functionality. |
| Compose Runtime | Manages UI state using `remember` and `mutableStateOf`. |
| Compose Foundation | Provides layout components, scrolling, spacing, and click interactions. |
| Compose Material 3 | Provides UI components such as `Text`, `Card`, `Button`, `Checkbox`, `Surface`, and `OutlinedTextField`. |
| Compose UI | Provides `Modifier`, alignment, colours, typography, and dimensions. |
| Compose UI Tooling Preview | Supports the `@Preview` annotation for previewing the signup screen where the IDE and target configuration support it. |

### UI Functionality

The screen includes:

- Business details: company name, contact person's name, email address, and phone number.
- Service areas: towns or postcodes covered.
- Waste carrier licence: registration number and licence document selection.
- Insurance: insurance provider, policy number, and proof of insurance selection.
- Vehicle details: vehicle type and registration number.
- Identification: proof of identity selection.
- Declaration: confirmation that the submitted information is accurate.
- Form validation: checks required fields and displays a message when information is missing.
- Demo submission: displays a success message when the form passes validation.

### Implementation Notes

- The screen is implemented in the shared `commonMain` source set to support reuse across the project's Kotlin Multiplatform targets.
- The form uses local Compose state for input values, document-selection indicators, validation messages, and submission status.
- Document selection is currently a visual demonstration only. Actual file uploads, data persistence, backend submission, and contractor approval workflows are not implemented in this feature.
- The feature uses the project's existing Compose dependencies; no additional third-party dependency is required specifically for the signup screen, assuming the imports are already configured in the project.
