package com.ecoquest.app.data.api

import com.ecoquest.app.data.model.*
import retrofit2.http.*

interface EcoQuestApi {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("me")
    suspend fun getMe(): ApiResponse<UserDto>

    // Tasks
    @GET("tasks/daily")
    suspend fun getDailyTasks(): ApiResponse<List<TaskDto>>

    @GET("tasks/nearby")
    suspend fun getNearbyTasks(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): ApiResponse<List<TaskDto>>

    // Submissions
    @POST("submissions/init")
    suspend fun initSubmission(@Body request: InitSubmissionRequest): ApiResponse<InitSubmissionResponse>

    @POST("submissions/{id}/complete")
    suspend fun completeSubmission(
        @Path("id") id: String,
        @Body request: CompleteSubmissionRequest
    ): ApiResponse<SubmissionDto>

    // Rewards
    @GET("me/stats")
    suspend fun getStats(): ApiResponse<StatsDto>

    @GET("leaderboard")
    suspend fun getLeaderboard(): ApiResponse<List<LeaderboardEntryDto>>

    // AI Assistant
    @POST("assistant/chat")
    suspend fun chat(@Body request: ChatRequest): ApiResponse<ChatResponse>
}
