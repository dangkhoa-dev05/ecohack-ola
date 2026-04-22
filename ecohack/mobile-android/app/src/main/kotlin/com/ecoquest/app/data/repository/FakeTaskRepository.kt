package com.ecoquest.app.data.repository

import com.ecoquest.app.data.model.SubmissionDto
import com.ecoquest.app.data.model.TaskDto

class FakeTaskRepository : TaskRepository {
    companion object {
        private val tasks = listOf(
            TaskDto(
                id = "task_001",
                title = "Nhặt rác công viên",
                description = "Thu gom rác tại công viên gần bạn và chụp ảnh kết quả",
                rewardCredits = 50,
                latitude = 10.7769,
                longitude = 106.7009,
                category = "CLEANUP"
            ),
            TaskDto(
                id = "task_002",
                title = "Trồng cây xanh",
                description = "Trồng ít nhất 1 cây xanh tại khu vực được chỉ định",
                rewardCredits = 100,
                latitude = 10.7800,
                longitude = 106.6950,
                category = "PLANTING"
            ),
            TaskDto(
                id = "task_003",
                title = "Phân loại rác tái chế",
                description = "Phân loại và mang rác tái chế đến điểm thu gom",
                rewardCredits = 30,
                latitude = 10.7730,
                longitude = 106.7050,
                category = "RECYCLING"
            ),
            TaskDto(
                id = "task_004",
                title = "Dọn dẹp bãi biển",
                description = "Tham gia dọn dẹp bãi biển cùng cộng đồng",
                rewardCredits = 80,
                latitude = 10.3460,
                longitude = 107.0843,
                category = "CLEANUP"
            )
        )
    }

    override suspend fun getDailyTasks(): List<TaskDto> = tasks

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


