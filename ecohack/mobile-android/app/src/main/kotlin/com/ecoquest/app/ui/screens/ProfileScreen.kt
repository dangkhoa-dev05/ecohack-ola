package com.ecoquest.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.data.model.User
import com.ecoquest.app.data.model.xpRequiredForNextLevel
import com.ecoquest.app.data.repository.CompletedTask
import com.ecoquest.app.ui.components.AnimatedFlowerDecor
import com.ecoquest.app.ui.components.AnimatedLeafDecor
import com.ecoquest.app.ui.components.AnimatedNatureBackdrop
import com.ecoquest.app.ui.components.AnimatedStarDecor
import com.ecoquest.app.ui.components.NatureBackdropStyle
import com.ecoquest.app.ui.components.OrbitSparkRing
import com.ecoquest.app.ui.theme.EcoQuestTheme
import com.ecoquest.app.ui.viewmodel.ProfileViewModel
import java.util.concurrent.TimeUnit

private val ProfileBackground = Color(0xFFEAF4EA)
private val ProfileSurfaceLow = Color(0xFFF8FCF4)
private val ProfileSurface = Color(0xFFFFFFFF)
private val ProfileSurfaceHigh = Color(0xFFF4F9EC)
private val ProfileSurfaceHighest = Color(0xFFECF4E2)
private val ProfileTextPrimary = Color(0xFF1F3D27)
private val ProfileTextMuted = Color(0xFF6A7F69)
private val ProfileOutline = Color(0xFFD8E4CE)
private val ProfilePrimary = Color(0xFF6F943C)
private val ProfilePrimaryBright = Color(0xFF8AB351)
private val ProfilePrimaryDim = Color(0xFF5D842B)
private val ProfileTertiary = Color(0xFF4B8F6E)
private val ProfileError = Color(0xFFC74E4E)

private data class RecentTask(
    val title: String,
    val subtitle: String,
    val reward: String,
    val accent: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val tasks = uiState.recentTasks.map { it.toRecentTask() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBackground)
    ) {
        ProfileBackgroundDecor()

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ProfilePrimaryBright
                )
            }

            user == null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error ?: "No profile available.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ProfileTextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadProfile() }) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    ProfileHero(user = user)
                    ProfileStatsGrid(user = user)
                    RecentTasksSection(tasks)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileBackgroundDecor() {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedNatureBackdrop(style = NatureBackdropStyle.EcoBot)
        AnimatedLeafDecor(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 92.dp),
            size = 132.dp,
            alpha = 0.20f,
            delayMs = 400
        )
        AnimatedFlowerDecor(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 124.dp, end = 12.dp),
            size = 104.dp,
            alpha = 0.18f,
            delayMs = 600
        )
        OrbitSparkRing(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 156.dp, end = 28.dp),
            radius = 44.dp,
            color = ProfilePrimaryBright.copy(alpha = 0.75f)
        )
        AnimatedStarDecor(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 26.dp, bottom = 140.dp),
            size = 42.dp,
            alpha = 0.26f,
            delayMs = 900
        )
    }
}

@Composable
private fun ProfileHero(user: User) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(132.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(ProfilePrimaryBright.copy(alpha = 0.22f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(ProfileSurface)
                    .border(width = 4.dp, color = Color(0xFF7FA94A), shape = CircleShape)
                    .padding(6.dp)
                    .background(ProfileSurface, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF89B057), Color(0xFF6E9343))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.displayName.initials(),
                    color = ProfileTextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = user.displayName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = ProfileTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Eco profile with your live progress, streak energy, and recent wins.",
            style = MaterialTheme.typography.bodyMedium,
            color = ProfileTextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileHeroBadge(text = "${user.credits} credits")
            ProfileHeroBadge(text = "${user.streak} day streak")
        }
    }
}

@Composable
private fun ProfileHeroBadge(text: String) {
    Text(
        text = text,
        color = ProfileTextPrimary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(ProfileSurfaceLow.copy(alpha = 0.92f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileStatsGrid(user: User) {
    val xpCurrent = user.xp
    val xpTarget = xpRequiredForNextLevel(user.level)
    val progress = (xpCurrent.toFloat() / xpTarget.toFloat()).coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = ProfileSurfaceLow),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "CURRENT LEVEL",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfileTextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Lv. ${user.level}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = ProfileTextPrimary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "$xpCurrent / $xpTarget XP",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProfilePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(ProfileSurfaceHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(ProfilePrimary, ProfilePrimaryBright)
                                    )
                                )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ProfilePrimary.copy(alpha = 0.10f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(88.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SquareStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Eco,
                iconTint = ProfileTertiary,
                value = user.credits.toString(),
                label = "Bio Credits"
            )
            SquareStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                iconTint = ProfileError,
                value = "${user.streak} Days",
                label = "Sproutling Streak"
            )
        }
    }
}

private fun String.initials(): String {
    return trim()
        .split("\\s+".toRegex())
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "U" }
}

@Composable
private fun SquareStatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ProfileSurfaceHighest),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(30.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = ProfileTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ProfileTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RecentTasksSection(tasks: List<RecentTask>) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(
            text = "Recent Completed Tasks",
            style = MaterialTheme.typography.titleLarge,
            color = ProfileTextPrimary,
            fontWeight = FontWeight.Bold
        )

        if (tasks.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ProfileSurfaceLow),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Complete a daily task to see it here.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ProfileTextMuted
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier.width(56.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(ProfileSurfaceHighest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = task.icon,
                                        contentDescription = null,
                                        tint = task.accent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = ProfileSurfaceLow),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = ProfileTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = task.reward,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = ProfilePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = task.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ProfileTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun CompletedTask.toRecentTask(): RecentTask {
    return RecentTask(
        title = title,
        subtitle = "${categoryLabel(category)} • ${relativeCompletedTime(completedAtMillis)}",
        reward = "+$rewardCredits",
        accent = ProfilePrimary,
        icon = categoryIcon(category)
    )
}

private fun categoryIcon(category: String) = when (category.uppercase()) {
    "CLEANUP" -> Icons.Default.DeleteOutline
    "PLANTING" -> Icons.Default.Nature
    "RECYCLING" -> Icons.Default.Eco
    "ENERGY" -> Icons.Default.ElectricBolt
    else -> Icons.Default.TaskAlt
}

private fun categoryLabel(category: String): String {
    return category.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private fun relativeCompletedTime(completedAtMillis: Long): String {
    val elapsedMillis = (System.currentTimeMillis() - completedAtMillis).coerceAtLeast(0L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
    val days = TimeUnit.MILLISECONDS.toDays(elapsedMillis)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        else -> "${days}d ago"
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    EcoQuestTheme {
        ProfileScreen()
    }
}
