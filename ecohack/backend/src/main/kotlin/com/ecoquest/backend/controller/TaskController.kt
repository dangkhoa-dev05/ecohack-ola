package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.TaskDto
import com.ecoquest.backend.service.TaskService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @GetMapping("/daily")
    fun getDailyTasks(): ApiResponse<List<TaskDto>> {
        return ApiResponse.success(taskService.getDailyTasks())
    }

    @GetMapping("/nearby")
    fun getNearbyTasks(
        @RequestParam lat: Double,
        @RequestParam lng: Double
    ): ApiResponse<List<TaskDto>> {
        return ApiResponse.success(taskService.getNearbyTasks(lat, lng))
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: String): ApiResponse<TaskDto> {
        val task = taskService.getTaskById(id)
            ?: return ApiResponse.error("Task not found")
        return ApiResponse.success(task)
    }
}
