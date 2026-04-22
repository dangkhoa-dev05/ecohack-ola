package com.ecoquest.app.data.repository

import com.ecoquest.app.data.model.SubmissionDto
import com.ecoquest.app.data.model.TaskDto

interface TaskRepository {
    suspend fun getDailyTasks(): List<TaskDto>

    suspend fun submitTask(task: TaskDto, imageUrl: String?): SubmissionDto
}
