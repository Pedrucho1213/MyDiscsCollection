package com.example.mydiscscollection.data.remote

import com.example.mydiscscollection.data.remote.dto.ArtistDetailDto
import com.example.mydiscscollection.data.remote.dto.ReleasesResponseDto
import com.example.mydiscscollection.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApiService {

    // Search Artist
    @GET("database/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchResponseDto


    // Detail Artist
    @GET("artists/{id}")
    suspend fun getArtistDetail(
        @Path("id") artistId: Int
    ): ArtistDetailDto

    // Releases by artist
    @GET("artists/{id}/releases")
    suspend fun getArtistReleases(
        @Path("id") artistId: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "year",
        @Query("sort_order") sortOrder: String = "desc"
    ): ReleasesResponseDto

}