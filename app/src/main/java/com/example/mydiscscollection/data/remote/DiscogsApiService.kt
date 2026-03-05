package com.example.mydiscscollection.data.remote

import com.example.mydiscscollection.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
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

}