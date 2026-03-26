package com.brewmatrix.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.brewmatrix.app.BrewMatrixApp
import android.app.Application
import com.brewmatrix.app.ui.calculator.CalculatorScreen
import com.brewmatrix.app.ui.calculator.CalculatorViewModel
import com.brewmatrix.app.ui.timer.TimerScreen
import com.brewmatrix.app.ui.timer.TimerViewModel
import com.brewmatrix.app.ui.grindmemory.AddEditGrindSettingScreen
import com.brewmatrix.app.ui.grindmemory.GrindMemoryScreen
import com.brewmatrix.app.ui.grindmemory.GrindMemoryViewModel
import com.brewmatrix.app.ui.brewlog.BrewLogScreen
import com.brewmatrix.app.ui.brewlog.BrewLogViewModel

enum class BrewMatrixRoute(
    val label: String,
    val icon: ImageVector,
) {
    Calculator(label = "Calculator", icon = Icons.Filled.Calculate),
    Timer(label = "Timer", icon = Icons.Filled.Timer),
    GrindMemory(label = "Grind", icon = Icons.Filled.Tune),
    BrewLog(label = "Log", icon = Icons.AutoMirrored.Filled.MenuBook),
}

// Non-tab routes
object GrindMemoryRoutes {
    const val ADD = "grind_memory/add"
}

@Composable
fun BrewMatrixNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val appContainer = (LocalContext.current.applicationContext as BrewMatrixApp).appContainer

    NavHost(
        navController = navController,
        startDestination = BrewMatrixRoute.Calculator.name,
        modifier = modifier,
    ) {
        composable(BrewMatrixRoute.Calculator.name) {
            val viewModel: CalculatorViewModel = viewModel(
                factory = CalculatorViewModel.Factory(appContainer.ratioPresetRepository),
            )
            CalculatorScreen(
                viewModel = viewModel,
                onNavigateToTimer = {
                    navController.navigate(BrewMatrixRoute.Timer.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(BrewMatrixRoute.Timer.name) {
            val context = LocalContext.current
            val timerViewModel: TimerViewModel = viewModel(
                factory = TimerViewModel.Factory(
                    repository = appContainer.timerRepository,
                    application = context.applicationContext as Application,
                ),
            )
            TimerScreen(viewModel = timerViewModel)
        }
        composable(BrewMatrixRoute.GrindMemory.name) {
            val grindMemoryViewModel: GrindMemoryViewModel = viewModel(
                factory = GrindMemoryViewModel.Factory(
                    grindMemoryRepository = appContainer.grindMemoryRepository,
                    ratioPresetRepository = appContainer.ratioPresetRepository,
                    timerRepository = appContainer.timerRepository,
                ),
            )
            GrindMemoryScreen(
                viewModel = grindMemoryViewModel,
                onNavigateToAdd = {
                    navController.navigate(GrindMemoryRoutes.ADD)
                },
                onNavigateToCalculator = { _ ->
                    // Quick-Brew: navigate to Calculator tab
                    navController.navigate(BrewMatrixRoute.Calculator.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(GrindMemoryRoutes.ADD) {
            // Share the same ViewModel instance with the GrindMemory list
            val grindMemoryViewModel: GrindMemoryViewModel = viewModel(
                factory = GrindMemoryViewModel.Factory(
                    grindMemoryRepository = appContainer.grindMemoryRepository,
                    ratioPresetRepository = appContainer.ratioPresetRepository,
                    timerRepository = appContainer.timerRepository,
                ),
            )
            AddEditGrindSettingScreen(
                viewModel = grindMemoryViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(BrewMatrixRoute.BrewLog.name) {
            val brewLogViewModel: BrewLogViewModel = viewModel(
                factory = BrewLogViewModel.Factory(
                    brewLogRepository = appContainer.brewLogRepository,
                    grindMemoryRepository = appContainer.grindMemoryRepository,
                ),
            )
            BrewLogScreen(viewModel = brewLogViewModel)
        }
    }
}

