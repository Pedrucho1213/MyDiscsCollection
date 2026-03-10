package com.example.mydiscscollection.data.repository

import com.example.mydiscscollection.data.remote.DiscogsApiService
import com.example.mydiscscollection.data.remote.mapper.ArtistMapper.toDomain
import com.example.mydiscscollection.di.IoDispatcher
import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.model.ArtistDetail
import com.example.mydiscscollection.domain.model.Release
import com.example.mydiscscollection.domain.repository.ArtistRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val apiService: DiscogsApiService,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ArtistRepository {

    override suspend fun searchArtists(
        query: String,
        page: Int,
    ): Result<Triple<List<Artist>, Int, Int>> = runCatching {
        val response = apiService.searchArtists(
            query   = query,
            page    = page,
            perPage = 30,
        )
        Triple(
            response.results.map { it.toDomain() },
            response.pagination.items,
            response.pagination.pages,
        )
    }


    override suspend fun getArtistDetail(artistId: Int): Result<ArtistDetail> =
        runCatching {
            apiService.getArtistDetail(artistId).toDomain()
        }


    override suspend fun getArtistReleases(
        artistId: Int,
        page: Int,
    ): Result<Triple<List<Release>, Int, Int>> = runCatching {
        val response = apiService.getArtistReleases(
            artistId = artistId,
            page     = page,
            perPage  = 30,
        )
        Triple(
            response.releases.map { it.toDomain() },
            response.pagination.items,
            response.pagination.pages,
        )
    }
}
