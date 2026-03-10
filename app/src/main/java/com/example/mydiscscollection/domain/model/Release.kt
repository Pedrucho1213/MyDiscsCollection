package com.example.mydiscscollection.domain.model

data class Release(
    val id: Int,
    val title: String,
    val year: Int?,
    val thumbUrl: String?,
    val genre: String?,
    val label: String?,
    val format: String?,
    val role: String?
)
