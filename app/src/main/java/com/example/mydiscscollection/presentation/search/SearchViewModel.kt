package com.example.mydiscscollection.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydiscscollection.domain.usecase.SearchArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArtists: SearchArtistsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var currentQuery: String = ""

    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        currentQuery = query

        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }
        _uiState.value = SearchUiState.Loading

        searchJob = viewModelScope.launch {
            delay(300)
            searchArtists(query, page = 1)
                .onSuccess { (artists, totalItems, totalPages) ->  // ← fix aquí
                    _uiState.value = if (artists.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Results(
                            artists     = artists,
                            query       = query,
                            totalItem   = totalItems,
                            currentPage = 1,
                            hasMore     = totalPages > 1,          // ← fix aquí
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun loadNextPage() {
        val current = _uiState.value as? SearchUiState.Results ?: return
        if (current.loadingMore || !current.hasMore) return

        viewModelScope.launch {
            _uiState.value = current.copy(loadingMore = true)

            searchArtists(currentQuery, page = current.currentPage + 1)
                .onSuccess { (newArtists, _, totalPages) ->
                    val nextPage = current.currentPage + 1
                    _uiState.value = current.copy(
                        artists     = current.artists + newArtists,
                        currentPage = nextPage,
                        loadingMore = false,
                        hasMore     = nextPage < totalPages,
                    )
                }
                .onFailure {
                    _uiState.value = current.copy(loadingMore = false)
                }
        }
    }

    fun retry(query: String) = onQueryChanged(query)
}