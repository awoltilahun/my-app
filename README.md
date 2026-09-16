# TechApp — Full-Stack Android + Spring Boot Project

A complete full-stack project combining a Spring Boot REST API backend with an Android app frontend, MySQL database, and Google AdMob integration.

---

## Project Structure

```
techapp/
├── database/
│   └── setup.sql                  ← MySQL setup script
│
├── backend/                       ← Spring Boot REST API (Java 17)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/techapp/demo/
│       │   ├── DemoApplication.java
│       │   ├── entity/
│       │   │   ├── TechTip.java
│       │   │   └── AppLink.java
│       │   ├── repository/
│       │   │   ├── TechTipRepository.java
│       │   │   └── AppLinkRepository.java
│       │   ├── service/
│       │   │   ├── TechTipService.java
│       │   │   └── AppLinkService.java
│       │   └── controller/
│       │       ├── TechTipController.java
│       │       └── AppLinkController.java
│       └── resources/
│           └── application.properties
│
└── android/                       ← Android App (Java)
    ├── build.gradle
    ├── settings.gradle
    ├── gradle.properties
    └── app/
        ├── build.gradle
        ├── proguard-rules.pro
        └── src/main/
            ├── AndroidManifest.xml
            ├── java/com/techapp/android/
            │   ├── model/
            │   │   ├── TechTip.java
            │   │   └── AppLink.java
            │   ├── api/
            │   │   ├── ApiService.java
            │   │   └── RetrofitClient.java
            │   ├── adapter/
            │   │   ├── TechTipAdapter.java
            │   │   └── AppLinkAdapter.java
            │   └── ui/
            │       ├── MainActivity.java
            │       ├── AboutActivity.java
            │       ├── PrivacyPolicyActivity.java
            │       └── ContactActivity.java
            └── res/
                ├── layout/
                │   ├── activity_main.xml
                │   ├── item_tech_tip.xml
                │   ├── item_app_link.xml
                │   ├── activity_about.xml
                │   ├── activity_privacy_policy.xml
                │   └── activity_contact.xml
                ├── drawable/
                │   ├── ic_video_placeholder.xml
                │   └── search_background.xml
                ├── menu/
                │   └── main_menu.xml
                └── values/
                    ├── strings.xml
                    ├── colors.xml
                    ├── themes.xml
                    └── styles.xml
```

---

## Step 1 — Database Setup

1. Install and start MySQL Server.
2. Open MySQL CLI or MySQL Workbench.
3. Run the setup script:

```bash
mysql -u root -p < database/setup.sql
```

This creates:
- Database: `myappdb`
- User: `myappuser` / Password: `mypassword`
- Tables: `tech_tips`, `app_links`
- Sample seed data

---

## Step 2 — Run the Backend

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL running with `myappdb` created

### Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Server starts on `http://localhost:8080`

### REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/techtips | Get all tech tips |
| POST | /api/techtips | Add new tech tip |
| GET | /api/techtips/search?keyword=X | Search tech tips |
| GET | /api/applinks | Get all app links |
| POST | /api/applinks | Add new app link |
| GET | /api/applinks/search?keyword=X | Search app links |

### Sample POST — Add Tech Tip
```json
POST http://localhost:8080/api/techtips
Content-Type: application/json

{
  "title": "Android Jetpack Guide",
  "description": "Master Android Jetpack components",
  "videoLink": "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
}
```

### Sample POST — Add App Link
```json
POST http://localhost:8080/api/applinks
Content-Type: application/json

{
  "name": "Spotify",
  "playstoreUrl": "https://play.google.com/store/apps/details?id=com.spotify.music"
}
```

---

## Step 3 — Build the Android App

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 34
- Physical device or emulator (API 24+)

### Steps

1. Open Android Studio → **Open an existing project** → select `techapp/android/`
2. Let Gradle sync finish.
3. Update `RetrofitClient.java` base URL:
   - **Emulator:** `http://10.0.2.2:8080/` (already set — localhost on host machine)
   - **Physical device:** Replace with your machine's local IP, e.g. `http://192.168.1.5:8080/`
4. Run the app on your device/emulator.

---

## AdMob Integration

### Ad Unit IDs Used

| Ad Type | Ad Unit ID |
|---------|-----------|
| Banner | `ca-app-pub-3940256099942544/6300978111` |
| Interstitial | `ca-app-pub-3940256099942544/1033173712` |
| Rewarded | `ca-app-pub-3940256099942544/5224354917` |
| App ID | `ca-app-pub-3940256099942544~3347511713` |

> These are Google's official test ad unit IDs. They show real test ads safely during development.
> Before publishing to the Play Store, replace them with your real ad unit IDs from the [AdMob Console](https://admob.google.com).

### Ad Placement
- **Banner ad** — bottom of the home feed (always visible)
- **Interstitial ad** — shown when user taps the settings menu (between navigation)
- **Rewarded ad** — triggered via "Unlock Bonus" menu item; user earns bonus content on completion

---

## Key Files Quick Reference

| File | Purpose |
|------|---------|
| `application.properties` | DB connection config (URL, username, password) |
| `RetrofitClient.java` | Change `BASE_URL` to match your server IP |
| `AndroidManifest.xml` | AdMob App ID meta-data tag |
| `MainActivity.java` | All ad loading, RecyclerView, search bar, API calls |
| `TechTipAdapter.java` | Renders tech tips with YouTube thumbnail + play button |
| `AppLinkAdapter.java` | Renders app cards with Play Store button |
| `setup.sql` | Full database setup from scratch |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Cannot connect to backend on emulator | Use `10.0.2.2:8080` not `localhost:8080` |
| Cannot connect on physical device | Use your PC's local IP (e.g. `192.168.1.x`) |
| Ads not showing | Check internet permission in manifest; test ads require internet |
| MySQL connection refused | Ensure MySQL is running and credentials match `application.properties` |
| App crashes on launch | Check Logcat; ensure AdMob App ID is set in `AndroidManifest.xml` |
