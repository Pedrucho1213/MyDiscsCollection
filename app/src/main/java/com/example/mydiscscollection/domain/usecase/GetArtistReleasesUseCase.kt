package com.example.mydiscscollection.domain.usecase

import com.example.mydiscscollection.domain.model.Release
import com.example.mydiscscollection.domain.repository.ArtistRepository
import javax.inject.Inject

class GetArtistReleasesUseCase @Inject constructor(
    private val repository: ArtistRepository
){
    suspend operator fun invoke(artistId: Int, page: Int = 1): Result<List<Release>> =
        repository.getArtistReleases(artistId, page)
}