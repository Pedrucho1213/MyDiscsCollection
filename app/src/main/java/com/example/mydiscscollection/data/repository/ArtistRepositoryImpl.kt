package com.example.mydiscscollection.data.repository

import com.example.mydiscscollection.data.remote.DiscogsApiService
import com.example.mydiscscollection.data.remote.mapper.ArtistMapper.toDomain
import com.example.mydiscscollection.di.IoDispatcher
import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.repository.ArtistRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val apiService: DiscogsApiService,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ArtistRepository {

    override suspend fun searchArtist(
        query: String,
        page: Int
    ): Result<List<Artist>> =
        withContext(ioDispatcher){
            runCatching {
                apiService.searchArtists(query = query, page = page)
                    .results
                    .map { it.toDomain() }
            }
        }
}
