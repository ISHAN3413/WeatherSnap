package com.oceanx.weathersnap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oceanx.weathersnap.ui.screen.CameraScreen
import com.oceanx.weathersnap.ui.screen.CreateReportScreen
import com.oceanx.weathersnap.ui.screen.SavedReportsScreen
import com.oceanx.weathersnap.ui.screen.WeatherScreen
import com.oceanx.weathersnap.ui.viewmodel.ReportViewModel
import com.oceanx.weathersnap.ui.viewmodel.SharedWeatherViewModel
import com.oceanx.weathersnap.ui.viewmodel.WeatherViewModel

sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object CreateReport : Screen("createReport")
    object Camera : Screen("camera")
    object SavedReports : Screen("savedReports")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route
    ) {
        composable(Screen.Weather.route) { backStackEntry ->
            val weatherVm: WeatherViewModel = hiltViewModel()
            val sharedVm: SharedWeatherViewModel = hiltViewModel()

            WeatherScreen(
                viewModel = weatherVm,
                onCreateReport = { weather ->
                    sharedVm.setWeather(weather)
                    navController.navigate(Screen.CreateReport.route)
                },
                onViewReports = {
                    navController.navigate(Screen.SavedReports.route)
                }
            )
        }

        composable(Screen.CreateReport.route) { backStackEntry ->
            val reportVm: ReportViewModel = hiltViewModel()
            val weatherEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Weather.route)
            }
            val sharedVm: SharedWeatherViewModel = hiltViewModel(weatherEntry)

            CreateReportScreen(
                reportViewModel = reportVm,
                sharedWeatherViewModel = sharedVm,
                savedStateHandle = backStackEntry.savedStateHandle,
                onOpenCamera = {
                    navController.navigate(Screen.Camera.route)
                },
                onSaved = {
                    navController.navigate(Screen.SavedReports.route) {
                        popUpTo(Screen.Weather.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { path ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle?.set("imagePath", path)
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }

        composable(Screen.SavedReports.route) {
            SavedReportsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}