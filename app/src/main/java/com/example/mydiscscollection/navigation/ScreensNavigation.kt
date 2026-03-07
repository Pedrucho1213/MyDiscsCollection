package com.example.mydiscscollection.navigation

sealed class ScreensNavigation(val route: String) {
    data object Search: ScreensNavigation("Search")
}