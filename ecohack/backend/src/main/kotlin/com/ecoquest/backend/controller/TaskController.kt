package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.TaskDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
class TaskController {

    private val mockTasks = listOf(
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

    @GetMapping("/daily")
    fun getDailyTasks(): ApiResponse<List<TaskDto>> {
        return ApiResponse.success(mockTasks)
    }

    @GetMapping("/nearby")
    fun getNearbyTasks(
        @RequestParam lat: Double,
        @RequestParam lng: Double
    ): ApiResponse<List<TaskDto>> {
        return ApiResponse.success(mockTasks.take(3))
    }
}
