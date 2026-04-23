package com.ecoquest.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.R
import com.ecoquest.app.data.model.LeaderboardEntryDto
import com.ecoquest.app.ui.components.AnimatedNatureBackdrop
import com.ecoquest.app.ui.components.NatureBackdropStyle
import com.ecoquest.app.ui.components.OneShotRankStarBurst
import com.ecoquest.app.ui.components.OneShotTop3ConfettiBurst
import com.ecoquest.app.ui.viewmodel.LeaderboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBurst by remember { mutableStateOf(true) }
    val canCelebrate = !uiState.isLoading && uiState.error == null && uiState.entries.size >= 3

    LaunchedEffect(Unit) { viewModel.loadLeaderboard() }

    Scaffold(
        containerColor = Color(0xFFEAF4EA),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆", fontSize = 24.sp)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.leaderboard_title),
                            color = Color(0xFF1F3D27),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEAF4EA))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF4EA))
        ) {
            AnimatedNatureBackdrop(style = NatureBackdropStyle.Rank)
            OneShotRankStarBurst(
                enabled = showBurst && canCelebrate
            )
            OneShotTop3ConfettiBurst(
                enabled = showBurst && canCelebrate,
                onFinished = { showBurst = false }
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadLeaderboard() }) {
                            Text(stringResource(R.string.common_retry))
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            LeaderboardSpotlight(
                                champion = uiState.entries.firstOrNull(),
                                totalCredits = uiState.entries.sumOf { it.credits }
                            )
                        }
                        itemsIndexed(uiState.entries) { index, entry ->
                            LeaderboardCard(position = index, entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardCard(position: Int, entry: LeaderboardEntryDto) {
    val badge = when (position) {
        0 -> "🥇"
        1 -> "🥈"
        2 -> "🥉"
        else -> "#${entry.rank}"
    }
    val containerColor = when (position) {
        0 -> Color(0xFFFFF5CF)
        1 -> Color(0xFFF7F8FC)
        2 -> Color(0xFFFFEFE1)
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (position < 3) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F7E6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badge)
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = entry.displayName,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF214029)
                    )
                    Text(
                        text = stringResource(R.string.level_format, entry.level),
                        color = Color(0xFF688068),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                text = "⭐ ${entry.credits}",
                color = Color(0xFF5D842B),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun LeaderboardSpotlight(
    champion: LeaderboardEntryDto?,
    totalCredits: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFF2C4), Color(0xFFFFE08C), Color(0xFFF3D46A))
                    )
                )
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Leaderboard spotlight",
                color = Color(0xFF7B5A00),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = champion?.let { "${it.displayName} is setting the pace at level ${it.level}." }
                    ?: "Top eco players are about to light this board up.",
                color = Color(0xFF3C2C00),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LeaderboardBadge(text = champion?.let { "#${it.rank}" } ?: "Waiting")
                LeaderboardBadge(text = champion?.let { "${it.credits} pts" } ?: "0 pts")
                LeaderboardBadge(text = "$totalCredits total")
            }
        }
    }
}

@Composable
private fun LeaderboardBadge(text: String) {
    Text(
        text = text,
        color = Color(0xFF3C2C00),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.72f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}
