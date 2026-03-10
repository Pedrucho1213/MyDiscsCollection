package com.example.mydiscscollection.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mydiscscollection.presentation.detail.ArtistDetailScreen
import com.example.mydiscscollection.presentation.discography.DiscographyScreen
import com.example.mydiscscollection.presentation.search.SearchScreen

@Composable
fun DiscogsNavGraph(
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = Screen.Search.route
    ){
        composable(Screen.Search.route){
            SearchScreen(
               onArtistClick = { artistId ->
                   navController.navigate(Screen.ArtistDetail.createRoute(artistId))
               }
            )
        }
        composable(
            Screen.ArtistDetail.route,
            arguments = listOf(
                navArgument("artistId"){
                    type = NavType.IntType
                }
            )
            ){
            ArtistDetailScreen(
                onViewAlbumsClick = { artistId ->
                    navController.navigate(Screen.Discography.createRoute(artistId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Discography.route,
            arguments = listOf(
                navArgument("artistId"){
                    type = NavType.IntType
                }
            )
        ){
            DiscographyScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )

        }
    }
}