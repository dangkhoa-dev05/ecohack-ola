package com.ecoquest.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ecoquest.app.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a cute wave/blob header shape using Canvas.
 */
@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF4CAF50),
    secondaryColor: Color = Color(0xFF2E7D32),
    height: Dp = 220.dp
) {
    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(height)
    ) {
        // Back wave (darker)
        val backWavePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.68f)
            cubicTo(
                size.width * 0.80f, size.height * 0.92f,
                size.width * 0.55f, size.height * 0.78f,
                size.width * 0.30f, size.height * 0.88f
            )
            cubicTo(
                size.width * 0.12f, size.height * 0.95f,
                0f, size.height * 0.80f,
                0f, size.height * 0.75f
            )
            close()
        }
        drawPath(backWavePath, color = secondaryColor.copy(alpha = 0.55f))

        // Main wave
        val mainWavePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.60f)
            cubicTo(
                size.width * 0.75f, size.height * 0.90f,
                size.width * 0.45f, size.height * 0.68f,
                size.width * 0.18f, size.height * 0.82f
            )
            cubicTo(
                size.width * 0.07f, size.height * 0.88f,
                0f, size.height * 0.72f,
                0f, size.height * 0.68f
            )
            close()
        }
        drawPath(mainWavePath, color = color)

        // Sparkle dots decoration
        val dotColor = Color.White.copy(alpha = 0.25f)
        listOf(
            Offset(size.width * 0.15f, size.height * 0.25f) to 6f,
            Offset(size.width * 0.50f, size.height * 0.18f) to 4f,
            Offset(size.width * 0.80f, size.height * 0.30f) to 7f,
            Offset(size.width * 0.65f, size.height * 0.10f) to 3f,
            Offset(size.width * 0.35f, size.height * 0.38f) to 5f,
            Offset(size.width * 0.90f, size.height * 0.18f) to 4f,
        ).forEach { (offset, radius) ->
            drawCircle(color = dotColor, radius = radius.dp.toPx(), center = offset)
        }
    }
}

/**
 * Draws animated floating sparkle stars.
 */
@Composable
fun FloatingSparkles(
    modifier: Modifier = Modifier,
    count: Int = 6,
    color: Color = Color(0xFFFFD54F)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val positions = listOf(
            Triple(0.08f, 0.10f, 12f),
            Triple(0.85f, 0.15f, 8f),
            Triple(0.20f, 0.75f, 10f),
            Triple(0.70f, 0.80f, 7f),
            Triple(0.50f, 0.05f, 9f),
            Triple(0.92f, 0.55f, 6f),
        ).take(count)

        positions.forEachIndexed { idx, (xFrac, yFrac, baseR) ->
            val phase = (time + idx * 60f) * (Math.PI / 180f).toFloat()
            val offsetX = cos(phase) * 6f
            val offsetY = sin(phase) * 6f
            val alphaVal = 0.18f + 0.14f * sin((time + idx * 45f) * (Math.PI / 180f).toFloat())
            drawStar(
                center = Offset(size.width * xFrac + offsetX, size.height * yFrac + offsetY),
                outerRadius = baseR.dp.toPx(),
                innerRadius = (baseR * 0.4f).dp.toPx(),
                color = color.copy(alpha = alphaVal.coerceIn(0.08f, 0.35f)),
                points = 4
            )
        }
    }
}

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color,
    points: Int = 5
) {
    val path = Path()
    val angleStep = (Math.PI / points).toFloat()
    for (i in 0 until points * 2) {
        val angle = i * angleStep - (Math.PI / 2).toFloat()
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color)
}

/**
 * Animated rotating decorative ring of dots.
 */
@Composable
fun SpinningDotRing(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFA5D6A7),
    radius: Dp = 40.dp,
    dotCount: Int = 8,
    dotRadius: Dp = 5.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_angle"
    )

    Canvas(modifier = modifier.size(radius * 2 + dotRadius * 2)) {
        val center = Offset(size.width / 2, size.height / 2)
        val r = radius.toPx()
        val dr = dotRadius.toPx()
        repeat(dotCount) { i ->
            val a = (angle + i * 360f / dotCount) * (Math.PI / 180f).toFloat()
            val cx = center.x + r * cos(a)
            val cy = center.y + r * sin(a)
            val alpha = 0.15f + 0.25f * ((i.toFloat() / dotCount))
            drawCircle(color = color.copy(alpha = alpha), radius = dr, center = Offset(cx, cy))
        }
    }
}

/**
 * A decorative curved bottom wave divider.
 */
@Composable
fun BottomWaveDivider(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFF5F0E8),
    height: Dp = 48.dp
) {
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val path = Path().apply {
            moveTo(0f, size.height)
            cubicTo(
                size.width * 0.25f, 0f,
                size.width * 0.75f, size.height * 0.5f,
                size.width, 0f
            )
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, color = color)
    }
}

/**
 * Animated floating leaf composable using Image + animation.
 */
@Composable
fun AnimatedLeafDecor(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    alpha: Float = 0.22f,
    delayMs: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "leaf_anim")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200 + delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_y"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_rot"
    )

    androidx.compose.foundation.Image(
        painter = painterResource(R.drawable.ic_leaf_decor),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .offset(y = floatY.dp)
            .rotate(rotation)
            .alpha(alpha)
    )
}

/**
 * Animated floating flower composable.
 */
@Composable
fun AnimatedFlowerDecor(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    alpha: Float = 0.30f,
    delayMs: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flower_anim")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800 + delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flower_y"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500 + delayMs),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flower_rot"
    )

    androidx.compose.foundation.Image(
        painter = painterResource(R.drawable.ic_flower_deco),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .offset(y = floatY.dp)
            .rotate(rotation)
            .alpha(alpha)
    )
}

/**
 * Animated cute plant pot illustration.
 */
@Composable
fun AnimatedPlantPot(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    delayMs: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "plant_pot_anim")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pot_y"
    )
    val scl by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400 + delayMs),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pot_scale"
    )

    androidx.compose.foundation.Image(
        painter = painterResource(R.drawable.ic_plant_pot),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .offset(y = floatY.dp)
            .scale(scl)
    )
}

/**
 * Animated star sparkle decoration.
 */
@Composable
fun AnimatedStarDecor(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    alpha: Float = 0.55f,
    delayMs: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_anim")
    val scl by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200 + delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_scale"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + delayMs),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_rot"
    )

    androidx.compose.foundation.Image(
        painter = painterResource(R.drawable.ic_star_sparkle),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .scale(scl)
            .rotate(rotation)
            .alpha(alpha)
    )
}

/**
 * Decorative cloud composable.
 */
@Composable
fun AnimatedCloudDecor(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    alpha: Float = 0.18f,
    delayMs: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cloud_anim")
    val floatX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500 + delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_x"
    )

    androidx.compose.foundation.Image(
        painter = painterResource(R.drawable.ic_cloud_deco),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .offset(x = floatX.dp)
            .alpha(alpha)
    )
}
