package com.example.lockin.ui.navigation

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Onboarding  : Screen("onboarding")
    object Permissions : Screen("permissions")
    object Home        : Screen("home")
    object AppSelection: Screen("app_selection")
    object LockSetup   : Screen("lock_setup")
    object ActiveLock  : Screen("active_lock")
    object History     : Screen("history")
    object MiniGame    : Screen("mini_game")
    object Stats       : Screen("stats")
    object GamesDashboard : Screen("games_dashboard")
}
