# Ezybag

A waste-removal marketplace app that connects people who need rubbish taken away with local, approved contractors. Customers book and pay for a pickup, contractors find and accept jobs, and admins approve contractors and keep an eye on bookings, revenue and issues.

Built with **Kotlin Multiplatform** and **Compose Multiplatform**, backed by **Firebase**, with payments through **Stripe**. Android is the primary target.

---

## Features

### Customer
- Sign up / sign in (email + password or Google)
- Book a pickup: address, waste type, volume, description, photos/videos
- Schedule a date and time and get a quote
- Pay through Stripe Checkout (success, failed and cancelled flows)
- View bookings, booking details and booking status
- Message or call the assigned contractor, or cancel a booking
- Rate the contractor once a job is completed
- Support page and account management

### Contractor
- Contractor sign-up with an approval step (pending screen until an admin approves)
- Home overview with job filters
- Browse available jobs, view job details and accept jobs
- Support page and account management

### Admin
- Dashboard with key metrics and pending actions
- Approve / manage contractors and view contractor details
- View customers, bookings, issues, reports and revenue
- Settings and sign out

---

## Tech stack

| Area          | Technology                                                                                                                  |
|---------------|-----------------------------------------------------------------------------------------------------------------------------|
| Language      | Kotlin 2.4 (Multiplatform)                                                                                                  |
| UI            | Compose Multiplatform 1.12, Material 3                                                                                      |
| Architecture  | MVVM (Screen / Content / ViewModel) + repositories                                                                          |
| Backend       | Firebase Authentication + Cloud Firestore via the [GitLive Firebase SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk) |
| Payments      | Stripe Android SDK                                                                                                          |
| Dates & times | kotlinx-datetime / `kotlin.time.Instant`                                                                                    |
| File picking  | FileKit                                                                                                                     |
| CI            | GitHub Actions                                                                                                              |
| Android       | minSdk 24, target/compile SDK 37, JVM target 17                                                                             |

---

## Project structure

```
EzybagApp/
├── androidApp/                 Android entry point (MainActivity, google-services.json goes here)
├── iosApp/                     iOS entry point (not actively maintained)
├── webApp/                     Web entry point (not actively maintained)
└── shared/src/
    ├── commonMain/kotlin/bag/ezy/ezybagapp/
    │   ├── App.kt              Root composable + top-level navigation
    │   ├── app/                Session routing (works out the signed-in user's role)
    │   ├── dev/                Developer menu
    │   ├── di/                 AppContainer – single place repositories are created
    │   ├── feature/            One package per screen/feature
    │   │   ├── startpage/      Landing screen
    │   │   ├── auth/           Sign in / sign up
    │   │   ├── main/           Customer tab container (Home, Bookings, Support, Account)
    │   │   ├── home/  bookings/  bookingdetails/  newbooking/  payment/  ratings/
    │   │   ├── contractor/  contractorapproval/  contractormain/
    │   │   ├── contractorhome/  contractorjobs/  contractoraccount/
    │   │   ├── admin/          Dashboard, contractors, customers, bookings, issues, reports, revenue, settings
    │   │   ├── support/  account/
    │   │   └── dbdevpage/      Firestore debug screen (dev only)
    │   ├── model/
    │   │   ├── data/           Data classes (Booking, Contractor, Customer, Review, …)
    │   │   ├── repository/     Repository interfaces + Firebase implementations
    │   │   └── queries/        Admin / review / Google auth queries
    │   └── ui/                 Theme, shared components, formatting and search helpers
    ├── androidMain/            Android-only code (Stripe service, Checkout screen)
    └── iosMain/                iOS-only code
```

---

## Architecture

### MVVM
Each feature follows the same pattern:

- **`XScreen`** – stateful entry point. Gets its ViewModel and passes state and callbacks down.
- **`XContent`** – stateless UI that only takes state and lambdas, so it can be previewed in Android Studio.
- **`XViewModel`** – holds UI state (`var state by mutableStateOf(...)`) and talks to repositories.

Repositories are defined as interfaces with Firebase implementations, and are created once in `di/AppContainer`.

### Navigation
Top-level navigation lives in `App.kt` using a `Screen` enum and a `when (screen)` block. Each role has its own container with tabs:

- **Customer** → `MainContainer` (Home, Bookings, Support, Account)
- **Contractor** → `ContractorMainContainer` (Home, Jobs, Support, Account)
- **Admin** → `AdminDashboardScreen` (sub-pages handled inside the dashboard)

On launch the app shows a loading screen and calls `resolveCurrentRole()` (`app/SessionRouting.kt`), which sends the user to the right place:

| Signed-in user      | Goes to                        |
|---------------------|--------------------------------|
| Not signed in       | Start screen                   |
| Admin               | Admin dashboard                |
| Approved contractor | Contractor home                |
| Pending contractor  | Contractor home (pending view) |
| Customer            | Customer home                  |

The same routing runs after sign in and sign up. Signing out from any role returns to the Start screen. The Android system back button is handled with `BackHandler` so it matches each screen's on-screen back button.

### Firestore collections
`users`, `customers`, `contractors`, `admins`, `bookings`, `reviews`, `issueReports`, `adminOnly`

---

## Getting started

### Prerequisites
- Android Studio (latest stable)
- JDK 17 or newer
- An Android emulator or device (API 24+)
- Access to the **Ezybag** Firebase project and the team's Stripe test keys

### 1. Clone and switch to `testing`
```bash
git clone https://github.com/degenerade/EzybagApp.git
cd EzybagApp
git checkout testing
```

### 2. Set up Firebase
Each developer has to add their machine's SHA-1 fingerprint to Firebase and download `google-services.json` into `androidApp/`.
Full steps: **[README_FIREBASE.md](README_FIREBASE.md)**.

### 3. Add Stripe keys
Create a file called `secrets.properties` in the **project root** (next to `local.properties`):

```properties
STRIPE_PUBLISHABLE_KEY=pk_test_...
STRIPE_SECRET_KEY=sk_test_...
```

- No quotes around the values.
- Ask a team member for the **test** keys. Never use live keys.
- `secrets.properties` is in `.gitignore`. Check it never shows up in `git status`:
  ```bash
  git check-ignore -v secrets.properties
  ```

### 4. Run
Select the **androidApp** run configuration in Android Studio, or build from the terminal:

```bash
./gradlew :androidApp:assembleDebug
```

### Paying in test mode
Use Stripe's test card **4242 4242 4242 4242** with any future expiry date and any CVC.

---

## Developer menu

While `DEV_MENU_ENABLED = true` (in `dev/DevMenu.kt`), a small **DEV** button appears in the top-right corner. It opens a menu that jumps straight to any screen, which is useful for working on a single page without going through the full flow.

> Set `DEV_MENU_ENABLED = false` before merging `testing` into `master`.

---

## Branching and contributing

```
feature branch  ──PR──▶  testing  ──PR──▶  master
```

1. Create a branch from `testing` (e.g. `customer/booking`, `admin/UI`).
2. Before opening a PR, merge the latest `testing` into your branch and make sure it builds.
3. Open a PR **into `testing`** (GitHub defaults to `master`, so check the base branch).
4. A PR into `testing` needs **1 approving review** and a passing build.
5. Only `testing` can be merged into `master`. This is enforced by the `master-source-check` workflow.

### CI
| Workflow                  | Runs on                         | What it does                                                                                                                 |
|---------------------------|---------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| `android-build.yml`       | PRs into `testing` and `master` | Creates `google-services.json` and `secrets.properties` from GitHub Secrets, then runs `./gradlew :androidApp:assembleDebug` |
| `master-source-check.yml` | PRs into `master`               | Fails unless the PR comes from `testing`                                                                                     |

Required repository secrets: `GOOGLE_SERVICES_JSON` (base64-encoded), `STRIPE_PUBLISHABLE_KEY`, `STRIPE_SECRET_KEY`.

### Conventions
- Package names are lowercase nouns (`newbooking`, `contractorjobs`).
- Actions are verbs (`createBooking`, `acceptJob`).
- New screens follow the Screen / Content / ViewModel pattern and get a `@Preview` of the Content.
- Shared formatting (dates, times, prices) goes in `ui/format/Formatting.kt` rather than being duplicated per screen.

---

## Known issues and next steps

- **Stripe secret key in the app.** Payment intents are currently created on the device, so the secret key is built into the APK. This should move to a Firebase Cloud Function (or the *Run Payments with Stripe* Firebase extension) so the app only needs the publishable key.
- **Bookings are saved before payment.** Leaving checkout can leave an unpaid booking behind, and booking again can create a duplicate.
- **Firestore security rules** are not yet stored or reviewed in this repository.
- **iOS / Web** are not maintained. Some JVM-only libraries in `commonMain` would need to move to `androidMain` first.
- **Admin UI polish:** spacing differs between some admin pages.
- Possible future features: in-app chat, push notifications for job updates, uploading booking photos to storage.

---

## Team

| Name                  |
|-----------------------|
| Max Sellick           |
| Varvara Aladinya      |
| Uchenna Enyinna Peter |
| Michael Agunbiade     |
| Lungowe Akushanga     |
| Anastasia Klimson     |
| Maraizu Dominic-Judes |