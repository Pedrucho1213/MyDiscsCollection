package com.example.mydiscscollection.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDetailDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "profile") val profile: String?,
    @param:Json(name = "images") val images: List<ImageDto>?,
    @param:Json(name = "members") val members: List<MemberDto>?,
    @param:Json(name = "releases_url") val releasesUrl: String
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    @param:Json(name = "type") val type: String,
    @param:Json(name = "uri") val uri: String,
)

@JsonClass(generateAdapter = true)
data class MemberDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "active") val active: Boolean,
)