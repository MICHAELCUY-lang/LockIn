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
import com.example.lockin.ui.theme.*

@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val allSessions by viewModel.allSessions.collectAsState(initial = emptyList())
    val monitoredApps by viewModel.monitoredApps.collectAsState(initial = emptySet())
    
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
                    text = "Your Progress",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                
                Box(modifier = Modifier.size(40.dp)) // Empty box for alignment
            }
        }

        // ── 2x2 METRICS GRID ──────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCard(
                        title = "Focused Time",
                        value = "${totalFocusMinutes}m",
                        subtitle = "+45m from yesterday",
                        icon = Icons.Default.Timer,
                        iconBgColor = PrimaryContainer,
                        iconTintColor = OnPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Sessions",
                        value = "${completedSessions.size}",
                        subtitle = "Completed",
                        icon = Icons.Default.TaskAlt,
                        iconBgColor = TertiaryContainer,
                        iconTintColor = OnTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCard(
                        title = "Current Streak",
                        value = "$streak Days",
                        subtitle = "Keep it up!",
                        icon = Icons.Default.LocalFireDepartment,
                        iconBgColor = ErrorContainer,
                        iconTintColor = OnErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Apps Restricted",
                        value = "${monitoredApps.size}",
                        subtitle = "Active rules",
                        icon = Icons.Default.Shield,
                        iconBgColor = SecondaryContainer,
                        iconTintColor = OnSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── ACHIEVEMENTS ──────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainer)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Achievements",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AchievementItem(
                    title = "First Block",
                    description = "Successfully complete one focus session",
                    progress = if (completedSessions.isNotEmpty()) 1f else 0f,
                    icon = Icons.Default.Star
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AchievementItem(
                    title = "Week Warrior",
                    description = "Maintain a 7-day focus streak",
                    progress = (streak / 7f).coerceIn(0f, 1f),
                    icon = Icons.Default.LocalFireDepartment
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AchievementItem(
                    title = "Zen Master",
                    description = "Accumulate 1000 focus points",
                    progress = (totalPoints / 1000f).coerceIn(0f, 1f),
                    icon = Icons.Default.SelfImprovement
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainer)
            .padding(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTintColor, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(title, fontSize = 14.sp, color = OnSurfaceVariant)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun AchievementItem(
    title: String,
    description: String,
    progress: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (progress >= 1f) PrimaryContainer else SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (progress >= 1f) OnPrimaryContainer else OnSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(Primary)
                )
            }
        }
    }
}
