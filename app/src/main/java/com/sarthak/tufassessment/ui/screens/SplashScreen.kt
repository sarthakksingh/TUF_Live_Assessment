package com.sarthak.tufassessment.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun UchatSplashScreen(
    onFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val pulse = rememberInfiniteTransition(label = "pulse")
    val ringScale by pulse.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringScale"
    )
    val ringAlpha by pulse.animateFloat(
        initialValue = 0.24f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )
    val badgeScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.72f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 220f),
        label = "badgeScale"
    )
    val badgeAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 650),
        label = "badgeAlpha"
    )
    val titleAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 150),
        label = "titleAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF14313B),
                        Color(0xFF0B141A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(220.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366).copy(alpha = 0.08f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.graphicsLayer(
                    scaleX = badgeScale,
                    scaleY = badgeScale,
                    alpha = badgeAlpha
                ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(168.dp)
                        .shadow(28.dp, CircleShape)
                        .background(Color(0xFF0F1B22), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .graphicsLayer(
                            scaleX = ringScale,
                            scaleY = ringScale,
                            alpha = ringAlpha
                        )
                        .background(Color(0xFF25D366).copy(alpha = 0.16f), CircleShape)
                )
                Surface(
                    shape = RoundedCornerShape(42.dp),
                    color = Color(0xFF25D366),
                    shadowElevation = 12.dp
                ) {
                    Text(
                        text = "U",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color(0xFF0B141A),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(titleAlpha)
            ) {
                Text(
                    text = "Uchat",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Chats, status, and calls in one place",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.72f)
                )
            }
        }
    }
}
