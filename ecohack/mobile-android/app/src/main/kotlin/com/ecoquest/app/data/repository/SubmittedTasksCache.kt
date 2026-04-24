package com.ecoquest.app.data.repository

object SubmittedTasksCache {
    private val submittedIds = mutableSetOf<String>()

    fun markSubmitted(taskId: String) {
        submittedIds.add(taskId)
    }

    fun isSubmitted(taskId: String): Boolean = submittedIds.contains(taskId)
}
