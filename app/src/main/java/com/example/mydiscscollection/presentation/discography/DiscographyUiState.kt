package com.example.mydiscscollection.presentation.discography

import com.example.mydiscscollection.domain.model.Release

sealed interface DiscographyUiState {
    data object Loading : DiscographyUiState
    data class Success(
        val releases: List<Release>,
        val availableYears: List<Int>,
        val availableGenres: List<String>,
        val availableLabels: List<String>,
        // active filters

        val activeYear: Int? = null,
        val activeGenre: String? = null,
        val activeLabel: String? = null
    ) : DiscographyUiState {
        val filteredReleases: List<Release>
            get() = releases.filter { release ->
                (activeYear == null || release.year == activeYear) &&
                        (activeGenre == null || release.genre == activeGenre) &&
                        (activeLabel == null || release.label == activeLabel)
            }
    }

    data object EmptyFilter : DiscographyUiState
    data class Error(val message: String) : DiscographyUiState


}