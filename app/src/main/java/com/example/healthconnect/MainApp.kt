package com.example.healthconnect

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthconnect.helper.HealthConnectManager
import com.example.healthconnect.navigation.MainNavigation
import com.example.healthconnect.ui.theme.HealthConnectTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainApp(healthConnectManager: HealthConnectManager) {
    HealthConnectTheme {
        val scaffoldState = rememberScaffoldState()
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val availability by healthConnectManager.availability


        Scaffold(scaffoldState = scaffoldState) {
            MainNavigation(
                healthConnectManager = healthConnectManager,
                navController = navController,
                scaffoldState = scaffoldState
            )
        }
    }
}