package com.example.healthconnect.navigation

import com.example.healthconnect.R

enum class Screen (val route: String, val titleId: Int, val hasMenuItem: Boolean = true) {
    HomeScreen(route = "home_screen", R.string.home_screen, false)
}