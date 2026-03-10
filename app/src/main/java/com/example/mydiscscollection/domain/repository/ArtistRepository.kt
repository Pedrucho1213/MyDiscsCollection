package com.example.mydiscscollection.domain.repository

import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.model.ArtistDetail
import com.example.mydiscscollection.domain.model.Release

interface  ArtistRepository {
    suspend fun searchArtists(query: String, page: Int): Result<Triple<List<Artist>, Int, Int>>
    suspend fun getArtistDetail(artistId: Int): Result<ArtistDetail>
    suspend fun getArtistReleases(artistId: Int, page: Int): Result<Triple<List<Release>, Int, Int>>

}