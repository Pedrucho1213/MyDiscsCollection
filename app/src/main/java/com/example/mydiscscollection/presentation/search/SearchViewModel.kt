package com.example.mydiscscollection.presentation.search

import androidx.compose.runtime.MutableState
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

    fun onQueryChanged(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }
        _uiState.value = SearchUiState.Loading

        searchJob = viewModelScope.launch {
            delay(300)
            searchArtists(query)
                .onSuccess { artists ->
                    _uiState.value = if (artists.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Results(artists, query, artists.size)
                    }
                }
                .onFailure { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: "Unknown error")
                }
        }


    }

    fun retry(query: String) {
        onQueryChanged(query)
    }
}
