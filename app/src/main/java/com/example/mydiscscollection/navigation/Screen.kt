package com.example.mydiscscollection.navigation

sealed class Screen(val route: String) {
    data object Search: Screen("Search")

    data object ArtistDetail : Screen("artist_detail/{artistId}"){
        fun createRoute(artistId: Int) = "artist_detail/$artistId"
    }

    data object Discography: Screen("discography/{artistId}"){
        fun createRoute(artistId: Int) = "discography/$artistId"

    }
}