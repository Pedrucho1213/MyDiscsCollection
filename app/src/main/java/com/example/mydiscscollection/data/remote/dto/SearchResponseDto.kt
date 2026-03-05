package com.example.mydiscscollection.data.remote.dto

import com.squareup.moshi.Json

data class SearchResponseDto(
    @param:Json(name = "pagination") val pagination : PaginationDto,
    @param:Json(name = "results") val results: List<ArtistSearchResultDto>
)

data class PaginationDto (
    @param:Json(name = "page") val page : Int,
    @param:Json(name = "pages") val pages : Int,
    @param:Json(name = "per_page") val perPage : Int,
    @param:Json(name = "items") val items : Int
)

data class ArtistSearchResultDto (
    @param:Json(name = "id") val id : Int,
    @param:Json(name = "title") val title : String,
    @param:Json(name = "thumb") val thumb : String?,
    @param:Json(name = "type") val type : String
)