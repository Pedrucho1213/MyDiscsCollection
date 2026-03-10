package com.example.mydiscscollection.data.repository

import com.example.mydiscscollection.data.remote.DiscogsApiService
import com.example.mydiscscollection.data.remote.mapper.ArtistMapper.toDomain
import com.example.mydiscscollection.di.IoDispatcher
import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.model.ArtistDetail
import com.example.mydiscscollection.domain.model.Release
import com.example.mydiscscollection.domain.repository.ArtistRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
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
        withContext(ioDispatcher) {
            runCatching {
                apiService.searchArtists(query = query, page = page)
                    .results
                    .map { it.toDomain() }
            }
        }

    override suspend fun getArtistDetail(artistId: Int): Result<ArtistDetail> =
        runCatching {
            apiService.getArtistDetail(artistId).toDomain()
        }


    override suspend fun getArtistReleases(
        artistId: Int,
        page: Int
    ): Result<List<Release>> =
        withContext(ioDispatcher) {
            runCatching {
                val releaseDtos = apiService.getArtistReleases(
                    artistId = artistId,
                    page = page
                ).releases

                coroutineScope {
                    releaseDtos
                        .map { releaseDto ->
                            async {
                                val genreFromMetadata = runCatching {
                                    apiService.getReleaseMetadata(releaseDto.resourceUrl)
                                        .genres
                                        ?.firstOrNull()
                                        ?.takeIf { it.isNotBlank() }
                                }.getOrNull()

                                releaseDto.toDomain(genreOverride = genreFromMetadata)
                            }
                        }
                        .awaitAll()
                }
            }
        }
}
