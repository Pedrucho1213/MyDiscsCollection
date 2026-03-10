package com.example.mydiscscollection.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReleaseMetadataDto(
    @param:Json(name = "genres") val genres: List<String>?,
    @param:Json(name = "released") val released: String?
)
