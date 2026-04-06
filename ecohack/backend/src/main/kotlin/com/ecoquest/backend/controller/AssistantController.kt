package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.ChatRequest
import com.ecoquest.backend.dto.ChatResponse
import com.ecoquest.backend.service.ChatService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/assistant")
class AssistantController(private val chatService: ChatService) {

    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ApiResponse<ChatResponse> {
        val reply = chatService.getReply(request.message)
        return ApiResponse.success(ChatResponse(reply = reply))
    }
}
