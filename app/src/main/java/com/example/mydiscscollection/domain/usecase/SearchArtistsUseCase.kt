package com.example.mydiscscollection.domain.usecase

import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.repository.ArtistRepository
import javax.inject.Inject

class SearchArtistsUseCase @Inject constructor(
    private val repository: ArtistRepository
){
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<Artist>>{
        if (query.isBlank()) return Result.success(emptyList())
        return repository.searchArtist(query, page)
    }
}