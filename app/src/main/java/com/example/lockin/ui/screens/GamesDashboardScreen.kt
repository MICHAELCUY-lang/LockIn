package com.example.lockin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lockin.domain.model.SessionStatus
import com.example.lockin.ui.navigation.Screen
import com.example.lockin.ui.theme.*

@Composable
fun GamesDashboardScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val allSessions by viewModel.allSessions.collectAsState(initial = emptyList())
    val completedSessions = remember(allSessions) { allSessions.filter { it.status == SessionStatus.COMPLETED } }
    val totalFocusMinutes = remember(completedSessions) { completedSessions.sumOf { it.duration / 1000 / 60 } }
    val streak = completedSessions.size.coerceAtMost(7)
    val totalPoints = totalFocusMinutes * 10 // 10 points per minute of focus
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
                    text = "Focus Games",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                
                Box(modifier = Modifier.size(40.dp)) // Empty box for alignment
            }
        }

        // ── STATS OVERVIEW CARD ───────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Current Streak
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = OnPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Current Streak", fontSize = 12.sp, color = OnSurfaceVariant)
                            Text("$streak Days", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                    }
                }
                
                // Total Points
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = OnTertiaryContainer)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Focus Points", fontSize = 12.sp, color = OnSurfaceVariant)
                            Text(String.format("%,d", totalPoints), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                    }
                }
            }
        }

        // ── CATEGORIES (Chips) ────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip("All Games", true)
                CategoryChip("Reflex", false)
                CategoryChip("Memory", false)
                CategoryChip("Logic", false)
            }
        }

        // ── FEATURED CHALLENGE (Focus Tap) ────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainer)
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PrimaryContainer
                        ) {
                            Text(
                                "FEATURED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = OnSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdsClick, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Focus Tap Challenge",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                "Tap moving targets to earn bypass",
                                fontSize = 14.sp,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureDetail(Icons.Default.Timer, "30 Sec")
                        FeatureDetail(Icons.Default.Star, "5 Min Bypass")
                        FeatureDetail(Icons.Default.Leaderboard, "Hard")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate(Screen.MiniGame.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary
                        )
                    ) {
                        Text("Launch Challenge", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                    }
                }
            }
        }

        // ── COMING SOON (Memory Recall) ───────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Surface)
                    .border(1.dp, SurfaceVariant, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceVariant
                        ) {
                            Text(
                                "COMING SOON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Icon(Icons.Default.Lock, contentDescription = null, tint = OnSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Memory Recall",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                "Match pairs to unlock app access",
                                fontSize = 14.sp,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Primary else SurfaceContainer,
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) OnPrimary else OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun FeatureDetail(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = OnSurface)
    }
}
