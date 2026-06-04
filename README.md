# MVL Android Assignment

A location-based booking app built as part of the MVL take-home test.

## Setup

### 1. API Keys
Keys are loaded from `local.properties` at build time — they are **never** in source code.

```bash
cp local.properties.example local.properties
```

Then fill in your keys in `local.properties`:

```properties
MAPS_API_KEY=your_google_maps_key
AQI_API_KEY=your_aqicn_token
```

`local.properties` is listed in `.gitignore` and will never be committed.

- **Google Maps**: [Google Cloud Console](https://console.cloud.google.com) → Maps SDK for Android
- **AQI**: [aqicn.org/data-platform/token/](https://aqicn.org/data-platform/token/) — free tier

### 2. Run
Open in Android Studio Hedgehog+, sync Gradle, run on device/emulator with API 26+.

### Key Design Decisions

**Single Activity + Compose Navigation**
`MainActivity` hosts `MVLNavHost`. All screens are composable destinations.

**Shared ViewModel (MapViewModel)**
Scoped to the NavGraph via `hiltViewModel()` called once in `MVLNavHost` and passed down.
This ensures Screen 1 state (A/B locations, current booking) is accessible in Screen 2 and 3 without re-fetching.

**Mock Layer via OkHttp Interceptor**
`MockBooksInterceptor` intercepts all requests to `/books` before they hit the network.
It dynamically generates responses based on request body/parameters — not static files.
Swapping to a real server requires only removing the interceptor from the `BooksApi` OkHttpClient; zero business logic changes needed.

**Coroutines + Flow**
- `StateFlow` for UI state in both ViewModels
- AQI fetches debounced 600ms on camera idle via `Job.cancel()`
- All network calls on `Dispatchers.IO` via repository

**Address Formatting**
`localityInfo.administrative` entries are sorted descending by `order`, top 2 names joined with `, `.
Example: order=5 ("Yangjae 2(i)-dong") + order=4 ("Seocho District") → "Yangjae 2(i)-dong, Seocho District"

---

## Screen Flow

```
Screen 1 (Map)
  ├── tap A/B label → Screen 2 (Nickname)
  │                       └── confirm → back to Screen 1
  └── tap Book → POST /books → Screen 3 (Booking Confirm)
                    ├── tap View History → Screen 4 (History)
                    └── back press → Screen 1 (RESET state)
```

## V Button State Machine

| State  | Button Label | Action on Tap                              |
|--------|-------------|---------------------------------------------|
| SET_A  | "Set A"     | Fetch address+AQI for marker → store as A   |
| SET_B  | "Set B"     | Fetch address+AQI for marker → store as B   |
| BOOK   | "Book"      | POST /books → navigate to Screen 3          |

---

## Optional Enhancements Implemented
- ✅ Dynamic mock responses based on request parameters (not static files)
- ✅ Repository pattern with `LocationRepository` — cache-first strategy
- ✅ Local coordinate cache with **Room** (`MVLDatabase`, `LocationDao`, `LocationEntity`)
  - Two coords match if they round to the same 3 decimal place key
  - Cache hit skips both geocoding and AQI network calls
  - Nicknames persisted to Room via `saveNickname()`
- ✅ **Screen 5** — cache picker launched when an unset A or B label is tapped
  - Shows all previously cached locations with nickname/address and AQI
  - Selecting one sets A or B directly and returns to Screen 1
  - Button label updates accordingly (Set A / Set B)
- ✅ `loadFromHistory()` in MapViewModel — tap Screen 4 item → Screen 1 with A/B pre-filled, re-fetches fresh AQI

## Optional Enhancements Not Implemented
- None — all optional items completed
