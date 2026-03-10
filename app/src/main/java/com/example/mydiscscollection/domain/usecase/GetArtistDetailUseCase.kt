package com.example.mydiscscollection.domain.usecase

import com.example.mydiscscollection.domain.repository.ArtistRepository
import javax.inject.Inject

class GetArtistDetailUseCase @Inject constructor(
    private val repository: ArtistRepository
){
    suspend operator fun invoke(artistId: Int) =
        repository.getArtistDetail(artistId)
}