package com.example.mydiscscollection.data.remote.mapper

import com.example.mydiscscollection.data.remote.dto.ArtistDetailDto
import com.example.mydiscscollection.data.remote.dto.ArtistSearchResultDto
import com.example.mydiscscollection.data.remote.dto.MemberDto
import com.example.mydiscscollection.data.remote.dto.ReleaseDto
import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.model.ArtistDetail
import com.example.mydiscscollection.domain.model.BandMember
import com.example.mydiscscollection.domain.model.Release

object ArtistMapper {

    fun ArtistSearchResultDto.toDomain(): Artist = Artist(
        id = id,
        name = title,
        imageUrl = thumb?.takeIf { it.isNotBlank() }
    )

    fun ArtistDetailDto.toDomain(): ArtistDetail = ArtistDetail(
        id = id,
        name = name,
        biography = profile.orEmpty(),
        imageUrl = images
            ?.firstOrNull{it.type == "primary"}?.uri
            ?: images?.firstOrNull()?.uri,
        members = members?.map { it.toDomain() } ?: emptyList(),
        releasesUrl = releasesUrl

    )

    fun MemberDto.toDomain(): BandMember = BandMember(
        id = id,
        name = name,
        isActive = active
    )

    fun ReleaseDto.toDomain(
        genreOverride: String? = null,
        releasedOnOverride: String? = null
    ): Release = Release(
        id = id,
        title = title,
        year = year,
        releasedOn = releasedOnOverride,
        thumbUrl = thumb?.takeIf { it.isNotBlank() },
        genre = (genreOverride ?: genre?.firstOrNull())
            ?.trim()
            ?.takeIf { it.isNotEmpty() },
        label = label,
        format = formats,
        role = role
        )
}
