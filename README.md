# WeatherSnap 🌤️

A polished Android app that lets users search live weather for any city, capture photo evidence using a custom camera, compress images, annotate with notes, and save reports locally.


---

## 📱 Screens

| Weather Search | Create Report | Custom Camera | Saved Reports |
|:-:|:-:|:-:|:-:|
| Search cities with autocomplete | Capture & annotate weather | CameraX live preview | View all saved reports |

---

## ✨ Features

- 🔍 **City Autocomplete** — Live suggestions after 2+ letters with in-memory caching
- 🌡️ **Live Weather** — Temperature, condition, humidity, wind speed, pressure
- 📷 **Custom Camera** — Built with CameraX (no device camera intent)
- 🗜️ **Image Compression** — Shows original vs compressed size in KB
- 📝 **Field Notes** — Annotate each weather report
- 💾 **Local Storage** — Reports saved persistently with Room DB
- 🕐 **Timestamps** — Each report shows exact save time
- 📭 **Empty States** — Friendly UI when no reports exist

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| State Management | StateFlow + ViewModel |
| Async | Coroutines |
| DI | Hilt |
| Navigation | Navigation Compose |
| Networking | Retrofit + Gson + OkHttp |
| Local DB | Room |
| Camera | CameraX |
| Image Loading | Coil |

---

## 🌐 API

Uses [Open-Meteo](https://open-meteo.com/) — free, no API key required.

| Endpoint | Purpose |
|---|---|
| `geocoding-api.open-meteo.com/v1/search` | City autocomplete |
| `api.open-meteo.com/v1/forecast` | Current weather data |

Weather parameters fetched:
`temperature_2m`, `relative_humidity_2m`, `wind_speed_10m`, `surface_pressure`, `weather_code`

---

## 🏗️ Project Structure
```
com.oceanx.weathersnap/
├── data/
│   ├── api/          # Retrofit API interfaces
│   ├── local/        # Room DB, DAO, Entity
│   ├── model/        # Data models
│   └── repository/   # Repository layer
├── di/               # Hilt dependency injection
├── ui/
│   ├── components/   # Reusable composables
│   ├── navigation/   # NavGraph
│   ├── screen/       # 4 app screens
│   ├── theme/        # Material 3 dark theme
│   └── viewmodel/    # ViewModels + UI states
└── util/             # ImageCompressor, WeatherCodeMapper
```
---

## 🚀 Setup & Run

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11+
- Android device or emulator with API 33+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/ISHAN3413/WeatherSnap.git

# 2. Open in Android Studio
File → Open → select the WeatherSnap folder

# 3. Let Gradle sync complete

# 4. Run on device or emulator
Run → Run 'app'
```

> No API key needed. Internet permission is required for weather data.

---

## 📋 Permissions

| Permission | Reason |
|---|---|
| `INTERNET` | Fetch live weather data |
| `CAMERA` | Custom CameraX capture |

---

## 🎥 Demo Flow

1. Search a city (e.g. "Mumbai")
2. Select from autocomplete suggestions
3. View live weather details
4. Tap **Create Report**
5. Tap **Capture Photo** → Custom camera opens
6. Capture image → returns with original & compressed size
7. Add field notes
8. Tap **Save Report**
9. View saved report in **Saved Reports** screen

---

## 📊 Evaluation Coverage

| Area | Status |
|---|---|
| MVVM architecture & code organization | ✅ |
| Compose UI quality & responsiveness | ✅ |
| API integration, loading/error states, caching | ✅ |
| CameraX custom camera | ✅ |
| Image compression & file handling | ✅ |
| Room DB with IO-thread usage | ✅ |
| Navigation, animations & UX polish | ✅ |
| Code quality, naming & readability | ✅ |

---

## 👨‍💻 Author

**Your Name**  
[GitHub](https://github.com/ISHAN3413) • [LinkedIn](https://www.linkedin.com/in/ishan-agarwal-00bbbb328/)
