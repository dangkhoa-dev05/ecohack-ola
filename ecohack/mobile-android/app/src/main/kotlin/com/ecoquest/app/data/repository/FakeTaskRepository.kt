package com.ecoquest.app.data.repository

import com.ecoquest.app.data.model.SubmissionDto
import com.ecoquest.app.data.model.TaskDto

class FakeTaskRepository : TaskRepository {
    companion object {
        private val tasks = listOf(
            TaskDto(
                id = "task_001",
                title = "Pick Up Litter",
                description = "Collect trash at a nearby park and take a photo of the result",
                rewardCredits = 50,
                latitude = 10.7769,
                longitude = 106.7009,
                category = "CLEANUP"
            ),
            TaskDto(
                id = "task_002",
                title = "Plant a Tree",
                description = "Plant at least one tree in a designated area",
                rewardCredits = 100,
                latitude = 10.7800,
                longitude = 106.6950,
                category = "PLANTING"
            ),
            TaskDto(
                id = "task_003",
                title = "Sort Recyclables",
                description = "Sort and bring recyclable waste to a collection point",
                rewardCredits = 30,
                latitude = 10.7730,
                longitude = 106.7050,
                category = "RECYCLING"
            ),
            TaskDto(
                id = "task_004",
                title = "Beach Cleanup",
                description = "Join the community beach cleanup event",
                rewardCredits = 80,
                latitude = 10.3460,
                longitude = 107.0843,
                category = "CLEANUP"
            )
        )
    }

    override suspend fun getTasks(
        latitude: Double?,
        longitude: Double?
    ): List<TaskDto> = tasks

    override suspend fun submitTask(task: TaskDto, imageUrl: String?): SubmissionDto {
        return when {
            imageUrl.isNullOrBlank() -> SubmissionDto(
                id = "fake_${task.id}",
                taskId = task.id,
                status = "REJECTED",
                rewardCredits = 0,
                rejectionReason = "MISSING_IMAGE"
            )

            else -> SubmissionDto(
                id = "fake_${task.id}",
                taskId = task.id,
                status = "APPROVED",
                rewardCredits = task.rewardCredits
            )
        }
    }
}

