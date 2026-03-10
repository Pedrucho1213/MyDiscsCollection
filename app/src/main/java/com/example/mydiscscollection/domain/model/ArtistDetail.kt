package com.example.mydiscscollection.domain.model

data class ArtistDetail(
    val id: Int,
    val name: String,
    val biography: String,
    val imageUrl: String?,
    val members: List<BandMember>,
    val releasesUrl: String
)

data class BandMember(
    val id: Int,
    val name: String,
    val isActive: Boolean
)
