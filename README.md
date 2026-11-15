# 🔥 Hotmailer

**Hotmailer** is a modern Android email client built with **Kotlin** and **Jetpack Compose** (Material 3), blending a clean Gmail-style inbox experience with signature Hotmailer branding—automatically appending `@Hotmailer` to outgoing sender addresses and email signatures.

---

## ✨ Features

- **Categorized Inbox & Smart Filters**
  - Organized inbox tabs (**Primary**, **Promotions**, **Social**, **Updates**) with live unread count badges.
  - Quick filter chips for **All**, **Unread**, **Starred**, **Attachment**, and **@Hotmailer**.
- **Read / Unread Status Tracking**
  - Unread emails stand out with bold sender, subject, snippet, and timestamp typography.
  - Visual unread dot indicator plus an interactive read/unread envelope icon on every row to toggle status in one tap.
  - Automatic mark-as-read when opening an email, plus **Mark as read / unread** in both detail view and multi-select mode.
- **Compose & Send with `@Hotmailer`**
  - Compose screen with **From**, **To** (with quick contact chips), **Subject**, **Body**, and file attachment options.
  - Automatically ensures the sender address ends with `@Hotmailer` and appends the `@Hotmailer` signature at the end of every sent email.
  - Supports saving **Drafts** and handling external `mailto:` (`ACTION_SENDTO`) intents.
- **Rich Email Detail View**
  - Expandable sender/recipient/security metadata, attachment preview cards, **Reply**, **Forward**, and inline **Quick Reply**.
  - Displays a verified `@Hotmailer` signature badge on messages signed with `@Hotmailer`.
- **Gestures, Search & Folder Management**
  - Floating pill search bar filtering across sender, subject, snippet, and body in real time.
  - Swipe right to **Archive** or swipe left to **Trash** with **Undo** snackbar support.
  - Navigation drawer with **Inbox**, **Starred**, **Sent**, **Drafts**, **Archive**, **Spam**, **Trash**, labels, account switcher, and custom signature settings.
- **Offline Persistence**
  - Backed by a local **Room** SQLite database pre-seeded with realistic sample emails so the app is ready to explore on first launch.

---

## 🛠️ Tech Stack

- **Language:** Kotlin (`2.2.10`)
- **UI:** Jetpack Compose + Material 3 (`enableEdgeToEdge()`)
- **Architecture:** MVVM (`HotmailerViewModel`, `StateFlow`, `EmailRepository`)
- **Database:** Room (`2.7.0`) with KSP
- **Async:** Kotlin Coroutines & Flow
- **Testing:** JUnit 4, Kotlinx Coroutines Test, Robolectric (`4.16.1`)
- **SDK:** `minSdk = 24`, `targetSdk = 36`, `compileSdk = 36`

---

## 📂 Project Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                  # App entry point, Scaffold, Drawer, Navigation & mailto intent handling
├── data/
│   ├── local/
│   │   ├── EmailDao.kt              # Room DAO queries for folders, categories, search & read/star state
│   │   ├── HotmailerDatabase.kt     # Room database singleton with initial sample seed callback
│   │   └── SampleData.kt            # Pre-populated sample threads across Inbox, Sent & Drafts
│   ├── model/
│   │   └── EmailEntity.kt           # Email Room entity & helper properties
│   └── repository/
│       └── EmailRepository.kt       # Email operations & automatic @Hotmailer address/signature logic
└── ui/
    ├── components/
    │   ├── AccountSwitcherDialog.kt # Account profile, storage indicator & active signature preview
    │   ├── CategoryChips.kt         # Category tabs with unread badges & quick filter chips
    │   ├── ComposeScreen.kt         # Email composer with To, Subject, Body, Send & @Hotmailer preview
    │   ├── EmailDetailScreen.kt     # Full email viewer, headers, attachments, Reply/Forward & Quick Reply
    │   ├── EmailListItem.kt         # Swipeable email row with bold unread styling & status indicators
    │   ├── HotmailerDrawer.kt       # Folder & label navigation drawer
    │   ├── HotmailerTopBar.kt       # Floating search bar & multi-select action bar
    │   └── SettingsDialog.kt        # Custom @Hotmailer signature editor & sample data reset
    ├── theme/                       # Color palette, Typography & HotmailerTheme
    └── viewmodel/
        └── HotmailerViewModel.kt    # Reactive mailbox streams, filtering, selection & compose state
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (Narwhal / Meerkat or newer with AGP `9.1.1` support) or Gradle CLI
- JDK 11+
- Android SDK 36

### Build & Run
1. Clone the repository:
   ```bash
   git clone <https://github.com/sweethamza14-sketch/HOTMAILER>
   cd Hotmailer
   ```
2. Build the debug APK:
   ```bash
   ./gradlew :app:assembleDebug
   ```
3. Run the unit & Robolectric tests:
   ```bash
   ./gradlew :app:testDebugUnitTest
   ```
