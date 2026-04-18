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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecoquest.app.ui.theme.EcoQuestTheme

private val ProfileBackground = Color(0xFF0B0F12)
private val ProfileSurfaceLow = Color(0xFF0F1418)
private val ProfileSurface = Color(0xFF151A1F)
private val ProfileSurfaceHigh = Color(0xFF1B2025)
private val ProfileSurfaceHighest = Color(0xFF21262C)
private val ProfileTextPrimary = Color(0xFFEBEEF4)
private val ProfileTextMuted = Color(0xFFA8ABB1)
private val ProfileOutline = Color(0xFF44484D)
private val ProfilePrimary = Color(0xFFA1FFC2)
private val ProfilePrimaryBright = Color(0xFF00FC9A)
private val ProfilePrimaryDim = Color(0xFF00EC90)
private val ProfileTertiary = Color(0xFF77DFFF)
private val ProfileError = Color(0xFFFF716C)

private data class RecentTask(
    val title: String,
    val subtitle: String,
    val reward: String,
    val accent: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun ProfileScreen() {
    val tasks = listOf(
        RecentTask(
            title = "Picked up 10 pieces of trash",
            subtitle = "Green Park, Downtown • 2h ago",
            reward = "+50",
            accent = ProfilePrimary,
            icon = Icons.Default.DeleteOutline
        ),
        RecentTask(
            title = "Planted a native sapling",
            subtitle = "Home Garden • Yesterday",
            reward = "+250",
            accent = ProfilePrimary,
            icon = Icons.Default.Nature
        ),
        RecentTask(
            title = "Switched to LED bulbs",
            subtitle = "Verified via Photo • 2 days ago",
            reward = "+100",
            accent = ProfilePrimary,
            icon = Icons.Default.ElectricBolt
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBackground)
    ) {
        ProfileBackgroundDecor()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            ProfileHero()
            ProfileStatsGrid()
            RecentTasksSection(tasks)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileBackgroundDecor() {
    Box(modifier = Modifier.fillMaxSize()) {
    }
}

@Composable
private fun ProfileHero() {
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
                    .border(width = 4.dp, color = Color(0xFF00EC90), shape = CircleShape)
                    .padding(6.dp)
                    .background(ProfileSurface, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF183529), Color(0xFF0F1418))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AR",
                    color = ProfileTextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Alex River",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = ProfileTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun ProfileStatsGrid() {
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
                                text = "Lv. 24",
                                style = MaterialTheme.typography.headlineLarge,
                                color = ProfileTextPrimary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "2,450 / 3,000 XP",
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
                                .fillMaxWidth(0.81f)
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
                    tint = ProfileTextPrimary.copy(alpha = 0.05f),
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
                value = "12,840",
                label = "Bio Credits"
            )
            SquareStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                iconTint = ProfileError,
                value = "14 Days",
                label = "Sproutling Streak"
            )
        }
    }
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

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    EcoQuestTheme {
        ProfileScreen()
    }
}
