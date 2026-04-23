package com.ecoquest.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecoquest.app.R
import com.ecoquest.app.ui.locale.LanguageManager
import com.ecoquest.app.ui.theme.EcoDimens
import com.ecoquest.app.ui.theme.EcoGreen

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var selectedLanguage by remember {
        mutableStateOf(LanguageManager.getSelectedLanguage(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF4EA))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = EcoDimens.ScreenHorizontal, vertical = EcoDimens.ScreenVertical),
        verticalArrangement = Arrangement.spacedBy(EcoDimens.SectionGap)
    ) {
        // Header with emoji
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⚙️",
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D4739)
            )
        }

        // Language card with cute styling
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌐", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = stringResource(R.string.language_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D4739)
                    )
                }
                Text(
                    text = stringResource(R.string.settings_language_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A8C7E)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedLanguage == LanguageManager.ENGLISH,
                        onClick = {
                            selectedLanguage = LanguageManager.ENGLISH
                            LanguageManager.applyLanguage(context, LanguageManager.ENGLISH)
                        },
                        label = { Text("🇬🇧 " + stringResource(R.string.language_english)) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EcoGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedLanguage == LanguageManager.VIETNAMESE,
                        onClick = {
                            selectedLanguage = LanguageManager.VIETNAMESE
                            LanguageManager.applyLanguage(context, LanguageManager.VIETNAMESE)
                        },
                        label = { Text("🇻🇳 " + stringResource(R.string.language_vietnamese)) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EcoGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Cute footer message
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F9E7)),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💡", fontSize = 20.sp, modifier = Modifier.padding(end = 10.dp))
                Text(
                    text = "Keep making a difference! 🌱",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8B6914),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
