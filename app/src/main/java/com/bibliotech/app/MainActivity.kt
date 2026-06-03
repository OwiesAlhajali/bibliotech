package com.bibliotech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bibliotech.app.ui.theme.BibliotechTheme
import com.bibliotech.app.ui.theme.Gold
import com.bibliotech.app.ui.theme.Navy
import com.bibliotech.app.ui.theme.SoftGray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BibliotechTheme {
                BiblioteechSplashScreen()
            }
        }
    }
}

@Composable
fun BiblioteechSplashScreen(modifier: Modifier = Modifier) {
    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current
    var startAnimation by remember { mutableStateOf(isPreview) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "LogoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "LogoAlpha"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 600),
        label = "TitleAlpha"
    )
    val titleOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 1000, delayMillis = 600, easing = EaseOutBack),
        label = "TitleOffset"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 1200),
        label = "TaglineAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteTransition")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    val bgShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BackgroundShift"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

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
        // Subtle cinematic vignette
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
                // Soft golden glow around the logo
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
                
                BiblioteechLogo(
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
                    text = "BIBLIOTEECH",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 12.sp,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(0f, 8f),
                            blurRadius = 12f
                        )
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Discover Books. Instantly.",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = SoftGray,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
                )
            }
        }
    }
}

@Composable
fun BiblioteechLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.5.dp.toPx()
        val bookWidth = size.width * 0.75f
        val bookHeight = size.height * 0.55f
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // Premium Book Design
        val path = Path().apply {
            // Left page (rounded geometric)
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
            
            // Right page (rounded geometric)
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

        // Magnifying glass integrated into the book
        val glassRadius = size.width * 0.2f
        val glassCenter = Offset(centerX + bookWidth / 3.5f, centerY + bookHeight / 3.5f)
        
        // Glass lens with glassmorphism effect
        drawCircle(
            color = Gold,
            radius = glassRadius,
            center = glassCenter,
            style = Stroke(width = strokeWidth)
        )
        
        // Inner lens highlight (Glass effect)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                start = glassCenter - Offset(glassRadius, glassRadius),
                end = glassCenter + Offset(glassRadius, glassRadius)
            ),
            radius = glassRadius - (strokeWidth / 2),
            center = glassCenter
        )
        
        // Handle
        drawLine(
            color = Gold,
            start = glassCenter + Offset(glassRadius * 0.707f, glassRadius * 0.707f),
            end = glassCenter + Offset(glassRadius * 1.5f, glassRadius * 1.5f),
            strokeWidth = strokeWidth * 1.2f,
            cap = StrokeCap.Round
        )

        // Digital/Network elements
        val dotRadius = 1.5.dp.toPx()
        
        // Tech dots and lines
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
        
        // Subtle glow dots
        drawCircle(
            color = Gold.copy(alpha = 0.6f),
            radius = dotRadius * 0.5f,
            center = Offset(centerX + bookWidth * 0.1f, centerY - bookHeight * 0.35f)
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun BiblioteechSplashPreview() {
    BibliotechTheme {
        BiblioteechSplashScreen()
    }
}
