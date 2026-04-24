# EcoQuest

EcoQuest is a gamified eco-action platform that encourages users to complete real-world environmental tasks — such as planting trees, recycling, and cleaning up — by submitting photo proof through a mobile app. Submissions are verified using Azure AI Vision and rewarded with credits and streaks.

---

## Repository Structure

```
ecohack-ola/
├── ecohack/
│   ├── backend/          # Spring Boot REST API (Kotlin)
│   └── mobile-android/   # Android app (Kotlin + Jetpack Compose)
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Mobile | Kotlin, Jetpack Compose, Retrofit, OkHttp |
| Backend | Kotlin, Spring Boot 3, Spring Security, JWT |
| Storage | Azure Blob Storage (SAS-based direct upload) |
| AI Vision | Azure AI Vision (image verification) |
| AI Chat | Azure OpenAI (EcoBot assistant) |

---

## Features

- **Task feed** — Daily and nearby eco tasks with location awareness
- **Photo proof submission** — Camera or gallery pick, direct upload to Azure Blob via SAS URL
- **AI verification** — Azure Vision analyzes submitted photos against task requirements
- **Rewards** — Credits and streaks awarded on approved submissions
- **EcoBot** — In-app chat assistant powered by Azure OpenAI
- **Leaderboard** — Rankings by credits earned
- **Submission history** — Track past submissions and their status

---

## Prerequisites

- JDK 21 (bundled with Android Studio at `C:\Program Files\Android\Android Studio\jbr`)
- Android Studio (Ladybug or newer)
- Azure account with:
  - Blob Storage account + container named `task-images`
  - (Optional) Azure AI Vision resource
  - (Optional) Azure OpenAI resource

---

## Backend Setup

### 1. Configure environment variables

Copy the example config:

```
ecohack/backend/src/main/resources/application.properties.example
→ ecohack/backend/.env
```

Fill in the required values:

```env
# Required
AZURE_STORAGE_CONNECTION_STRING=DefaultEndpointsProtocol=https;AccountName=...;AccountKey=...;EndpointSuffix=core.windows.net

# Optional — leave blank to use mock fallbacks
AZURE_VISION_ENDPOINT=
AZURE_VISION_KEY=
AZURE_OPENAI_ENDPOINT=
AZURE_OPENAI_KEY=
JWT_SECRET=
```

> **Note:** `.env` is gitignored. Never commit real credentials.

### 2. Run the backend

```powershell
# Windows PowerShell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:AZURE_STORAGE_CONNECTION_STRING="<your-connection-string>"

cd ecohack/backend
.\gradlew.bat bootRun
```

The server starts on **http://localhost:8081**

### 3. Verify it's running

```
GET http://localhost:8081/actuator/health
→ {"status":"UP"}
```

### Demo accounts

| Email | Password |
|-------|----------|
| `demo@ecoquest.app` | `pass123` |
| `admin@ecoquest.app` | `admin123` |

---

## Android App Setup

### 1. Open project in Android Studio

Open the `ecohack/mobile-android` folder in Android Studio and wait for Gradle sync to complete.

### 2. Configure backend URL

The base URL is set in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://<your-machine-ip>:8081/\"")
```

- **Emulator**: use `http://10.0.2.2:8081/` (auto-configured)
- **Physical device**: replace with your machine's local IP (run `ipconfig` to find it); device and machine must be on the same Wi-Fi network

### 3. Run the app

Connect a device or start an emulator, then click **Run ▶** in Android Studio.

---

## Submission Flow

```
1. User selects a task → Task Detail screen
2. Taps "Submit Proof" → opens Submit Proof screen
3. Takes or picks a photo
4. App calls POST /submissions/init → receives a SAS upload URL from the backend
5. App PUTs the image bytes directly to Azure Blob Storage via the SAS URL
6. App calls POST /submissions/{id}/complete → backend verifies with Azure Vision
7. Result (APPROVED / REJECTED) is returned with credits awarded
8. App auto-navigates back to Tasks; submitted task button is disabled
```

---

## API Overview

All endpoints except `/auth/**` require a `Bearer <JWT>` header.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/login` | Login, returns JWT token |
| `GET` | `/me` | Current user profile |
| `GET` | `/me/stats` | Credits, streak, level |
| `GET` | `/tasks/daily` | Daily task list |
| `GET` | `/tasks/nearby?lat=&lng=` | Nearby tasks |
| `GET` | `/tasks/{id}` | Task detail |
| `POST` | `/submissions/init` | Create submission, returns SAS upload URL |
| `POST` | `/submissions/{id}/complete` | Finalize submission after photo upload |
| `GET` | `/submissions` | List user's submissions |
| `GET` | `/submissions/{id}` | Single submission detail |
| `POST` | `/assistant/chat` | Chat with EcoBot |
| `GET` | `/api/v1/leaderboard` | Leaderboard rankings |

---

## Development Notes

- Azure credentials are optional — all Azure services fall back to mocks when keys are not set, so local development works without an Azure account.
- Backend port is **8081** (8080 is reserved by other system services on development machines).
- Submissions are stored in-memory; there is no database. Data resets on each backend restart.
