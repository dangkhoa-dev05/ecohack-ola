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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.R
import com.ecoquest.app.ui.components.AnimatedNatureBackdrop
import com.ecoquest.app.ui.components.NatureBackdropStyle
import com.ecoquest.app.ui.viewmodel.ChatMessage
import com.ecoquest.app.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Color(0xFFEAF4EA),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 22.sp)
                        Spacer(modifier = Modifier.size(8.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.chat_title),
                                color = Color(0xFF1F3D27),
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = stringResource(R.string.chat_subtitle),
                                color = Color(0xFF4A6A53),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEAF4EA))
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.chat_placeholder)) },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6E9343),
                            unfocusedBorderColor = Color(0xFFC8D8BE)
                        )
                    )
                    Button(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText.trim())
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D842B)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF4EA))
        ) {
            AnimatedNatureBackdrop(style = NatureBackdropStyle.EcoBot)

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    EcoBotHeroCard(messageCount = uiState.messages.size)
                }

                items(uiState.messages) { msg ->
                    ChatBubble(msg)
                }

                if (uiState.isLoading) {
                    item {
                        Text(
                            text = "EcoBot is typing...",
                            color = Color(0xFF5D842B),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EcoBotHeroCard(messageCount: Int) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFDFF8EA), Color(0xFFC7EFD9), Color(0xFFB3E4D5))
                    )
                )
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Eco assistant online",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D6A54)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ask for greener habits, recycling tips, or quick ways to earn more credits today.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F3D27)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChatHintChip(text = "$messageCount messages")
                ChatHintChip(text = "Instant tips")
                ChatHintChip(text = "Eco Q&A")
            }
        }
    }
}

@Composable
private fun ChatHintChip(text: String) {
    Text(
        text = text,
        color = Color(0xFF1F3D27),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isUser) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFD6F2E2),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "AI",
                            color = Color(0xFF2D6A54),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 6.dp,
                    bottomEnd = if (isUser) 6.dp else 18.dp
                ),
                color = if (isUser) Color(0xFF5D842B) else Color.White,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    color = if (isUser) Color.White else Color(0xFF234028)
                )
            }
        }
    }
}
