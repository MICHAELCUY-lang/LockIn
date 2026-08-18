package com.example.lockin.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lockin.domain.model.AppInfo
import com.example.lockin.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val apps by viewModel.installedApps.collectAsState()
    val selected by viewModel.selectedApps.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) {
            apps
        } else {
            apps.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Distracting Apps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = selected.isNotEmpty(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = if (selected.isNotEmpty()) {
                                "ADD ${selected.size} APP${if (selected.size > 1) "S" else ""}"
                            } else {
                                "SELECT AT LEAST 1 APP"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
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
        ) {
            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = { Text("Search apps (e.g. TikTok, Instagram)...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            // Header summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredApps.size} apps found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                if (selected.isNotEmpty()) {
                    Text(
                        text = "${selected.size} selected",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No apps found matching \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        AppListItem(
                            app = app,
                            isSelected = selected.contains(app.packageName),
                            onToggle = { viewModel.toggleAppSelection(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppListItem(
    app: AppInfo,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            AppIconRenderer(
                drawable = app.icon,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    maxLines = 1
                )
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun AppIconRenderer(
    drawable: Drawable?,
    modifier: Modifier = Modifier
) {
    val imageBitmap = remember(drawable) {
        drawable?.let {
            try {
                val width = if (it.intrinsicWidth > 0) it.intrinsicWidth else 96
                val height = if (it.intrinsicHeight > 0) it.intrinsicHeight else 96
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                it.setBounds(0, 0, canvas.width, canvas.height)
                it.draw(canvas)
                bitmap.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(
                MaterialTheme.colorScheme.surfaceVariant,
                CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
