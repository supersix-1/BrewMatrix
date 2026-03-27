package com.brewmatrix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brewmatrix.app.navigation.BrewMatrixNavHost
import com.brewmatrix.app.navigation.BrewMatrixRoute
import com.brewmatrix.app.navigation.SettingsRoutes
import com.brewmatrix.app.ui.theme.BrewMatrixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContainer = (application as BrewMatrixApp).appContainer
            val themeMode by appContainer.preferencesRepository.themeMode
                .collectAsState(initial = "system")

            BrewMatrixTheme(themeMode = themeMode) {
                // Sync status bar icon color with theme
                val isDark = when (themeMode) {
                    "light" -> false
                    "dark" -> true
                    else -> isSystemInDarkTheme()
                }
                SideEffect {
                    WindowCompat.getInsetsController(window, window.decorView)
                        .isAppearanceLightStatusBars = !isDark
                }

                BrewMatrixMainScreen()
            }
        }
    }
}

@Composable
private fun BrewMatrixMainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val isOnMainTab = BrewMatrixRoute.entries.any { it.name == currentRoute }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                AnimatedVisibility(
                    visible = isOnMainTab,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        BrewMatrixRoute.entries.forEach { route ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route == route.name
                            } == true

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(route.name) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = route.icon,
                                        contentDescription = route.label,
                                    )
                                },
                                label = {
                                    Text(
                                        text = route.label,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                                ),
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            BrewMatrixNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }

        // Gear icon — floating overlay, no impact on layout
        AnimatedVisibility(
            visible = isOnMainTab,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 4.dp),
        ) {
            IconButton(
                onClick = {
                    navController.navigate(SettingsRoutes.SETTINGS) {
                        launchSingleTop = true
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
