package com.bibliotech.app.ui.splash

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bibliotech.app.R
import com.bibliotech.app.ui.theme.Gold
import com.bibliotech.app.ui.theme.Navy
import com.bibliotech.app.ui.theme.SoftGray
import com.bibliotech.app.ui.theme.White

@Composable
fun BibliotechSplashRoute(
    viewModel: SplashViewModel = viewModel(),
    onSplashFinished: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.shouldNavigate) {
        if (uiState.shouldNavigate) {
            onSplashFinished()
        }
    }

    BibliotechSplashScreen(uiState = uiState)
}

@Composable
fun BibliotechSplashScreen(
    uiState: SplashUiState,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    val logoScale by animateFloatAsState(
        targetValue = if (isPreview || uiState.isLogoVisible) 1f else 0.72f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (isPreview || uiState.isLogoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "logoAlpha"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (isPreview || uiState.isTitleVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "titleAlpha"
    )
    val titleOffset by animateDpAsState(
        targetValue = if (isPreview || uiState.isTitleVisible) 0.dp else 36.dp,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "titleOffset"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (isPreview || uiState.isTaglineVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "taglineAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splashTransition")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    val bgShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgShift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Navy, Color(0xFF1E293B), Navy),
                    startY = -500f * bgShift,
                    endY = 1500f * (1f + bgShift * 0.5f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                        radius = 1500f
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Gold.copy(alpha = 0.25f * glowPulse), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                BibliotechLogo(
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = titleOffset)
                    .graphicsLayer { alpha = titleAlpha }
            ) {
                Text(
                    text = stringResource(id = R.string.splash_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 12.sp,
                        color = White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(0f, 8f),
                            blurRadius = 12f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.splash_tagline),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = SoftGray,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
                )
            }
        }
    }
}

@Composable
fun BibliotechLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.5.dp.toPx()
        val bookWidth = size.width * 0.75f
        val bookHeight = size.height * 0.55f
        val centerX = size.width / 2
        val centerY = size.height / 2

        val path = Path().apply {
            moveTo(centerX - 4.dp.toPx(), centerY - bookHeight / 2)
            quadraticBezierTo(
                centerX - bookWidth / 4, centerY - bookHeight / 2 - 5.dp.toPx(),
                centerX - bookWidth / 2, centerY - bookHeight / 2 + 10.dp.toPx()
            )
            lineTo(centerX - bookWidth / 2, centerY + bookHeight / 2)
            quadraticBezierTo(
                centerX - bookWidth / 4, centerY + bookHeight / 2 - 5.dp.toPx(),
                centerX - 4.dp.toPx(), centerY + bookHeight / 2 - 12.dp.toPx()
            )

            moveTo(centerX + 4.dp.toPx(), centerY - bookHeight / 2)
            quadraticBezierTo(
                centerX + bookWidth / 4, centerY - bookHeight / 2 - 5.dp.toPx(),
                centerX + bookWidth / 2, centerY - bookHeight / 2 + 10.dp.toPx()
            )
            lineTo(centerX + bookWidth / 2, centerY + bookHeight / 2)
            quadraticBezierTo(
                centerX + bookWidth / 4, centerY + bookHeight / 2 - 5.dp.toPx(),
                centerX + 4.dp.toPx(), centerY + bookHeight / 2 - 12.dp.toPx()
            )
        }

        drawPath(
            path = path,
            color = Gold,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val glassRadius = size.width * 0.2f
        val glassCenter = Offset(centerX + bookWidth / 3.5f, centerY + bookHeight / 3.5f)

        drawCircle(
            color = Gold,
            radius = glassRadius,
            center = glassCenter,
            style = Stroke(width = strokeWidth)
        )

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                start = glassCenter - Offset(glassRadius, glassRadius),
                end = glassCenter + Offset(glassRadius, glassRadius)
            ),
            radius = glassRadius - (strokeWidth / 2),
            center = glassCenter
        )

        drawLine(
            color = Gold,
            start = glassCenter + Offset(glassRadius * 0.707f, glassRadius * 0.707f),
            end = glassCenter + Offset(glassRadius * 1.5f, glassRadius * 1.5f),
            strokeWidth = strokeWidth * 1.2f,
            cap = StrokeCap.Round
        )

        val dotRadius = 1.5.dp.toPx()
        val points = listOf(
            Offset(centerX - bookWidth * 0.35f, centerY - bookHeight * 0.3f),
            Offset(centerX - bookWidth * 0.4f, centerY - bookHeight * 0.15f),
            Offset(centerX - bookWidth * 0.32f, centerY + bookHeight * 0.1f)
        )

        points.forEach { point ->
            drawCircle(color = Gold, radius = dotRadius, center = point)
        }

        drawLine(
            color = Gold.copy(alpha = 0.3f),
            start = points[0],
            end = Offset(centerX - bookWidth * 0.2f, points[0].y),
            strokeWidth = 0.8.dp.toPx()
        )
        drawLine(
            color = Gold.copy(alpha = 0.3f),
            start = points[1],
            end = points[0],
            strokeWidth = 0.8.dp.toPx()
        )

        drawCircle(
            color = Gold.copy(alpha = 0.6f),
            radius = dotRadius * 0.5f,
            center = Offset(centerX + bookWidth * 0.1f, centerY - bookHeight * 0.35f)
        )
    }
}