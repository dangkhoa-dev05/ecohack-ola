package com.ecoquest.app.ui.screens

import com.ecoquest.app.R
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.ui.components.*
import com.ecoquest.app.ui.theme.EcoGreen
import com.ecoquest.app.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "eco_home")
    val plantFloatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -14f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plant_float"
    )
    val plantScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plant_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F0E8))
    ) {
        // === FIXED BACKGROUND DECORATIONS (don't scroll) ===
        // Sparkles across whole screen
        FloatingSparkles(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFFFD54F),
            count = 6
        )

        // Corner leaf decorations (fixed)
        AnimatedLeafDecor(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 180.dp, start = 0.dp),
            size = 100.dp,
            alpha = 0.18f,
            delayMs = 300
        )
        AnimatedLeafDecor(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 0.dp),
            size = 90.dp,
            alpha = 0.16f,
            delayMs = 700
        )

        // Flower decorations (fixed)
        AnimatedFlowerDecor(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 200.dp, start = 12.dp),
            size = 52.dp,
            alpha = 0.28f,
            delayMs = 500
        )
        AnimatedFlowerDecor(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 200.dp, end = 8.dp),
            size = 44.dp,
            alpha = 0.26f,
            delayMs = 900
        )

        // Star sparkles (fixed)
        AnimatedStarDecor(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 290.dp, end = 20.dp),
            size = 26.dp,
            alpha = 0.50f,
            delayMs = 0
        )
        AnimatedStarDecor(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 160.dp, start = 28.dp),
            size = 20.dp,
            alpha = 0.45f,
            delayMs = 600
        )

        // === SCROLLABLE CONTENT ===
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedPlantPot(size = 100.dp)
                    Spacer(Modifier.height(12.dp))
                    CircularProgressIndicator(color = EcoGreen)
                }
            }
            uiState.error != null && uiState.user == null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌧️", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = uiState.error ?: stringResource(R.string.common_unknown_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadHome() }, colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)) {
                        Text(stringResource(R.string.common_retry))
                    }
                }
            }
            else -> {
                val user = uiState.user
                val stats = uiState.stats
                val tasksProgress = ((stats?.tasksCompleted ?: 0) / 5f).coerceIn(0f, 1f)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // ============ WAVE HEADER SECTION ============
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    ) {
                        // Wave canvas background
                        WaveBackground(
                            modifier = Modifier.fillMaxWidth(),
                            color = EcoGreen,
                            secondaryColor = Color(0xFF1B5E20),
                            height = 230.dp
                        )

                        // Cloud decoration on wave
                        AnimatedCloudDecor(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 30.dp),
                            size = 80.dp,
                            alpha = 0.20f,
                            delayMs = 0
                        )
                        AnimatedCloudDecor(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 30.dp, start = 60.dp),
                            size = 60.dp,
                            alpha = 0.15f,
                            delayMs = 1200
                        )

                        // Animated plant pot in wave (right side)
                        AnimatedPlantPot(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 0.dp, end = 12.dp),
                            size = 130.dp,
                            delayMs = 0
                        )

                        // Star sparkles on the wave
                        AnimatedStarDecor(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 18.dp, start = 20.dp),
                            size = 22.dp,
                            alpha = 0.7f,
                            delayMs = 200
                        )
                        AnimatedStarDecor(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 60.dp, end = 160.dp),
                            size = 16.dp,
                            alpha = 0.6f,
                            delayMs = 500
                        )
                        AnimatedStarDecor(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 160.dp),
                            size = 18.dp,
                            alpha = 0.55f,
                            delayMs = 800
                        )

                        // Greeting text on wave
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 22.dp, bottom = 28.dp)
                        ) {
                            Text(
                                text = "Hi ${user?.displayName ?: "EcoHero"}! 🌱",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.home_level_format, user?.level ?: 0),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // BottomWaveDivider for smooth cream transition
                    BottomWaveDivider(
                        color = Color(0xFFF5F0E8),
                        height = 36.dp
                    )

                    // ============ MAIN CONTENT ============
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // Progress card with floating tree
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                                Column(modifier = Modifier.align(Alignment.CenterStart).fillMaxWidth(0.65f)) {
                                    Text(
                                        text = "TODAY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFAAAAAA),
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "${stats?.tasksCompleted ?: 0} Tasks Done",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF2D4739)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    LinearProgressIndicator(
                                        progress = { tasksProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp)),
                                        color = EcoGreen,
                                        trackColor = EcoGreen.copy(alpha = 0.12f)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🔥", fontSize = 14.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "${stats?.streak ?: 0} day streak",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE64A19)
                                        )
                                    }
                                }
                                // Floating tree with animation (right side of card)
                                Text(
                                    text = "🌳",
                                    fontSize = 72.sp,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .offset(y = plantFloatY.dp)
                                        .scale(plantScale)
                                )
                            }
                        }

                        // Journey section header with sparkle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Your Journey",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2D4739)
                            )
                            Text("🌿", fontSize = 20.sp)
                            AnimatedStarDecor(size = 18.dp, alpha = 0.8f, delayMs = 100)
                        }

                        // Stat emoji cards 2x2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatEmojiCard(
                                modifier = Modifier.weight(1f),
                                emoji = "⭐",
                                value = "${stats?.credits ?: 0}",
                                label = stringResource(R.string.stat_credits),
                                bgColor = Color(0xFFFFF8E1)
                            )
                            StatEmojiCard(
                                modifier = Modifier.weight(1f),
                                emoji = "🔥",
                                value = "${stats?.streak ?: 0}d",
                                label = stringResource(R.string.stat_streak),
                                bgColor = Color(0xFFFFECE0)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatEmojiCard(
                                modifier = Modifier.weight(1f),
                                emoji = "✅",
                                value = "${stats?.tasksCompleted ?: 0}",
                                label = stringResource(R.string.stat_tasks_done),
                                bgColor = Color(0xFFEFF9EE)
                            )
                            StatEmojiCard(
                                modifier = Modifier.weight(1f),
                                emoji = "🏆",
                                value = "Lv.${stats?.level ?: 1}",
                                label = stringResource(R.string.stat_rank),
                                bgColor = Color(0xFFF0EEFF)
                            )
                        }

                        // Decorative quote card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💡", fontSize = 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Eco Tip of the Day",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D4739)
                                    )
                                    Text(
                                        text = "Every small action counts towards a greener planet! 🌍",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF5A7A60)
                                    )
                                }
                            }
                        }

                        // CTA Button with gradient
                        Button(
                            onClick = onNavigateToTasks,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                                        ),
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🌱", fontSize = 22.sp)
                                    Text(
                                        text = stringResource(R.string.action_view_daily_tasks),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text("✨", fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatEmojiCard(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    label: String,
    bgColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D4739)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9E9E9E),
                maxLines = 1
            )
        }
    }
}

