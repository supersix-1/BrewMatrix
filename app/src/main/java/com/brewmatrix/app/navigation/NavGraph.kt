package com.brewmatrix.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.brewmatrix.app.ui.calculator.CalculatorScreen
import com.brewmatrix.app.ui.timer.TimerScreen
import com.brewmatrix.app.ui.grindmemory.GrindMemoryScreen
import com.brewmatrix.app.ui.brewlog.BrewLogScreen

enum class BrewMatrixRoute(
    val label: String,
    val icon: ImageVector,
) {
    Calculator(label = "Calculator", icon = Icons.Filled.Calculate),
    Timer(label = "Timer", icon = Icons.Filled.Timer),
    GrindMemory(label = "Grind", icon = Icons.Filled.Tune),
    BrewLog(label = "Log", icon = Icons.AutoMirrored.Filled.MenuBook),
}

@Composable
fun BrewMatrixNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = BrewMatrixRoute.Calculator.name,
        modifier = modifier,
    ) {
        composable(BrewMatrixRoute.Calculator.name) {
            CalculatorScreen()
        }
        composable(BrewMatrixRoute.Timer.name) {
            TimerScreen()
        }
        composable(BrewMatrixRoute.GrindMemory.name) {
            GrindMemoryScreen()
        }
        composable(BrewMatrixRoute.BrewLog.name) {
            BrewLogScreen()
        }
    }
}
