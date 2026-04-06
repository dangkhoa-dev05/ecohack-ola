package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.entities.Leaderboard
import com.ecoquest.backend.service.LeaderboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/leaderboard")
class LeaderboardController(private val leaderboardService: LeaderboardService) {

    @GetMapping
    fun getAllLeaderboard(): ResponseEntity<ApiResponse<List<Leaderboard>>> {
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = leaderboardService.getAllLeaderboard()
                , error = null
            )
        )
    }
    @GetMapping("top")

    fun getTopLeaderBoard(
        @RequestParam(defaultValue = "3") limit : Int
    ): ResponseEntity<ApiResponse<List<Leaderboard>>> {
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = leaderboardService.getTopLeaderboard(limit),
                error = null
            )
        )
    }

}