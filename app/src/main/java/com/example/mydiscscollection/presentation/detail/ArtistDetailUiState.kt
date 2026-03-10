package com.example.mydiscscollection.presentation.detail

import com.example.mydiscscollection.domain.model.ArtistDetail

sealed interface ArtistDetailUiState {
    data object Loading: ArtistDetailUiState
    data class Success(val artistDetail: ArtistDetail): ArtistDetailUiState
    data class Error(val message: String): ArtistDetailUiState
}