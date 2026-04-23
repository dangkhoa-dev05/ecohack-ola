package com.ecoquest.app.data.repository

import com.ecoquest.app.data.model.TaskDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CompletedTask(
    val taskId: String,
    val title: String,
    val category: String,
    val rewardCredits: Int,
    val completedAtMillis: Long = System.currentTimeMillis()
)

object TaskHistoryRepository {
    private const val MAX_HISTORY_ITEMS = 10

    private val _completedTasks = MutableStateFlow<List<CompletedTask>>(emptyList())
    val completedTasks: StateFlow<List<CompletedTask>> = _completedTasks.asStateFlow()

    fun recordCompletedTask(task: TaskDto, rewardCredits: Int) {
        val completedTask = CompletedTask(
            taskId = task.id,
            title = task.title,
            category = task.category,
            rewardCredits = rewardCredits
        )

        _completedTasks.value = buildList {
            add(completedTask)
            addAll(_completedTasks.value.filterNot { it.taskId == task.id })
        }.take(MAX_HISTORY_ITEMS)
    }
}