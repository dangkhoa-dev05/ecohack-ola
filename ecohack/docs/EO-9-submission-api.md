# EO-9 — Submission API Skeleton

## What was done

Refactored the submission flow from a single overloaded controller into a proper layered architecture, and added a submission history endpoint.

## New files

| File | Role |
|------|------|
| `BlobStorageService.kt` | Generates pre-signed upload URLs for photo submissions |
| `SubmissionService.kt` | Owns all submission business logic (init, complete, get, list) |

## Changed files

| File | Change |
|------|--------|
| `SubmissionController.kt` | Delegates to `SubmissionService`; added `GET /submissions` |
| `SubmissionDtos.kt` | Added `uploadUrlExpiresAt`, `createdAt`, `updatedAt`, `SubmissionSummaryDto` |
| `SubmissionVerificationService.kt` | Simplified; mock logic preserved |
| `application.properties` | Added commented Azure config stubs |
| `submission.json` | Updated mock responses to match new shapes |
| `Models.kt` (Android) | Synced with new backend fields |
| `EcoQuestApi.kt` (Android) | Added `getSubmission` and `listSubmissions` calls |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/submissions/init` | Create submission, returns upload URL |
| `POST` | `/submissions/{id}/complete` | Verify photo, returns APPROVED or REJECTED |
| `GET` | `/submissions/{id}` | Get single submission by ID |
| `GET` | `/submissions?userId=user_001` | List submission history for a user |

## Submission lifecycle

```
POST /init
  → status: PENDING_UPLOAD
  → returns uploadUrl + uploadUrlExpiresAt (15 min TTL)

POST /{id}/complete
  → VerificationService checks: image present? location present?
  → APPROVED  → credits granted via RewardService
  → REJECTED  → rejectionReason returned
```

## Response shapes

**POST /submissions/init**
```json
{
  "success": true,
  "data": {
    "submissionId": "sub_1713430000000",
    "uploadUrl": "https://ecoquestblob.../sub_1713430000000.jpg?sv=mock-sas-token",
    "uploadUrlExpiresAt": "2026-04-18T10:15:00Z"
  }
}
```

**POST /submissions/{id}/complete — approved**
```json
{
  "success": true,
  "data": {
    "id": "sub_1713430000000",
    "taskId": "task_001",
    "status": "APPROVED",
    "rewardCredits": 50,
    "rejectionReason": null,
    "createdAt": "2026-04-18T10:00:00Z",
    "updatedAt": "2026-04-18T10:01:30Z"
  }
}
```

**POST /submissions/{id}/complete — rejected**
```json
{
  "success": true,
  "data": {
    "status": "REJECTED",
    "rewardCredits": 0,
    "rejectionReason": "MISSING_IMAGE"
  }
}
```

**GET /submissions?userId=user_001**
```json
{
  "success": true,
  "data": [
    { "id": "sub_001", "taskId": "task_001", "status": "APPROVED", "rewardCredits": 50, "createdAt": "..." },
    { "id": "sub_002", "taskId": "task_003", "status": "REJECTED", "rewardCredits": 0,  "createdAt": "..." }
  ]
}
```

## Rejection reasons

| Reason | Meaning |
|--------|---------|
| `MISSING_IMAGE` | `imageUrl` was null or blank |
| `MISSING_LOCATION` | `latitude` or `longitude` was null |

## Task reward table

| Task ID | Credits |
|---------|---------|
| task_001 | 50 |
| task_002 | 100 |
| task_003 | 30 |
| task_004 | 80 |

## Day 3+ integration points

- **`BlobStorageService.generateUploadUrl()`** — replace mock URL with real Azure Blob SAS token
- **`SubmissionVerificationService.verify()`** — replace null checks with Azure AI Vision call
- **`SubmissionService.store`** — replace `HashMap` with Cosmos DB repository
- **`application.properties`** — uncomment and fill in Azure credentials
