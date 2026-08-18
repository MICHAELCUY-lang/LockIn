package com.example.lockin.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lockin.service.ProtectionState
import com.example.lockin.ui.theme.*
import kotlinx.coroutines.delay

class BlockingActivity : ComponentActivity() {

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "extra_blocked_package"
        const val EXTRA_END_TIME = "extra_end_time"
        const val EXTRA_IS_LIMIT_REACHED = "extra_is_limit_reached"
    }

    private var currentBlockedPkg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentBlockedPkg = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE) ?: ""
        val endTime = intent.getLongExtra(EXTRA_END_TIME, System.currentTimeMillis() + 60_000L)

        // ── CRITICAL: Intercept Back button — send to home, NEVER to the blocked app ──
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goHome()  // Always go to Home launcher, not back stack
            }
        })

        loadAndSetContent(endTime)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentBlockedPkg = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE) ?: ""
        ProtectionState.currentBlockedPackage = currentBlockedPkg

        val endTime = intent.getLongExtra(EXTRA_END_TIME, System.currentTimeMillis() + 60_000L)
        loadAndSetContent(endTime)
    }

    override fun onResume() {
        super.onResume()
        ProtectionState.isBlockingVisible = true
        ProtectionState.currentBlockedPackage = currentBlockedPkg
    }

    override fun onPause() {
        super.onPause()
        ProtectionState.isBlockingVisible = false
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun loadAndSetContent(endTime: Long) {
        var appName = "This App"
        var appIcon: Drawable? = null
        try {
            if (currentBlockedPkg.isNotBlank()) {
                val info = packageManager.getApplicationInfo(currentBlockedPkg, 0)
                appName = packageManager.getApplicationLabel(info).toString()
                appIcon = packageManager.getApplicationIcon(info)
            }
        } catch (_: Exception) {}

        setContent {
            LockInTheme {
                BlockingScreenContent(
                    appName = appName,
                    appIcon = appIcon,
                    endTime = endTime,
                    onReturnHome = { goHome() }
                )
            }
        }
    }

    /** Navigate to the home launcher — NEVER back to the blocked app */
    private fun goHome() {
        ProtectionState.isBlockingVisible = false

        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(homeIntent)
        // Do NOT call finish() — keep this activity alive in background so
        // if the blocked app comes back, we are still in the task stack.
        // Actually we do finish because we don't want it in back stack either.
        finish()
    }
}

// ── Composable UI ─────────────────────────────────────────────────────────────

@Composable
fun BlockingScreenContent(
    appName: String,
    appIcon: Drawable?,
    endTime: Long,
    onReturnHome: () -> Unit
) {
    var remainingMs by remember { mutableStateOf(maxOf(0L, endTime - System.currentTimeMillis())) }

    LaunchedEffect(endTime) {
        while (remainingMs > 0) {
            delay(500)
            remainingMs = maxOf(0L, endTime - System.currentTimeMillis())
        }
    }

    val iconBitmap = remember(appIcon) {
        appIcon?.let {
            try {
                val w = if (it.intrinsicWidth > 0) it.intrinsicWidth else 96
                val h = if (it.intrinsicHeight > 0) it.intrinsicHeight else 96
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                it.setBounds(0, 0, w, h)
                it.draw(Canvas(bmp))
                bmp.asImageBitmap()
            } catch (_: Exception) { null }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Glow background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .background(Surface0)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── App icon with lock badge ──────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Surface1),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconBitmap != null) {
                        Image(
                            bitmap = iconBitmap,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp))
                        )
                    } else {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Muted, modifier = Modifier.size(42.dp))
                    }
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Coral),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Label ─────────────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Coral.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "APP BLOCKED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Coral,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$appName is locked",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "You reached your limit.\nTime to focus.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Countdown timer ───────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Surface1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Coral.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (remainingMs > 0) {
                            val minutes = (remainingMs / 1000) / 60
                            val seconds = (remainingMs / 1000) % 60
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                fontSize = 54.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnBackground,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "REMAINING",
                                fontSize = 11.sp,
                                color = Muted,
                                letterSpacing = 2.sp
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "LOCKOUT DONE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald
                            )
                            Text(
                                text = "You can open the app again",
                                fontSize = 12.sp,
                                color = Muted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Return home button ────────────────────────────────────────
            Button(
                onClick = onReturnHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Surface1)
            ) {
                Icon(Icons.Default.Home, null, tint = OnSurface, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("GO TO HOME SCREEN", fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 13.sp)
            }
        }
    }
}

