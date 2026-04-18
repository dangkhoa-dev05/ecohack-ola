package com.ecoquest.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ecoquest.app.ui.theme.EcoQuestTheme

private val LoginBackground = Color(0xFF0B0F12)
private val LoginSurface = Color(0xFF151A1F)
private val LoginSurfaceHigh = Color(0xFF1B2025)
private val LoginSurfaceHighest = Color(0xFF21262C)
private val LoginTextMuted = Color(0xFFA8ABB1)
private val LoginOutline = Color(0xFF44484D)
private val LoginPrimary = Color(0xFFA1FFC2)
private val LoginPrimaryBright = Color(0xFF00FC9A)
private val LoginTertiary = Color(0xFF04D5FF)
private val LoginOnPrimary = Color(0xFF00643A)

@Composable
fun LoginScreen(
    isLoading: Boolean = false,
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LoginBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LoginBackground)
                .padding(padding)
        ) {
            LoginBackgroundDecor()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(36.dp))
                LoginBranding()
                Spacer(modifier = Modifier.height(44.dp))

                Card(
                    modifier = Modifier.widthIn(max = 420.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LoginSurfaceHigh.copy(alpha = 0.9f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = LoginOutline.copy(alpha = 0.25f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "Email or Username",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = LoginPrimary,
                            letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Enter your credentials") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = LoginTextMuted
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Password",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = LoginPrimary
                                )
                                TextButton(
                                    onClick = {}
                                ) {
                                    Text("Forgot Password?", color = LoginTextMuted)
                                }
                            }

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("••••••••") },
                                visualTransformation = PasswordVisualTransformation(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = LoginTextMuted
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GradientLoginButton(
                            text = "LOGIN TO ECOSYSTEM",
                            isLoading = isLoading,
                            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                            onClick = { onLoginClick(email.trim(), password) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New to the mission? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoginTextMuted,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoginPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LoginBackgroundDecor() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(LoginPrimary.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.CenterEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(LoginTertiary.copy(alpha = 0.12f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = LoginPrimary.copy(alpha = 0.08f),
                startAngle = 15f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(-size.width * 0.1f, size.height * 0.25f),
                size = Size(size.width * 1.1f, size.height * 0.35f),
                style = stroke
            )
            drawArc(
                color = LoginPrimary.copy(alpha = 0.05f),
                startAngle = 10f,
                sweepAngle = 115f,
                useCenter = false,
                topLeft = Offset(-size.width * 0.05f, size.height * 0.38f),
                size = Size(size.width * 1.05f, size.height * 0.28f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 14f))
                )
            )
        }
    }
}

@Composable
private fun LoginBranding() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(84.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(LoginPrimaryBright.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(LoginSurfaceHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = LoginPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "Eco",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFEBEEF4)
            )
            Text(
                text = "Pulse",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = LoginPrimary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "LEVEL UP THE PLANET",
            style = MaterialTheme.typography.labelLarge,
            color = LoginTextMuted
        )
    }
}

@Composable
private fun GradientLoginButton(
    text: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(LoginPrimary, LoginPrimaryBright)
                    )
                )
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = LoginOnPrimary
                )
            } else {
                Text(
                    text = text,
                    color = LoginOnPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SocialButton(
    modifier: Modifier = Modifier,
    label: String
) {
    OutlinedButton(
        onClick = {},
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            LoginOutline.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = LoginSurface
        )
    ) {
        Text(
            text = label,
            color = Color(0xFFEBEEF4),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    EcoQuestTheme {
        LoginScreen()
    }
}
