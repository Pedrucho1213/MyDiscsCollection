package com.example.mydiscscollection.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReleasesResponseDto(
    @param:Json(name = "releases") val releases: List<ReleaseDto>,
    @param:Json(name = "pagination") val pagination: PaginationDto

)
@JsonClass(generateAdapter = true)
data class ReleaseDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "resource_url") val resourceUrl: String,
    @param:Json(name = "year") val year: Int?,
    @param:Json(name = "thumb") val thumb: String?,
    @param:Json(name = "role") val role : String?,
    @param:Json(name = "type") val type : String?,
    @param:Json(name = "label") val label : String?,
    @param:Json(name = "format") val formats: String?,
    @param:Json(name = "genre") val genre: List<String>?
)
