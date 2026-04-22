package com.ecoquest.app.data.repository

import com.ecoquest.app.data.api.RetrofitClient
import com.ecoquest.app.data.model.CompleteSubmissionRequest
import com.ecoquest.app.data.model.InitSubmissionRequest
import com.ecoquest.app.data.model.SubmissionDto
import com.ecoquest.app.data.model.TaskDto

class NetworkTaskRepository : TaskRepository {
    private val api = RetrofitClient.api

    override suspend fun getDailyTasks(): List<TaskDto> {
        val response = api.getDailyTasks()
        if (!response.success || response.data == null) {
            throw IllegalStateException(response.error ?: "Failed to load tasks")
        }

        return response.data
    }

    override suspend fun submitTask(task: TaskDto, imageUrl: String?): SubmissionDto {
        val initResponse = api.initSubmission(
            InitSubmissionRequest(
                taskId = task.id,
                latitude = task.latitude,
                longitude = task.longitude
            )
        )

        if (!initResponse.success || initResponse.data == null) {
            throw IllegalStateException(initResponse.error ?: "Failed to init submission")
        }

        val completeResponse = api.completeSubmission(
            id = initResponse.data.submissionId,
            request = CompleteSubmissionRequest(imageUrl = imageUrl)
        )

        if (!completeResponse.success || completeResponse.data == null) {
            throw IllegalStateException(completeResponse.error ?: "Submission failed")
        }

        return completeResponse.data
    }
}

