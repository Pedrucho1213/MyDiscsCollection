package com.example.mydiscscollection.data.remote.mapper

import com.example.mydiscscollection.data.remote.dto.ArtistSearchResultDto
import com.example.mydiscscollection.domain.model.Artist

object ArtistMapper {

    fun ArtistSearchResultDto.toDomain(): Artist = Artist(
        id = id,
        name = title,
        imageUrl = thumb?.takeIf { it.isNotBlank() }
    )
}