package com.ecoquest.backend.common

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(true, data, null)
        fun <T> error(msg: String): ApiResponse<T> = ApiResponse(false, null, msg)
    }
}
