package com.example.mydiscscollection.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mydiscscollection.presentation.search.SearchScreen

@Composable
fun DiscogsNavGraph(
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = ScreensNavigation.Search.route
    ){
        composable(ScreensNavigation.Search.route){
            SearchScreen(
               onArtistClick = { artistId ->
                   navController.navigate("")
               }
            )
        }
    }
}