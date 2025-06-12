package com.example.healthconnect.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthconnect.MainViewModel
import com.example.healthconnect.MainViewModelFactory
import com.example.healthconnect.helper.HealthConnectManager
import com.example.healthconnect.screens.HomeScreen

@Composable
fun MainNavigation(navController: NavHostController,
                   healthConnectManager: HealthConnectManager,
                   scaffoldState: ScaffoldState) {

    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route ) {
        val availability by healthConnectManager.availability
        Log.d("MainNavigation", "Health Connect availability = $availability")

        composable(Screen.HomeScreen.route) {
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(healthConnectManager))
            val permissionsGranted by viewModel.permissionsGranted
            val sessionsList by viewModel.sessionsList
            val permissions = viewModel.permissions
            val backgroundReadPermissions = viewModel.backgroundReadPermissions
            val backgroundReadAvailable by viewModel.backgroundReadAvailable
            val backgroundReadGranted by viewModel.backgroundReadGranted
            val historyReadPermissions = viewModel.historyReadPermissions
            val historyReadAvailable by viewModel.historyReadAvailable
            val historyReadGranted by viewModel.historyReadGranted
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher = rememberLauncherForActivityResult(viewModel.permissionsLauncher) { grantedPermissions ->
                Log.d("ExerciseSessionScreen", "Granted permissions: $grantedPermissions")
                onPermissionsResult()
            }

            Log.d("MainNavigation", "permissionsGranted=${viewModel.logGrantedPermissions()}")
            HomeScreen(
                permissionsGranted = permissionsGranted,
                permissions = permissions,
                backgroundReadAvailable = backgroundReadAvailable,
                backgroundReadGranted = backgroundReadGranted,
                backgroundReadPermissions = backgroundReadPermissions,
                historyReadAvailable = historyReadAvailable,
                historyReadGranted = historyReadGranted,
                historyReadPermissions = historyReadPermissions,
                onBackgroundReadClick = {
                    viewModel.enqueueReadStepWorker()
                },
                sessionsList = sessionsList,
                uiState = viewModel.uiState,
                onInsertClick = {
                    viewModel.insertExerciseSession()
                },
                onDetailsClick = { },
                onError = { Log.d("MainNavigation", "Error: ${it?.message}") },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    Log.d("ExerciseSessionScreen", "Launching permission request for $values")
                    permissionsLauncher.launch(values)
                }
            )
        }
    }

}