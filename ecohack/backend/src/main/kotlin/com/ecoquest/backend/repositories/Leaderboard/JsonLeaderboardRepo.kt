package com.ecoquest.backend.repositories.Leaderboard

import com.ecoquest.backend.dto.Leaderboard.LeaderboardResponse
import com.ecoquest.backend.entities.Leaderboard
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository

@Repository
class JsonLeaderboardRepo ( private val objectMapper: ObjectMapper ) : LeaderboardRepo {

    private val filePath = "./mock/leaderboard.json"
    override fun findAll(): List<Leaderboard> {
        val resource = ClassPathResource(filePath)

        resource.inputStream.use { inputStream ->
            val response : LeaderboardResponse =  objectMapper.readValue(
                inputStream,
                object : TypeReference<LeaderboardResponse>() {}
            )

            return response.data
        }
    }

    //NO NEED CURRENTLY ( IMPLEMENT LATER ON )
//    override fun findById(rank: Int): Leaderboard? {
//        return findAll().firstOrNull { it.rank == rank }
//    }
}