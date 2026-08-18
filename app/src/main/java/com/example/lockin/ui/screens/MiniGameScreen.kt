package com.example.lockin.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lockin.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// ── Data ─────────────────────────────────────────────────────────────────────

enum class GameState { IDLE, COUNTDOWN, PLAYING, RESULT }

data class TapTarget(
    val id: Int,
    val xFrac: Float,  // 0..1 fraction of box width
    val yFrac: Float,  // 0..1 fraction of box height
    var tapped: Boolean = false,
    var missed: Boolean = false
)

private const val TOTAL_TARGETS   = 8
private const val WINDOW_MS       = 1000L   // ms to tap before it disappears
private const val PASS_THRESHOLD  = 6       // hits needed to earn a bypass

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun MiniGameScreen(
    navController: NavController,
    onBypassEarned: (() -> Unit)? = null  // called when user wins (from BlockingActivity)
) {
    var gameState   by remember { mutableStateOf(GameState.IDLE) }
    var countdown   by remember { mutableStateOf(3) }
    var currentRound by remember { mutableStateOf(0) }
    var targets     by remember { mutableStateOf<List<TapTarget>>(emptyList()) }
    var activeId    by remember { mutableStateOf(-1) }
    var hits        by remember { mutableStateOf(0) }
    var totalScore  by remember { mutableStateOf(0) }   // lifetime
    var lastResult  by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Generate random non-overlapping positions
    fun generateTargets(): List<TapTarget> = List(TOTAL_TARGETS) { i ->
        TapTarget(
            id = i,
            xFrac = 0.1f + Random.nextFloat() * 0.78f,
            yFrac = 0.08f + Random.nextFloat() * 0.80f
        )
    }

    fun startGame() {
        gameState = GameState.COUNTDOWN
        hits = 0
        currentRound = 0
        targets = generateTargets()
        activeId = -1

        scope.launch {
            for (i in 3 downTo 1) {
                countdown = i
                delay(700)
            }
            gameState = GameState.PLAYING

            for (i in 0 until TOTAL_TARGETS) {
                activeId = i
                delay(WINDOW_MS)
                // if not tapped → mark missed
                targets = targets.toMutableList().also { list ->
                    if (!list[i].tapped) list[i] = list[i].copy(missed = true)
                }
                activeId = -1
                delay(150)
            }

            // Done
            lastResult = hits >= PASS_THRESHOLD
            if (lastResult) {
                totalScore += hits * 10
                onBypassEarned?.invoke()
            }
            gameState = GameState.RESULT
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        when (gameState) {
            GameState.IDLE -> IdleView(
                totalScore = totalScore,
                onBack = { navController.popBackStack() },
                onStart = { startGame() }
            )

            GameState.COUNTDOWN -> CountdownView(countdown)

            GameState.PLAYING -> PlayingView(
                targets = targets,
                activeId = activeId,
                hits = hits,
                total = TOTAL_TARGETS,
                onTap = { id ->
                    if (activeId == id && !targets[id].tapped) {
                        targets = targets.toMutableList().also { list ->
                            list[id] = list[id].copy(tapped = true)
                        }
                        hits++
                    }
                }
            )

            GameState.RESULT -> ResultView(
                won = lastResult,
                hits = hits,
                total = TOTAL_TARGETS,
                threshold = PASS_THRESHOLD,
                onReplay = { startGame() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// ── Sub-Composables ───────────────────────────────────────────────────────────

@Composable
private fun IdleView(totalScore: Int, onBack: () -> Unit, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OnSurface)
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Trophy glow
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Violet.copy(alpha = 0.35f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Violet, modifier = Modifier.size(54.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Focus Tap",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = OnBackground
        )
        Text(
            text = "Challenge",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Violet
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tap the glowing circle before it disappears.\nHit $PASS_THRESHOLD/$TOTAL_TARGETS to earn a 5-min bypass!",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Score card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface1),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Violet, modifier = Modifier.size(22.dp))
                    Text(
                        text = "$TOTAL_TARGETS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = OnBackground
                    )
                    Text("Targets", style = MaterialTheme.typography.labelSmall, color = Muted)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Violet, modifier = Modifier.size(22.dp))
                    Text(
                        text = "1.0s",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = OnBackground
                    )
                    Text("Per target", style = MaterialTheme.typography.labelSmall, color = Muted)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Violet, modifier = Modifier.size(22.dp))
                    Text(
                        text = "$totalScore",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Violet
                    )
                    Text("Your score", style = MaterialTheme.typography.labelSmall, color = Muted)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Violet
            )
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("START CHALLENGE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CountdownView(value: Int) {
    val animScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300, easing = EaseOutBack),
        label = "countdown_scale"
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (value > 0) "$value" else "GO!",
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                color = if (value > 0) Violet else Emerald,
                modifier = Modifier.scale(animScale)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "GET READY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = Muted
            )
        }
    }
}

@Composable
private fun PlayingView(
    targets: List<TapTarget>,
    activeId: Int,
    hits: Int,
    total: Int,
    onTap: (Int) -> Unit
) {
    val progressAnim by animateFloatAsState(
        targetValue = hits.toFloat() / total.toFloat(),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "progress"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface0)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hits: $hits / $total", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Need $PASS_THRESHOLD to win", color = Muted, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progressAnim },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Emerald,
                trackColor = Surface2
            )
        }

        // Game field
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            // Render each target
            targets.forEachIndexed { i, target ->
                val isActive  = activeId == i
                val isTapped  = target.tapped
                val isMissed  = target.missed

                if (isActive || isTapped || isMissed) {
                    // Use BoxWithConstraints or just use fillMaxSize with fraction offsets
                    TargetDot(
                        xFrac = target.xFrac,
                        yFrac = target.yFrac,
                        isActive = isActive,
                        isTapped = isTapped,
                        isMissed = isMissed,
                        windowMs = WINDOW_MS,
                        onClick = { if (isActive) onTap(i) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetDot(
    xFrac: Float,
    yFrac: Float,
    isActive: Boolean,
    isTapped: Boolean,
    isMissed: Boolean,
    windowMs: Long,
    onClick: () -> Unit
) {
    val size = 64.dp
    val color = when {
        isTapped -> Emerald
        isMissed -> Coral
        else     -> Violet
    }

    val pulseAnim by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse_scale"
    )

    val countdownAnim by animateFloatAsState(
        targetValue = if (isActive) 0f else 1f,
        animationSpec = tween(windowMs.toInt(), easing = LinearEasing),
        label = "countdown"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val offsetX = (maxWidth - size) * xFrac
        val offsetY = (maxHeight - size) * yFrac

        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(size)
        ) {
            // Sweep timer ring (only when active)
            if (isActive) {
                CircularProgressIndicator(
                    progress = { countdownAnim },
                    modifier = Modifier.fillMaxSize(),
                    color = Cyan,
                    trackColor = Surface2,
                    strokeWidth = 3.dp
                )
            }

            // Main dot
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .align(Alignment.Center)
                    .scale(if (isActive) pulseAnim else 1f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(color, color.copy(alpha = 0.6f))
                        )
                    )
                    .border(2.dp, color.copy(alpha = 0.5f), CircleShape)
                    .clickable(enabled = isActive, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        isTapped -> "✓"
                        isMissed -> "✗"
                        else     -> "●"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ResultView(
    won: Boolean,
    hits: Int,
    total: Int,
    threshold: Int,
    onReplay: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (won) "🏆" else "😬", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (won) "Focus Unlocked!" else "Almost!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (won) Emerald else Coral
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You hit $hits/$total targets",
            fontSize = 18.sp,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (won) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Emerald.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "🔓 5-minute bypass earned!",
                    color = Emerald,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            Text(
                text = "Need $threshold hits to earn a bypass. Try again!",
                color = Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onReplay,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Violet)
        ) {
            Text("PLAY AGAIN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Muted)
        ) {
            Text("Back to Home", fontWeight = FontWeight.Medium)
        }
    }
}
