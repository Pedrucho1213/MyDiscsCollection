package com.example.mydiscscollection.domain.repository

import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.model.ArtistDetail
import com.example.mydiscscollection.domain.model.Release

interface  ArtistRepository {
    suspend fun searchArtist(query: String, page: Int): Result<List<Artist>>
    suspend fun getArtistDetail(artistId: Int): Result<ArtistDetail>
    suspend fun getArtistReleases(artistId: Int, page: Int): Result<List<Release>>

}