package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.ChatRequest
import com.ecoquest.backend.dto.ChatResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/assistant")
class AssistantController {

    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ApiResponse<ChatResponse> {
        return ApiResponse.success(
            ChatResponse(
                reply = "Great question! Here's an eco-tip: " +
                    "Reducing single-use plastics is one of the easiest ways to help the environment. " +
                    "Try bringing a reusable bag and water bottle when you go out!"
            )
        )
    }
}
