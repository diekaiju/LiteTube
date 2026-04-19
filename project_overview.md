# Project Overview: LiteTube (NewPipe)

LiteTube is a lightweight, privacy-focused Android streaming client based on the **NewPipe** project. It allows users to watch and listen to content from YouTube, PeerTube, SoundCloud, and other services without a Google account or proprietary libraries.

## 🚀 Key Features

- **Multi-Service Support**: YouTube, PeerTube, Bandcamp, SoundCloud, media.ccc.de.
- **Privacy-First**: No Google Play Services required; doesn't use official APIs for restricted services.
- **Advanced Playback**: Background audio, Popup (Picture-in-Picture) mode, and 4K support.
- **Local Features**: Subscriptions, playlists, and history stored locally on the device.
- **No Ads**: Built-in ad-blocking as a consequence of how it fetches content.

## 🛠 Technology Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin & Java |
| **Build System** | Gradle (Kotlin DSL), Version Catalogs (`libs.versions.toml`) |
| **UI Framework** | Android XML View Binding, Material Design 3 |
| **Media Player** | ExoPlayer |
| **Network** | OkHttp, Jsoup |
| **Reactive** | RxJava 3 |
| **Database** | Room (SQL) |
| **Parsing** | NewPipe Extractor |
| **Image Loading**| Coil |
| **Architecture** | Fragment-based navigation with a central `MainActivity` |

## 📁 Repository Structure

```text
LiteTube/
├── app/                        # Main Android application module
│   ├── src/
│   │   └── main/
│   │       ├── java/org/schabi/newpipe/
│   │       │   ├── App.kt           # Application class (Initialization)
│   │       │   ├── MainActivity.java # Entry point & Fragment orchestration
│   │       │   ├── player/          # Core player logic (ExoPlayer integration)
│   │       │   ├── fragments/       # UI Screens (List, Detail, Home)
│   │       │   ├── local/           # Local Data (DB, Feed, Notifications)
│   │       │   └── util/            # Helper utilities
│   │       └── res/                 # Resources (Layouts, Drawables, Strings)
│   └── build.gradle.kts        # App-level build configuration
├── assets/                     # App icons and graphics
├── gradle/                     # Gradle wrapper and dependency catalog
├── doc/                        # Documentation in various languages
└── build.gradle.kts            # Root build configuration
```

## 🏗 Key Components

### 1. **NewPipe Extractor**
The engine that parses streaming websites and internal APIs. It's used to fetch video details, search results, and stream URLs.

### 2. **Player (`org.schabi.newpipe.player`)**
A robust implementation of ExoPlayer that supports:
- Seamless transitions between Video, Audio, and Popup modes.
- Playback speed control and rotation locks.
- Integration with Android MediaSession for notification controls.

### 3. **Navigation Architecture**
- `MainActivity` acts as the host for fragments.
- Uses `DrawerLayout` for the side menu.
- `NavigationHelper` facilitates fragment transitions.

### 4. **Local Storage**
Uses **Room** to manage:
- User subscriptions.
- Local playlists.
- Watch history.
- Downloaded content metadata.

## 🔄 Current Development
The project is currently undergoing a **major refactor** (as mentioned in the README) to modernize the codebase and improve stability. Development is primarily happening in the `refactor` branch.
