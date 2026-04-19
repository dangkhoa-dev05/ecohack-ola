package com.ecoquest.backend.service

import com.ecoquest.backend.dto.ChatResponse
import org.springframework.stereotype.Service

@Service
class ChatService {

    fun reply(message: String): ChatResponse {
        val msg = message.lowercase().trim()
        val response = when {
            // Greetings
            msg.matches(Regex(".*(hello|hi|hey|xin chào|chào).*")) ->
                "Hello! 🌱 I'm EcoBot, your eco-friendly assistant. Ask me about recycling, saving energy, reducing waste, or how to earn credits on EcoQuest!"

            // Recycling
            msg.contains("recycle") || msg.contains("recycling") || msg.contains("tái chế") ->
                "♻️ Great question about recycling!\n\n" +
                "• **Paper & cardboard** — flatten and keep dry\n" +
                "• **Plastic bottles** (1 & 2) — rinse and remove caps\n" +
                "• **Glass** — rinse, no broken glass in bins\n" +
                "• **Metal cans** — rinse, crush to save space\n\n" +
                "❌ Do NOT recycle: plastic bags, styrofoam, food-soiled items.\n\n" +
                "Tip: Complete a RECYCLING task on EcoQuest to earn credits! 🎯"

            // Plastic
            msg.contains("plastic") || msg.contains("nhựa") ->
                "🚫 Reducing plastic usage is one of the biggest impacts you can make!\n\n" +
                "• Bring reusable bags when shopping\n" +
                "• Use a refillable water bottle (saves ~150 bottles/year)\n" +
                "• Say no to plastic straws — try bamboo or metal ones\n" +
                "• Choose products with minimal packaging\n" +
                "• Avoid single-use cutlery — carry your own set\n\n" +
                "Did you know? 8 million tons of plastic enter oceans every year! 🌊"

            // Energy saving
            msg.contains("energy") || msg.contains("electricity") || msg.contains("điện") || msg.contains("năng lượng") ->
                "⚡ Tips to save energy at home:\n\n" +
                "• Switch to LED bulbs (use 75% less energy)\n" +
                "• Unplug devices when not in use (phantom load adds up!)\n" +
                "• Use natural light during the day\n" +
                "• Set AC to 26°C instead of lower\n" +
                "• Wash clothes in cold water\n" +
                "• Use a fan before turning on AC\n\n" +
                "Small changes = big savings on your electricity bill AND the planet! 💚"

            // Water
            msg.contains("water") || msg.contains("nước") ->
                "💧 Water conservation tips:\n\n" +
                "• Fix leaky faucets (1 drip/sec = 11,000 liters/year!)\n" +
                "• Take shorter showers (5 min saves 40 liters)\n" +
                "• Turn off tap while brushing teeth\n" +
                "• Collect rainwater for plants\n" +
                "• Run dishwasher/washing machine only when full\n" +
                "• Water plants in early morning or evening\n\n" +
                "Only 1% of Earth's water is drinkable. Every drop counts! 🌍"

            // Planting / trees
            msg.contains("plant") || msg.contains("tree") || msg.contains("garden") ||
            msg.contains("cây") || msg.contains("trồng") ->
                "🌳 Planting trees is amazing for the environment!\n\n" +
                "• 1 tree absorbs ~22kg of CO₂ per year\n" +
                "• Trees provide shade and reduce cooling costs by 25%\n" +
                "• They filter air pollutants and produce oxygen\n" +
                "• Start small: herbs on your balcony count too!\n\n" +
                "Easy plants to start with: basil, mint, spider plant, pothos.\n\n" +
                "Complete PLANTING tasks on EcoQuest to earn extra credits! 🎯"

            // Cleanup / trash
            msg.contains("cleanup") || msg.contains("clean up") || msg.contains("trash") ||
            msg.contains("litter") || msg.contains("rác") || msg.contains("dọn") ->
                "🧹 Community cleanups make a huge difference!\n\n" +
                "• Bring gloves and trash bags\n" +
                "• Separate recyclables from general waste\n" +
                "• Focus on waterways — trash near water often ends up in oceans\n" +
                "• Take before/after photos for your EcoQuest submissions! 📸\n" +
                "• Invite friends — more hands = more impact\n\n" +
                "Check out CLEANUP tasks nearby to earn credits! 🏆"

            // Credits / points / rewards
            msg.contains("credit") || msg.contains("point") || msg.contains("reward") ||
            msg.contains("điểm") || msg.contains("thưởng") ->
                "🏆 How to earn credits on EcoQuest:\n\n" +
                "• Complete daily tasks (+10 to +50 credits each)\n" +
                "• Submit photo proof of your eco-actions\n" +
                "• Maintain your daily streak for bonus rewards\n" +
                "• Try nearby tasks for location-based challenges\n\n" +
                "Credits help you level up and climb the leaderboard! 📊\n" +
                "Current categories: CLEANUP, PLANTING, RECYCLING"

            // Level / leaderboard
            msg.contains("level") || msg.contains("leaderboard") || msg.contains("rank") ||
            msg.contains("xếp hạng") ->
                "📊 EcoQuest Ranking System:\n\n" +
                "• Earn credits by completing eco-tasks\n" +
                "• Level up as you accumulate credits\n" +
                "• Compete with other eco-warriors on the leaderboard\n" +
                "• Keep your streak going for a higher rank!\n\n" +
                "Check the Leaderboard tab to see where you stand! 🥇"

            // Climate / environment / CO2
            msg.contains("climate") || msg.contains("environment") || msg.contains("co2") ||
            msg.contains("carbon") || msg.contains("khí hậu") || msg.contains("môi trường") ->
                "🌍 Climate action starts with YOU!\n\n" +
                "• Reduce, Reuse, Recycle (the 3 R's)\n" +
                "• Walk, bike, or use public transport when possible\n" +
                "• Eat more plant-based meals (livestock = 14.5% of emissions)\n" +
                "• Support local and seasonal products\n" +
                "• Reduce food waste — plan meals ahead\n\n" +
                "Average person's carbon footprint: ~4 tons CO₂/year.\n" +
                "Goal: reduce to under 2 tons by 2050! 🎯"

            // Food waste
            msg.contains("food") || msg.contains("waste") || msg.contains("compost") ||
            msg.contains("thực phẩm") || msg.contains("lãng phí") ->
                "🍎 Reducing food waste:\n\n" +
                "• Plan meals and make a shopping list\n" +
                "• Use FIFO: First In, First Out in your fridge\n" +
                "• Freeze leftovers before they spoil\n" +
                "• Compost food scraps (great for gardens!)\n" +
                "• 'Best before' ≠ 'expired' — use your senses\n\n" +
                "1/3 of all food produced globally is wasted. Let's change that! 💪"

            // Transport
            msg.contains("transport") || msg.contains("car") || msg.contains("bike") ||
            msg.contains("bus") || msg.contains("xe") || msg.contains("giao thông") ->
                "🚲 Eco-friendly transportation:\n\n" +
                "• Walk or bike for short distances\n" +
                "• Use public transport — buses emit 80% less CO₂ per passenger than cars\n" +
                "• Carpool with colleagues\n" +
                "• Consider electric vehicles for longer commutes\n" +
                "• Work from home when possible\n\n" +
                "Transport accounts for ~16% of global emissions. Every trip counts! 🌿"

            // How to use app
            msg.contains("how") || msg.contains("app") || msg.contains("use") ||
            msg.contains("cách") || msg.contains("sử dụng") || msg.contains("hướng dẫn") ->
                "📱 How to use EcoQuest:\n\n" +
                "1. **Home** — View your profile, stats, and streak\n" +
                "2. **Tasks** — Browse daily eco-challenges\n" +
                "3. **Task Detail** — See description, reward, and location\n" +
                "4. **Submit Proof** — Take a photo to complete a task\n" +
                "5. **Leaderboard** — See top eco-warriors\n" +
                "6. **EcoBot** (me!) — Ask eco-questions anytime\n\n" +
                "Start with a CLEANUP task — they're the easiest!"

            // Thank you
            msg.matches(Regex(".*(thank|thanks|cảm ơn|cám ơn).*")) ->
                "You're welcome! 😊🌱 Keep making eco-friendly choices — every small action adds up to a big impact. Feel free to ask me anything anytime!"

            // Default / unknown
            else ->
                "🌱 I'd love to help! Here are topics I know about:\n\n" +
                "• ♻️ **Recycling** — how to recycle properly\n" +
                "• 🚫 **Plastic reduction** — alternatives to single-use\n" +
                "• ⚡ **Energy saving** — reduce your electricity bill\n" +
                "• 💧 **Water conservation** — every drop counts\n" +
                "• 🌳 **Planting** — trees and indoor plants\n" +
                "• 🧹 **Cleanup** — community cleanup tips\n" +
                "• 🍎 **Food waste** — reduce and compost\n" +
                "• 🚲 **Transport** — eco-friendly commuting\n" +
                "• 🏆 **Credits & rewards** — how to earn points\n" +
                "• 📱 **App guide** — how to use EcoQuest\n\n" +
                "Just ask about any topic! 💚"
        }

        return ChatResponse(reply = response)
    }
}
