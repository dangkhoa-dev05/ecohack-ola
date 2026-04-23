package com.ecoquest.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecoquest.app.R
import com.ecoquest.app.ui.components.AnimatedCloudDecor
import com.ecoquest.app.ui.components.AnimatedFlowerDecor
import com.ecoquest.app.ui.components.AnimatedLeafDecor
import com.ecoquest.app.ui.components.AnimatedNatureBackdrop
import com.ecoquest.app.ui.components.AnimatedStarDecor
import com.ecoquest.app.ui.components.NatureBackdropStyle

@Composable
fun LoginScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF4EA))
    ) {
        AnimatedNatureBackdrop(
            style = NatureBackdropStyle.EcoBot,
            sparkleCount = 11
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(336.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF3E67E), Color(0xFFAAF07E), Color(0xFF61D58A))
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(336.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.30f),
                            Color.Transparent
                        ),
                        radius = 420f
                    )
                )
        )

        AnimatedLeafDecor(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 98.dp, start = 10.dp),
            size = 92.dp,
            alpha = 0.24f,
            delayMs = 300
        )
        AnimatedLeafDecor(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 112.dp, end = 8.dp),
            size = 84.dp,
            alpha = 0.22f,
            delayMs = 800
        )
        AnimatedCloudDecor(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 30.dp),
            size = 100.dp,
            alpha = 0.18f,
            delayMs = 400
        )
        AnimatedFlowerDecor(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 122.dp, start = 14.dp),
            size = 50.dp,
            alpha = 0.26f,
            delayMs = 500
        )
        AnimatedStarDecor(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            size = 28.dp,
            alpha = 0.42f,
            delayMs = 100
        )
        AnimatedStarDecor(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 34.dp, start = 44.dp),
            size = 26.dp,
            alpha = 0.52f,
            delayMs = 200
        )
        AnimatedStarDecor(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 76.dp, end = 76.dp),
            size = 18.dp,
            alpha = 0.48f,
            delayMs = 900
        )
        AnimatedFlowerDecor(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 130.dp, end = 26.dp),
            size = 44.dp,
            alpha = 0.22f,
            delayMs = 320
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(176.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(146.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFF8D6),
                                    Color(0xFFFEE89A),
                                    Color(0x88FFFFFF)
                                )
                            )
                        )
                )
                Surface(
                    modifier = Modifier
                        .size(122.dp)
                        .graphicsLayer {
                            shadowElevation = 34.dp.toPx()
                            shape = CircleShape
                            clip = true
                        },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.34f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.ic_ecoquest_logo),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier.size(104.dp)
                        )
                    }
                }
                AnimatedStarDecor(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 10.dp),
                    size = 20.dp,
                    alpha = 0.68f,
                    delayMs = 0
                )
                AnimatedStarDecor(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 18.dp, start = 8.dp),
                    size = 16.dp,
                    alpha = 0.58f,
                    delayMs = 500
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EcoQuest",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFFCF0)
            )
            Text(
                text = stringResource(R.string.login_tagline),
                color = Color(0xFFFCFFF0)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFFAFCF7),
                shadowElevation = 14.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Welcome Back! 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF214029)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.label_email)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6E9343),
                            focusedLabelColor = Color(0xFF4E6F31)
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.label_password)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6E9343),
                            focusedLabelColor = Color(0xFF4E6F31)
                        )
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { onLoginClick(email.trim(), password) },
                        enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5D842B)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.action_sign_in),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.login_demo_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6C7A69)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Let’s make the world green again",
                color = Color(0xFF4C6A49),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
