package com.example.lockin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lockin.ui.screens.*

@Composable
fun LockInNavGraph(
    navController: NavHostController,
    sharedViewModel: MainViewModel,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(route = Screen.Permissions.route) {
            PermissionsScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = sharedViewModel)
        }
        composable(route = Screen.AppSelection.route) {
            AppSelectionScreen(navController = navController, viewModel = sharedViewModel)
        }
        composable(route = Screen.LockSetup.route) {
            LockSetupScreen(navController = navController, viewModel = sharedViewModel)
        }
        composable(route = Screen.ActiveLock.route) {
            ActiveLockScreen(navController = navController, viewModel = sharedViewModel)
        }
        composable(route = Screen.History.route) {
            HistoryScreen(navController = navController, viewModel = sharedViewModel)
        }
        composable(route = Screen.Stats.route) {
            StatsScreen(navController = navController, viewModel = sharedViewModel)
        }
        composable(route = Screen.GamesDashboard.route) {
            GamesDashboardScreen(navController = navController, viewModel = sharedViewModel)
        }
        composable(route = Screen.MiniGame.route) {
            MiniGameScreen(navController = navController)
        }
    }
}
