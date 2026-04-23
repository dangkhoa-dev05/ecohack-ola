package com.ecoquest.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.R
import com.ecoquest.app.ui.components.*
import com.ecoquest.app.ui.theme.EcoDimens
import com.ecoquest.app.data.model.LeaderboardEntryDto
import com.ecoquest.app.ui.theme.EcoGold
import com.ecoquest.app.ui.theme.EcoGreen
import com.ecoquest.app.ui.viewmodel.LeaderboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLeaderboard()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "leaderboard")
    val trophyFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_float"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🏆",
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .offset(y = trophyFloat.dp)
                                .scale(1f + (trophyFloat / 50))
                        )
                        Text(
                            text = stringResource(R.string.leaderboard_title),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F0E8))
        ) {
            // Background decorations
            FloatingSparkles(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFFFD54F),
                count = 5
            )
            AnimatedStarDecor(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 12.dp),
                size = 28.dp,
                alpha = 0.55f,
                delayMs = 0
            )
            AnimatedStarDecor(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp),
                size = 22.dp,
                alpha = 0.45f,
                delayMs = 400
            )
            AnimatedFlowerDecor(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 8.dp),
                size = 52.dp,
                alpha = 0.22f,
                delayMs = 300
            )
            AnimatedLeafDecor(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 80.dp),
                size = 80.dp,
                alpha = 0.16f,
                delayMs = 600
            )

            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        CircularProgressIndicator(color = EcoGreen)
                    }
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = uiState.error ?: stringResource(R.string.common_unknown_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadLeaderboard() },
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
                        ) {
                            Text(stringResource(R.string.common_retry))
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize(),
                        contentPadding = PaddingValues(
                            horizontal = EcoDimens.ScreenHorizontal,
                            vertical = EcoDimens.ScreenVertical
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(uiState.entries) { index, entry ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + slideInHorizontally(
                                    initialOffsetX = { -700 },
                                    animationSpec = spring(
                                        dampingRatio = 0.72f,
                                        stiffness = 140f
                                    )
                                ),
                                modifier = Modifier.animateItem()
                            ) {
                                LeaderboardCard(entry = entry, position = index, index = index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardCard(entry: LeaderboardEntryDto, position: Int, index: Int) {
    val isTopThree = position < 3
    val medalEmoji = when (position) {
        0 -> "🥇"
        1 -> "🥈"
        2 -> "🥉"
        else -> null
    }
    
    val medalBgColor = when (position) {
        0 -> Color(0xFFFFF8E1)
        1 -> Color(0xFFF5F5F5)
        2 -> Color(0xFFFFECE0)
        else -> Color.White
    }

    val infiniteTransition = rememberInfiniteTransition(label = "medal_$index")
    val medalScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200 + (index * 100), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "medal_scale_$index"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTopThree) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = medalBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Animated medal badge
            if (medalEmoji != null) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(medalScale)
                        .clip(CircleShape)
                        .background(
                            when (position) {
                                0 -> Color(0xFFFFD700)
                                1 -> Color(0xFFC0C0C0)
                                else -> Color(0xFFCD7F32)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(medalEmoji, fontSize = 28.sp)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8E8E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${entry.rank}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF666)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D4739)
                    )
                    if (position < 3) {
                        Text(
                            text = when (position) {
                                0 -> "👑"
                                1 -> "⭐"
                                else -> "🎖️"
                            },
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = stringResource(R.string.level_format, entry.level),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7A8C7E),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Credits badge with cute styling
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F0F0).copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${entry.credits}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = EcoGreen
                    )
                }
            }
        }
    }
}
