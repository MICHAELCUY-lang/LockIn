package com.example.lockin.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lockin.domain.model.SessionStatus
import com.example.lockin.service.ProtectionState
import com.example.lockin.ui.navigation.Screen
import com.example.lockin.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val allSessions by viewModel.allSessions.collectAsState(initial = emptyList())
    val monitoredApps by viewModel.monitoredApps.collectAsState(initial = emptySet())
    val usageLimit by viewModel.usageLimitMinutes.collectAsState(initial = 30L)
    val lockoutDuration by viewModel.lockoutDurationMinutes.collectAsState(initial = 30L)
    val isProtectionEnabled by viewModel.isProtectionEnabled.collectAsState(initial = false)

    val isActive = isProtectionEnabled && monitoredApps.isNotEmpty()
    val completedSessions = remember(allSessions) { allSessions.filter { it.status == SessionStatus.COMPLETED } }
    val totalFocusMinutes = remember(completedSessions) { completedSessions.sumOf { it.duration / 1000 / 60 } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 100.dp) // extra padding for bottom nav
    ) {
        // ── TOP APP BAR ───────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh)
                        .border(1.dp, OutlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = OnSurfaceVariant)
                }
                
                Text(
                    text = "LockIn",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                
                IconButton(
                    onClick = { navController.navigate(Screen.AppSelection.route) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = OnSurfaceVariant)
                }
            }
        }

        // ── FOCUS TIMER CARD ──────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainer)
                    .padding(24.dp)
            ) {
                // Ambient Glow (simulated with a Box in the background)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(PrimaryContainer.copy(alpha = 0.2f))
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Protection Active Badge
                    Row(
                        modifier = Modifier
                            .background(PrimaryContainer.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isActive) Icons.Default.Shield else Icons.Default.Warning,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isActive) "Protection Active" else "Not Configured",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Large Timer Display
                    Text(
                        text = if (isActive) "${usageLimit}m Limit" else "00:00",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        letterSpacing = (-0.5).sp
                    )
                    
                    Text(
                        text = if (isActive) "${lockoutDuration}m Lockout Duration" else "No limit set",
                        fontSize = 16.sp,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress Bar
                    Column(modifier = Modifier.width(280.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Setup Protection", fontSize = 12.sp, color = OnSurfaceVariant)
                            Text(if (isActive) "100%" else "0%", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (isActive) 1f else 0f)
                                    .fillMaxHeight()
                                    .background(Primary)
                            )
                        }
                    }
                }
            }
        }

        // ── WATCHED APPS BENTO GRID ───────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Watched Apps",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (monitoredApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, SurfaceVariant, RoundedCornerShape(12.dp))
                            .clickable { navController.navigate(Screen.AppSelection.route) }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Add App to Watchlist", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        }
                    }
                } else {
                    val appsList = monitoredApps.toList()
                    // Total items to render = apps + 1 (for the add button)
                    val totalItems = appsList.size + 1
                    for (i in 0 until totalItems step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left item
                            if (i < appsList.size) {
                                AppCard(pkg = appsList[i], modifier = Modifier.weight(1f))
                            } else {
                                AddAppButton(navController, modifier = Modifier.weight(1f))
                            }

                            // Right item
                            if (i + 1 < appsList.size) {
                                AppCard(pkg = appsList[i + 1], modifier = Modifier.weight(1f))
                            } else if (i + 1 == appsList.size) {
                                AddAppButton(navController, modifier = Modifier.weight(1f))
                            } else {
                                // Empty spacer if both apps and add button are already rendered
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // ── SESSION HISTORY ───────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainer)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Session History",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (allSessions.isEmpty()) {
                    Text("No sessions recorded yet.", fontSize = 14.sp, color = OnSurfaceVariant)
                } else {
                    allSessions.take(3).forEach { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (session.status == SessionStatus.COMPLETED) PrimaryContainer else ErrorContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (session.status == SessionStatus.COMPLETED) Icons.Default.TaskAlt else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (session.status == SessionStatus.COMPLETED) OnPrimaryContainer else OnErrorContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (session.status == SessionStatus.COMPLETED) "Deep Work Session" else "Distraction Blocked",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface
                                )
                                Text(
                                    text = "${session.duration / 1000 / 60} min • ${session.status.name}",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        if (session != allSessions.take(3).last()) {
                            Divider(color = SurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(Screen.Stats.route) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = OnSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant)
                ) {
                    Text("View Full History", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun AppCard(pkg: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val appName = remember(pkg) {
        try { context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(pkg, 0)).toString() }
        catch (e: Exception) { pkg.substringAfterLast(".") }
    }
    val isLocked = ProtectionState.isHardLocked(pkg)
    val iconBitmap = remember(pkg) {
        try { context.packageManager.getApplicationIcon(pkg).toBitmap(150, 150).asImageBitmap() }
        catch (e: Exception) { null }
    }

    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainer)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Icon(Icons.Default.Apps, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        if (isLocked) Icons.Default.LockClock else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isLocked) Error else OnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLocked) "Locked" else "Watching",
                        fontSize = 12.sp,
                        color = if (isLocked) Error else OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AddAppButton(navController: NavController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, SurfaceVariant, RoundedCornerShape(12.dp))
            .clickable { navController.navigate(Screen.AppSelection.route) },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(28.dp))
    }
}
