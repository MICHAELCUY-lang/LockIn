package com.example.lockin.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.lockin.domain.model.SessionStatus
import com.example.lockin.domain.model.LockSession
import com.example.lockin.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val allSessions by viewModel.allSessions.collectAsState(initial = emptyList())
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(start = 0.dp, top = 16.dp, end = 0.dp, bottom = 100.dp)
    ) {
        // HEADER
        item {
            Text(
                text = "Analytics & History",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }

        // CHART
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainer)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Focus Time (Last 7 Days)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    BarChart(sessions = allSessions, modifier = Modifier.fillMaxWidth().height(180.dp))
                }
            }
        }

        // HISTORY LIST
        item {
            Text(
                text = "Detailed Log",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp)
            )
        }

        if (allSessions.isEmpty()) {
            item {
                Text(
                    text = "No sessions recorded yet.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            items(allSessions) { session ->
                HistoryLogItem(session = session)
            }
        }
    }
}

@Composable
private fun BarChart(sessions: List<LockSession>, modifier: Modifier = Modifier) {
    // Generate data for the last 7 days
    val calendar = java.util.Calendar.getInstance()
    // Reset to start of day
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    
    val todayStart = calendar.timeInMillis
    
    val dailyDurations = FloatArray(7) // Index 0 is 6 days ago, Index 6 is today
    
    for (session in sessions) {
        if (session.status != SessionStatus.COMPLETED) continue
        
        val diffDays = ((todayStart - session.startTime) / (1000 * 60 * 60 * 24)).toInt()
        
        if (diffDays in 0..6) {
            val idx = 6 - diffDays
            dailyDurations[idx] += (session.duration / 1000 / 60).toFloat()
        }
    }

    val maxDuration = dailyDurations.maxOrNull()?.takeIf { it > 0 } ?: 60f

    val primaryColor = Primary
    val surfaceVariantColor = SurfaceVariant

    Canvas(modifier = modifier) {
        val barWidth = size.width / 14f // Space and bar
        val maxBarHeight = size.height
        
        for (i in 0 until 7) {
            val value = dailyDurations[i]
            val barHeight = (value / maxDuration) * maxBarHeight
            
            // Draw background track
            drawRoundRect(
                color = surfaceVariantColor,
                topLeft = Offset(x = i * 2 * barWidth + barWidth / 2, y = 0f),
                size = Size(width = barWidth, height = maxBarHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
            
            // Draw actual value
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(x = i * 2 * barWidth + barWidth / 2, y = maxBarHeight - barHeight),
                size = Size(width = barWidth, height = barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

@Composable
private fun HistoryLogItem(session: LockSession) {
    val context = LocalContext.current
    val pkg = session.blockedApps.firstOrNull()
    val iconBitmap = remember(session.id) {
        pkg?.let {
            try { context.packageManager.getApplicationIcon(it).toBitmap(150, 150).asImageBitmap() }
            catch (e: Exception) { null }
        }
    }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(session.startTime))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, SurfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (session.status == SessionStatus.COMPLETED) PrimaryContainer else ErrorContainer),
            contentAlignment = Alignment.Center
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                )
            } else {
                Icon(
                    if (session.status == SessionStatus.COMPLETED) Icons.Default.TaskAlt else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (session.status == SessionStatus.COMPLETED) OnPrimaryContainer else OnErrorContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (session.status == SessionStatus.COMPLETED) "Deep Work Session" else "Distraction Attempt",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Text(
                text = dateStr,
                fontSize = 12.sp,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Text(
            text = "${session.duration / 1000 / 60}m",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (session.status == SessionStatus.COMPLETED) Primary else Error
        )
    }
}
