package com.example.mydiscscollection.domain.repository

import com.example.mydiscscollection.domain.model.Artist

interface  ArtistRepository {
    suspend fun searchArtist(query: String, page: Int): Result<List<Artist>>
}