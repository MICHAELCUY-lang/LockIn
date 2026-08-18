package com.example.lockin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lockin.service.AppMonitoringService
import com.example.lockin.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockSetupScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val selectedAppToConfigure by viewModel.selectedAppToConfigure.collectAsState()
    val appRules by viewModel.appRules.collectAsState()
    val allApps by viewModel.installedApps.collectAsState()

    if (selectedAppToConfigure == null) {
        // Fallback
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val pkg = selectedAppToConfigure!!
    val appInfo = remember(pkg, allApps) { allApps.find { it.packageName == pkg } }
    val currentRule = appRules[pkg]

    var usageLimitMinutes by remember(currentRule) { mutableStateOf(currentRule?.usageLimitMinutes ?: 30L) }
    var lockoutDurationMinutes by remember(currentRule) { mutableStateOf(currentRule?.lockoutDurationMinutes ?: 30L) }

    val usageLimitOptions = listOf(
        1L to "1 min (Test)",
        15L to "15 min",
        30L to "30 min",
        45L to "45 min",
        60L to "1 hour"
    )

    val lockoutDurationOptions = listOf(
        1L to "1 min (Test)",
        15L to "15 min",
        30L to "30 min",
        60L to "1 hour",
        120L to "2 hours"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = appInfo?.name ?: "Configure App",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary Action: Save Auto-Doomscroll Rule
                    Button(
                        onClick = {
                            viewModel.saveAppRule(pkg, usageLimitMinutes, lockoutDurationMinutes)
                            try {
                                AppMonitoringService.start(context)
                            } catch (_: Exception) {}
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE RULE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Secondary Action: Instant Lockout
                    OutlinedButton(
                        onClick = {
                            viewModel.saveAppRule(pkg, usageLimitMinutes, lockoutDurationMinutes)
                            viewModel.startInstantLockSessionForApp(pkg, lockoutDurationMinutes)
                            try {
                                AppMonitoringService.start(context)
                            } catch (_: Exception) {}
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOCK IMMEDIATELY ($lockoutDurationMinutes MIN)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Explanation Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "If you use ${appInfo?.name ?: "this app"} for more than $usageLimitMinutes min, LockIn will automatically force close and block it for $lockoutDurationMinutes min.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Max Usage Limit Section
            Text(
                text = "1. Max Scrolling Time Limit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "How long can you use the app before getting locked out?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                usageLimitOptions.chunked(3).forEach { rowList ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowList.forEach { (duration, label) ->
                            val isSelected = usageLimitMinutes == duration
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                                    .clickable { usageLimitMinutes = duration }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        if (rowList.size < 3) {
                            Spacer(modifier = Modifier.weight((3 - rowList.size).toFloat()))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. Lockout Duration Section
            Text(
                text = "2. Lockout / Break Duration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "How long must the app stay locked after hitting the limit?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                lockoutDurationOptions.chunked(3).forEach { rowList ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowList.forEach { (duration, label) ->
                            val isSelected = lockoutDurationMinutes == duration
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                                    .clickable { lockoutDurationMinutes = duration }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        if (rowList.size < 3) {
                            Spacer(modifier = Modifier.weight((3 - rowList.size).toFloat()))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
