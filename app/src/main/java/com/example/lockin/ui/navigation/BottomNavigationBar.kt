package com.example.lockin.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show on these specific screens
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.GamesDashboard.route,
        Screen.Stats.route,
        Screen.History.route
    )

    if (!showBottomBar) return

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Shield,
                label = "Home",
                isSelected = currentRoute == Screen.Home.route,
                onClick = {
                    if (currentRoute != Screen.Home.route) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                }
            )
            
            NavItem(
                icon = Icons.Default.Extension,
                label = "Games",
                isSelected = currentRoute == Screen.GamesDashboard.route,
                onClick = {
                    if (currentRoute != Screen.GamesDashboard.route) {
                        navController.navigate(Screen.GamesDashboard.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                }
            )
            
            NavItem(
                icon = Icons.Default.Leaderboard,
                label = "Stats",
                isSelected = currentRoute == Screen.Stats.route,
                onClick = {
                    if (currentRoute != Screen.Stats.route) {
                        navController.navigate(Screen.Stats.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                }
            )

            NavItem(
                icon = Icons.Default.History,
                label = "History",
                isSelected = currentRoute == Screen.History.route,
                onClick = {
                    if (currentRoute != Screen.History.route) {
                        navController.navigate(Screen.History.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
