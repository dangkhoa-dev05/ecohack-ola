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
            title = "Pick Up Park Litter",
            description = "Collect trash at a nearby park and take a photo of the result",
            rewardCredits = 50,
            latitude = 10.7769,
            longitude = 106.7009,
            category = "CLEANUP"
        ),
        TaskDto(
            id = "task_002",
            title = "Plant a Tree",
            description = "Plant at least 1 tree at the designated area",
            rewardCredits = 100,
            latitude = 10.7800,
            longitude = 106.6950,
            category = "PLANTING"
        ),
        TaskDto(
            id = "task_003",
            title = "Sort Recyclable Waste",
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
