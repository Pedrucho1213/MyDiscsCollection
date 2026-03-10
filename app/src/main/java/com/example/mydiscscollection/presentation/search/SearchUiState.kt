package com.example.mydiscscollection.presentation.search

import com.example.mydiscscollection.domain.model.Artist

sealed interface SearchUiState {
    data object Idle: SearchUiState
    data object Loading: SearchUiState
    data class Results(
        val artists: List<Artist>,
        val query: String,
        val totalItem: Int,
        val currentPage: Int = 1,
        val loadingMore: Boolean = false,
        val hasMore: Boolean = true
    ): SearchUiState
    data object Empty: SearchUiState
    data class Error(val message: String): SearchUiState

}