package com.ecoquest.backend.service

import com.ecoquest.backend.service.openai.AzureOpenAIClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * JIRA-423 — Assistant chat service.
 *
 * Tries Azure OpenAI first. If the client is not configured or the remote
 * call fails, falls back to a deterministic keyword-based canned reply so the
 * Android chat screen always gets a response.
 */
@Service
class ChatService(
    private val openAIClient: AzureOpenAIClient
) {
    private val log = LoggerFactory.getLogger(ChatService::class.java)

    fun getReply(message: String): String {
        val remote = try {
            openAIClient.chat(message)
        } catch (ex: Exception) {
            log.warn("Unexpected error calling Azure OpenAI, falling back. {}", ex.message)
            null
        }

        return remote ?: fallbackReply(message)
    }

    private fun fallbackReply(message: String): String {
        val lower = message.lowercase()
        return when {
            lower.containsAny("recycle", "recycling", "tái chế") ->
                "Recycling is a great way to reduce waste! Start by separating plastics, paper, and glass. " +
                "Look for local recycling drop-off points in your area."

            lower.containsAny("plant", "tree", "trồng cây", "cây xanh") ->
                "Planting trees is one of the most impactful things you can do! " +
                "A single tree can absorb up to 22kg of CO2 per year. Consider joining a local planting event."

            lower.containsAny("plastic", "nhựa", "bag", "túi") ->
                "Reducing single-use plastics is crucial! Try carrying a reusable bag, water bottle, and utensils. " +
                "Every piece of plastic avoided makes a difference."

            lower.containsAny("water", "nước", "save water", "tiết kiệm") ->
                "Water conservation matters! Take shorter showers, fix leaky faucets, and collect rainwater for plants. " +
                "Small changes add up to big impact."

            lower.containsAny("energy", "electricity", "điện", "năng lượng") ->
                "Save energy by switching to LED bulbs, unplugging devices when not in use, " +
                "and using natural light when possible. Every kilowatt counts!"

            lower.containsAny("clean", "cleanup", "dọn", "rác", "trash", "litter") ->
                "Community cleanups make a huge difference! Organize or join a local cleanup event. " +
                "Even picking up a few pieces of litter on your daily walk helps keep our environment clean."

            lower.containsAny("tip", "advice", "help", "suggest", "giúp", "gợi ý") ->
                "Here are some daily eco-tips: 1) Bring reusable bags when shopping, " +
                "2) Use public transport or bike, 3) Compost food scraps, " +
                "4) Support local eco-friendly businesses. Every action counts!"

            lower.containsAny("hello", "hi", "hey", "chào", "xin chào") ->
                "Hello! I'm your EcoQuest assistant. Ask me anything about helping the environment — " +
                "from recycling tips to energy saving ideas. How can I help you today?"

            else ->
                "Great question! Here's an eco-tip: Reducing single-use plastics is one of the easiest ways " +
                "to help the environment. Try bringing a reusable bag and water bottle when you go out! " +
                "Feel free to ask me about recycling, planting, energy saving, or community cleanups."
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it) }
}
