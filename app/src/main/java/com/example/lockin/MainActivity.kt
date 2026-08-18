package com.example.lockin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.lockin.ui.navigation.BottomNavigationBar
import com.example.lockin.ui.navigation.LockInNavGraph
import com.example.lockin.ui.screens.MainViewModel
import com.example.lockin.ui.theme.LockInTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LockInTheme {
                val navController = rememberNavController()
                // Create ONE shared ViewModel at Activity scope so _selectedApps
                // persists across ALL screen navigations (AppSelection → LockSetup → Home)
                val sharedViewModel: MainViewModel = hiltViewModel()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.example.lockin.ui.theme.Background
                ) {
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController = navController) },
                        containerColor = com.example.lockin.ui.theme.Background
                    ) { paddingValues ->
                        LockInNavGraph(
                            navController = navController,
                            sharedViewModel = sharedViewModel,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}
