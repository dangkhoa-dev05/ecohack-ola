package com.ecoquest.app.data.api

import com.ecoquest.app.data.model.*
import retrofit2.http.*

interface EcoQuestApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("me")
    suspend fun getMe(): ApiResponse<UserDto>

    @GET("tasks/daily")
    suspend fun getDailyTasks(): ApiResponse<List<TaskDto>>

    @GET("tasks/nearby")
    suspend fun getNearbyTasks(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): ApiResponse<List<TaskDto>>

    @POST("submissions/init")
    suspend fun initSubmission(@Body request: InitSubmissionRequest): ApiResponse<InitSubmissionResponse>

    @POST("submissions/{id}/complete")
    suspend fun completeSubmission(
        @Path("id") id: String,
        @Body request: CompleteSubmissionRequest
    ): ApiResponse<SubmissionDto>

    @GET("submissions/{id}")
    suspend fun getSubmission(@Path("id") id: String): ApiResponse<SubmissionDto>

    @GET("submissions")
    suspend fun listSubmissions(
        @Query("userId") userId: String = "user_001"
    ): ApiResponse<List<SubmissionSummaryDto>>

    @GET("me/stats")
    suspend fun getStats(): ApiResponse<StatsDto>

    @GET("api/v1/leaderboard")
    suspend fun getLeaderboard(): ApiResponse<List<LeaderboardEntryDto>>

    @POST("assistant/chat")
    suspend fun chat(@Body request: ChatRequest): ApiResponse<ChatResponse>
}
