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

@Composable
fun GlowBlobLayer(
    modifier: Modifier = Modifier,
    style: NatureBackdropStyle
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_blob")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_blob_time"
    )

    val baseColor = when (style) {
        NatureBackdropStyle.Tasks -> Color(0xFFB7D9A8)
        NatureBackdropStyle.Rank -> Color(0xFFE8DCA1)
        NatureBackdropStyle.History -> Color(0xFFCBE3B8)
        NatureBackdropStyle.EcoBot -> Color(0xFFAEDCC7)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val phase = (time * (Math.PI / 180f)).toFloat()
        val x1 = size.width * 0.20f + cos(phase) * 18f
        val y1 = size.height * 0.24f + sin(phase * 0.8f) * 14f
        val x2 = size.width * 0.82f + sin(phase * 0.9f) * 14f
        val y2 = size.height * 0.68f + cos(phase * 0.7f) * 16f

        drawCircle(
            color = baseColor.copy(alpha = 0.18f),
            radius = size.minDimension * 0.26f,
            center = Offset(x1, y1)
        )
        drawCircle(
            color = baseColor.copy(alpha = 0.12f),
            radius = size.minDimension * 0.22f,
            center = Offset(x2, y2)
        )
    }
}

@Composable
fun FireflyDrift(
    modifier: Modifier = Modifier,
    count: Int = 14,
    color: Color = Color(0xFFFFF59D)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "firefly")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "firefly_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        repeat(count) { i ->
            val lane = (i + 1).toFloat() / (count + 1)
            val drift = sin((time * 9f + i) * (Math.PI / 2f).toFloat()) * 18f
            val x = size.width * lane + drift
            val y = size.height * (1f - ((time * (0.35f + (i % 4) * 0.08f) + i * 0.07f) % 1f))
            val alpha = (0.10f + 0.22f * sin((time * 18f + i) * (Math.PI / 2f).toFloat())).coerceIn(0.08f, 0.28f)
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = (2.2f + (i % 3)).dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun OrbitSparkRing(
    modifier: Modifier = Modifier,
    radius: Dp = 36.dp,
    points: Int = 9,
    color: Color = Color(0xFFFFE082)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit_spark")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    Canvas(modifier = modifier.size(radius * 2 + 20.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = radius.toPx()
        repeat(points) { index ->
            val a = (angle + index * (360f / points)) * (Math.PI / 180f).toFloat()
            val p = Offset(
                x = center.x + cos(a) * r,
                y = center.y + sin(a) * r
            )
            drawStar(
                center = p,
                outerRadius = (3.8f + (index % 2)).dp.toPx(),
                innerRadius = 1.8.dp.toPx(),
                color = color.copy(alpha = 0.45f + (index / points.toFloat()) * 0.25f),
                points = 4
            )
        }
    }
}

@Composable
fun ShootingSparkLayer(
    modifier: Modifier = Modifier,
    streakCount: Int = 6,
    color: Color = Color(0xFFFFF3B0)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shooting_spark")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shooting_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        repeat(streakCount) { index ->
            val offsetBase = (index + 1).toFloat() / (streakCount + 1)
            val phase = ((time + index * 0.15f) % 1f)
            val startX = size.width * (offsetBase + phase * 0.35f)
            val startY = size.height * (phase * 0.65f)
            val endX = startX - 42f
            val endY = startY + 24f
            val alpha = (0.08f + 0.22f * sin((phase * Math.PI).toFloat())).coerceIn(0.06f, 0.26f)

            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.2f
            )
            drawCircle(
                color = color.copy(alpha = (alpha + 0.08f).coerceAtMost(0.33f)),
                radius = 2.2f,
                center = Offset(startX, startY)
            )
        }
    }
}

@Composable
fun BreathingHalo(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFCDE8B3),
    minRadius: Dp = 54.dp,
    maxRadius: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing_halo")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_progress"
    )

    Canvas(modifier = modifier.size(maxRadius * 2)) {
        val radius = minRadius.toPx() + (maxRadius.toPx() - minRadius.toPx()) * progress
        drawCircle(
            color = color.copy(alpha = 0.14f),
            radius = radius
        )
        drawCircle(
            color = color.copy(alpha = 0.08f),
            radius = radius * 0.68f,
            style = Stroke(width = 6f)
        )
    }
}

@Composable
fun OrbitingLeafCluster(
    modifier: Modifier = Modifier,
    orbitRadius: Dp = 36.dp,
    size: Dp = 34.dp,
    alpha: Float = 0.22f,
    durationMs: Int = 7600
) {
    val infiniteTransition = rememberInfiniteTransition(label = "leaf_orbit")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "leaf_orbit_angle"
    )

    Box(modifier = modifier.size(orbitRadius * 2 + size * 2)) {
        val orbit = orbitRadius.value

        AnimatedLeafDecor(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(
                    x = (cos((angle) * (Math.PI / 180f).toFloat()) * orbit).dp,
                    y = (sin((angle) * (Math.PI / 180f).toFloat()) * orbit).dp
                ),
            size = size,
            alpha = alpha,
            delayMs = 0
        )

        AnimatedLeafDecor(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(
                    x = (cos((angle + 180f) * (Math.PI / 180f).toFloat()) * orbit).dp,
                    y = (sin((angle + 180f) * (Math.PI / 180f).toFloat()) * orbit).dp
                ),
            size = size * 0.85f,
            alpha = (alpha * 0.9f),
            delayMs = 800
        )
    }
}

enum class NatureBackdropStyle {
    Tasks,
    Rank,
    History,
    EcoBot
}

enum class EffectsIntensity {
    Low,
    Medium,
    High
}

object VisualEffectsConfig {
    // Quick switch for animation density across non-home screens.
    var intensity: EffectsIntensity = EffectsIntensity.High
}

private fun scaledCount(base: Int): Int {
    val scale = when (VisualEffectsConfig.intensity) {
        EffectsIntensity.Low -> 0.65f
        EffectsIntensity.Medium -> 1.0f
        EffectsIntensity.High -> 1.45f
    }
    return (base * scale).toInt().coerceAtLeast(1)
}

@Composable
fun OneShotRankStarBurst(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onFinished: () -> Unit = {}
) {
    if (!enabled) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        val center = Offset(size.width / 2f, size.height * 0.16f)
        val maxRadius = size.minDimension * 0.42f
        val ringRadius = maxRadius * p
        val alpha = (1f - p).coerceIn(0f, 1f)

        drawCircle(
            color = Color(0xFFFFE082).copy(alpha = 0.25f * alpha),
            radius = ringRadius,
            center = center,
            style = Stroke(width = 8f)
        )

        val starCount = scaledCount(20)
        repeat(starCount) { i ->
            val angle = (i * (360f / starCount)) * (Math.PI / 180f).toFloat()
            val radius = ringRadius * (0.52f + (i % 3) * 0.22f)
            val x = center.x + cos(angle) * radius
            val y = center.y + sin(angle) * radius
            drawStar(
                center = Offset(x, y),
                outerRadius = (6f - p * 2f).dp.toPx().coerceAtLeast(2.dp.toPx()),
                innerRadius = 2.2.dp.toPx(),
                color = Color(0xFFFFF3B0).copy(alpha = (0.75f - p * 0.55f).coerceAtLeast(0f)),
                points = 4
            )
        }
    }
}

@Composable
fun OneShotTop3ConfettiBurst(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onFinished: () -> Unit = {}
) {
    if (!enabled) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1800, easing = LinearOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        val anchors = listOf(0.18f, 0.50f, 0.82f)
        val piecesPerAnchor = scaledCount(16)
        val colors = listOf(
            Color(0xFFFFE082),
            Color(0xFFFFCC80),
            Color(0xFFC5E1A5),
            Color(0xFFB39DDB),
            Color(0xFF80CBC4)
        )

        anchors.forEachIndexed { anchorIndex, anchor ->
            repeat(piecesPerAnchor) { i ->
                val lane = i / piecesPerAnchor.toFloat()
                val wobble = sin((p * 16f + i * 0.6f) * (Math.PI / 2f).toFloat()) * (14f + i % 4)
                val x = size.width * anchor + wobble + (anchorIndex - 1) * 6f
                val startY = size.height * 0.10f
                val y = startY + size.height * (0.70f * p + lane * 0.10f)
                val alpha = (0.85f - p * 0.72f).coerceIn(0f, 0.85f)
                val pieceColor = colors[(i + anchorIndex) % colors.size].copy(alpha = alpha)

                if (i % 2 == 0) {
                    drawRect(
                        color = pieceColor,
                        topLeft = Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(5f + (i % 3), 8f + (i % 4))
                    )
                } else {
                    drawCircle(
                        color = pieceColor,
                        radius = 3f + (i % 3),
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

/**
 * Reusable soft animated background decorations for non-home screens.
 */
@Composable
fun AnimatedNatureBackdrop(
    modifier: Modifier = Modifier,
    sparkleCount: Int = 7,
    style: NatureBackdropStyle = NatureBackdropStyle.Tasks,
    scrollInfluence: Float = 0f
) {
    val reactiveShift by animateFloatAsState(
        targetValue = scrollInfluence.coerceIn(-30f, 30f),
        animationSpec = spring(dampingRatio = 0.58f, stiffness = 95f),
        label = "backdrop_reactive_shift"
    )

    Box(modifier = modifier.fillMaxSize()) {
        when (style) {
            NatureBackdropStyle.Tasks -> {
                GlowBlobLayer(
                    modifier = Modifier.offset(
                        x = (reactiveShift * 0.34f).dp,
                        y = (-reactiveShift * 0.24f).dp
                    ),
                    style = style
                )
                FireflyDrift(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (reactiveShift * 0.22f).dp),
                    count = scaledCount(12),
                    color = Color(0xFFFFF9C4)
                )
                AnimatedLeafDecor(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (reactiveShift * 0.34f).dp)
                        .padding(top = 96.dp, start = 0.dp),
                    size = 106.dp,
                    alpha = 0.17f,
                    delayMs = 180
                )
                AnimatedFlowerDecor(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-reactiveShift * 0.22f).dp)
                        .padding(bottom = 152.dp, end = 12.dp),
                    size = 56.dp,
                    alpha = 0.24f,
                    delayMs = 500
                )
                OrbitingLeafCluster(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    orbitRadius = 24.dp,
                    size = 24.dp,
                    alpha = 0.16f,
                    durationMs = 6400
                )
                OrbitSparkRing(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 136.dp, start = 24.dp),
                    radius = 20.dp,
                    points = 7,
                    color = Color(0xFFE7EBA6)
                )
            }

            NatureBackdropStyle.Rank -> {
                BreathingHalo(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (8 - reactiveShift * 0.16f).dp),
                    color = Color(0xFFE9DEA8),
                    minRadius = 48.dp,
                    maxRadius = 82.dp
                )
                FloatingSparkles(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-reactiveShift * 0.18f).dp),
                    color = Color(0xFFFFD54F),
                    count = scaledCount(sparkleCount + 8)
                )
                ShootingSparkLayer(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (-reactiveShift * 0.18f).dp),
                    streakCount = scaledCount(9),
                    color = Color(0xFFFFEEB8)
                )
                OrbitSparkRing(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 72.dp),
                    radius = 30.dp,
                    points = 11,
                    color = Color(0xFFFFE08A)
                )
                SpinningDotRing(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp),
                    color = Color(0xFFE7DA9D),
                    radius = 24.dp,
                    dotCount = 12,
                    dotRadius = 2.6.dp
                )
                SpinningDotRing(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp),
                    color = Color(0xFFD9C882),
                    radius = 24.dp,
                    dotCount = 12,
                    dotRadius = 2.6.dp
                )
                AnimatedStarDecor(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (reactiveShift * 0.20f).dp)
                        .padding(bottom = 128.dp),
                    size = 28.dp,
                    alpha = 0.5f,
                    delayMs = 0
                )
            }

            NatureBackdropStyle.History -> {
                GlowBlobLayer(
                    modifier = Modifier.offset(
                        x = (-reactiveShift * 0.26f).dp,
                        y = (-reactiveShift * 0.14f).dp
                    ),
                    style = style
                )
                AnimatedCloudDecor(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (reactiveShift * 0.24f).dp)
                        .padding(top = 86.dp, start = 18.dp),
                    size = 86.dp,
                    alpha = 0.16f,
                    delayMs = 260
                )
                AnimatedCloudDecor(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-reactiveShift * 0.22f).dp)
                        .padding(bottom = 168.dp, end = 12.dp),
                    size = 76.dp,
                    alpha = 0.13f,
                    delayMs = 740
                )
                AnimatedLeafDecor(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (reactiveShift * 0.30f).dp)
                        .padding(start = 0.dp),
                    size = 72.dp,
                    alpha = 0.12f,
                    delayMs = 980
                )
                FireflyDrift(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (reactiveShift * 0.12f).dp),
                    count = scaledCount(8),
                    color = Color(0xFFE6EE9C)
                )
                OrbitSparkRing(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 128.dp, end = 32.dp),
                    radius = 16.dp,
                    points = 6,
                    color = Color(0xFFDCEEA8)
                )
            }

            NatureBackdropStyle.EcoBot -> {
                BreathingHalo(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (22 - reactiveShift * 0.12f).dp),
                    color = Color(0xFFC5E6CE),
                    minRadius = 40.dp,
                    maxRadius = 70.dp
                )
                ShootingSparkLayer(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (-reactiveShift * 0.18f).dp),
                    streakCount = scaledCount(7),
                    color = Color(0xFFDDEFBF)
                )
                FireflyDrift(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (reactiveShift * 0.18f).dp),
                    count = scaledCount(14),
                    color = Color(0xFFC5E1A5)
                )
                AnimatedCloudDecor(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-reactiveShift * 0.20f).dp)
                        .padding(top = 52.dp),
                    size = 88.dp,
                    alpha = 0.15f,
                    delayMs = 420
                )
                OrbitSparkRing(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 24.dp),
                    radius = 24.dp,
                    points = 8,
                    color = Color(0xFFDBF0AA)
                )
                AnimatedStarDecor(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (reactiveShift * 0.20f).dp)
                        .padding(bottom = 142.dp, start = 22.dp),
                    size = 22.dp,
                    alpha = 0.38f,
                    delayMs = 1100
                )
            }
        }
    }
}
