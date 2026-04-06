//package com.ecoquest.backend.controller
//
//import com.ecoquest.backend.common.ApiResponse
//import com.ecoquest.backend.dto.LeaderboardDTO
//import com.ecoquest.backend.dto.StatsDto
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//class RewardController {
//
//    @GetMapping("/me/stats")
//    fun getStats(): ApiResponse<StatsDto> {
//        return ApiResponse.success(
//            StatsDto(
//                level = 3,
//                credits = 250,
//                streak = 5,
//                tasksCompleted = 12
//            )
//        )
//    }
//
//    @GetMapping("/leaderboard")
//    fun getLeaderboard(): ApiResponse<List<LeaderboardDTO>> {
//        return ApiResponse.success(
//            listOf(
//                LeaderboardDTO(rank = 1, displayName = "GreenHero", credits = 1200, level = 8),
//                LeaderboardDTO(rank = 2, displayName = "EcoWarrior", credits = 950, level = 6),
//                LeaderboardDTO(rank = 3, displayName = "TreePlanter", credits = 800, level = 5),
//                LeaderboardDTO(rank = 4, displayName = "OceanGuard", credits = 620, level = 4),
//                LeaderboardDTO(rank = 5, displayName = "RecycleKing", credits = 450, level = 3)
//            )
//        )
//    }
//}
