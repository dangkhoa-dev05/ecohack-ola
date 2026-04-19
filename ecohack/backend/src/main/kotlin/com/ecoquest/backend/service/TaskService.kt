package com.ecoquest.backend.service

import com.ecoquest.backend.dto.TaskDto
import com.ecoquest.backend.model.Task
import com.ecoquest.backend.model.TaskCategory
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class TaskService {

    private val tasks = listOf(
        Task(
            id = "task_001",
            title = "Nhặt rác công viên",
            description = "Thu gom rác tại công viên gần bạn và chụp ảnh kết quả",
            rewardCredits = 50,
            latitude = 10.7769,
            longitude = 106.7009,
            category = TaskCategory.CLEANUP
        ),
        Task(
            id = "task_002",
            title = "Trồng cây xanh",
            description = "Trồng ít nhất 1 cây xanh tại khu vực được chỉ định",
            rewardCredits = 100,
            latitude = 10.7800,
            longitude = 106.6950,
            category = TaskCategory.PLANTING
        ),
        Task(
            id = "task_003",
            title = "Phân loại rác tái chế",
            description = "Phân loại và mang rác tái chế đến điểm thu gom",
            rewardCredits = 30,
            latitude = 10.7730,
            longitude = 106.7050,
            category = TaskCategory.RECYCLING
        ),
        Task(
            id = "task_004",
            title = "Dọn dẹp bãi biển",
            description = "Tham gia dọn dẹp bãi biển cùng cộng đồng",
            rewardCredits = 80,
            latitude = 10.3460,
            longitude = 107.0843,
            category = TaskCategory.CLEANUP
        )
    )

    fun getDailyTasks(): List<TaskDto> = tasks.map { it.toDto() }

    fun getNearbyTasks(lat: Double, lng: Double): List<TaskDto> =
        tasks.map { task ->
            val dist = haversineKm(lat, lng, task.latitude, task.longitude)
            task.toDto(distanceKm = Math.round(dist * 10.0) / 10.0)
        }.sortedBy { it.distanceKm }

    fun getTaskById(id: String): TaskDto? =
        tasks.find { it.id == id }?.toDto()

    private fun Task.toDto(distanceKm: Double? = null) = TaskDto(
        id = id,
        title = title,
        description = description,
        rewardCredits = rewardCredits,
        latitude = latitude,
        longitude = longitude,
        category = category.name,
        distanceKm = distanceKm
    )

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
