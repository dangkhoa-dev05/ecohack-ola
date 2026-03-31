# EcoQuest

A gamified eco-task mobile app where users complete real-world environmental tasks (pick up trash, plant trees, recycle), submit photo + GPS proof, and earn credits, levels, and streaks. Verification is powered by Azure AI Vision; an AI assistant (Azure OpenAI) guides users along the way.

## Architecture

```
Android App (Kotlin / Jetpack Compose)
            |
     REST API (JSON)
            |
Spring Boot Backend (Kotlin)
            |
  ┌─────────┴──────────────────────┐
  │  Cosmos DB    -> data storage   │
  │  Blob Storage -> image uploads  │
  │  Service Bus  -> async verify   │
  │  Azure Vision -> image analysis │
  │  Azure OpenAI -> AI assistant   │
  └────────────────────────────────┘
```

> **Current phase (Day 1–2):** All endpoints return mock data. No Azure services are required to run the project.

## Prerequisites

| Tool              | Version   | Notes                                   |
|-------------------|-----------|-----------------------------------------|
| JDK               | 21        | Required by the backend                 |
| Gradle            | 9.4.1     | Backend wrapper downloads automatically |
| Android Studio    | Latest    | For the mobile project                  |
| Android SDK       | API 36    | `compileSdk` / `targetSdk`              |
| Git               | Any       |                                         |

## Repository Structure

```
ecohack/
├── backend/                  Spring Boot backend (Kotlin)
│   └── src/main/kotlin/com/ecoquest/backend/
│       ├── common/           ApiResponse wrapper
│       ├── config/           SecurityConfig
│       ├── controller/       REST controllers (mock)
│       └── dto/              Data transfer objects
├── mobile-android/           Android app (Kotlin / Compose)
│   └── app/src/main/kotlin/com/ecoquest/app/
│       ├── data/api/         Retrofit API interface + client
│       ├── data/model/       DTOs (mirrors backend)
│       ├── ui/theme/         Compose theme
│       ├── ui/screens/       Composable screens
│       ├── ui/viewmodel/     ViewModels
│       └── ui/navigation/    NavGraph
└── docs/
    ├── README.md             This file
    └── mock/                 Sample JSON responses
```

## Running the Backend

```bash
cd backend
./gradlew bootRun
```

The server starts on **http://localhost:8080**. Verify with:

```bash
curl http://localhost:8080/hello
# -> Hello EcoQuest
```

### API Endpoints

All responses follow the standard format:

```json
{
  "success": true,
  "data": { },
  "error": null
}
```

| Method | Endpoint                      | Description                 |
|--------|-------------------------------|-----------------------------|
| GET    | `/hello`                      | Health check                |
| POST   | `/auth/login`                 | Login (mock token + user)   |
| GET    | `/me`                         | Current user profile        |
| GET    | `/tasks/daily`                | List daily tasks            |
| GET    | `/tasks/nearby?lat=…&lng=…`   | List nearby tasks           |
| POST   | `/submissions/init`           | Start a submission          |
| POST   | `/submissions/{id}/complete`  | Complete a submission       |
| GET    | `/me/stats`                   | User stats                  |
| GET    | `/leaderboard`                | Top 5 leaderboard           |
| POST   | `/assistant/chat`             | AI assistant chat           |

### Example Requests

**Login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@eco.com","password":"pass123"}'
```

**Get daily tasks:**

```bash
curl http://localhost:8080/tasks/daily
```

**Init a submission:**

```bash
curl -X POST http://localhost:8080/submissions/init \
  -H "Content-Type: application/json" \
  -d '{"taskId":"task_001","latitude":10.7769,"longitude":106.7009}'
```

**Chat with assistant:**

```bash
curl -X POST http://localhost:8080/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"How can I help the environment?"}'
```

## Running the Android App

1. Open the `mobile-android/` folder in **Android Studio**.
2. Wait for Gradle sync to finish.
3. Make sure the backend is running on your machine.
4. Run the app on an emulator (API 26+).

> The app connects to `http://10.0.2.2:8080/` by default, which is the Android emulator alias for `localhost` on the host machine. If running on a physical device, update `BASE_URL` in `app/build.gradle.kts`.

## Mock Data

Sample JSON responses are available in `docs/mock/` for reference:

- `tasks.json` — daily tasks list
- `login.json` — login response with token + user
- `submission.json` — init and complete submission responses
- `stats.json` — user stats
- `leaderboard.json` — leaderboard entries
- `chat.json` — AI assistant response

## Cosmos DB Schema (planned)

| Container      | Partition Key | Fields                                      |
|----------------|---------------|---------------------------------------------|
| `users`        | `/id`         | id, displayName, level, credits, streak     |
| `tasks`        | `/id`         | id, title, description, reward, location    |
| `submissions`  | `/userId`     | id, userId, taskId, imageUrl, status        |
| `credit_ledger`| `/userId`     | userId, delta, reason, timestamp            |

## Azure Resources (planned for Day 3+)

| Service            | Resource Name        | Purpose              |
|--------------------|----------------------|----------------------|
| Blob Storage       | `task-images`        | Store submission photos |
| Cosmos DB          | `ecoquestdb`         | App data             |
| Service Bus        | `submission-verify`  | Async verification queue |
| Azure AI Vision    | —                    | Image analysis       |
| Azure OpenAI       | —                    | AI assistant / chat  |

## Team Workflow

- **Backend devs** — work in `backend/`, add real service integrations.
- **Android devs** — work in `mobile-android/`, build screens against mock API.
- **Nobody is blocked** — mock endpoints return valid data from Day 1.
