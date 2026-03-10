package com.example.mydiscscollection.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydiscscollection.domain.usecase.GetArtistDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsDetailViewModel @Inject constructor(
    private val getArtistDetail: GetArtistDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val artistId: Int = checkNotNull(savedStateHandle["artistId"])

    private val _uiState = MutableStateFlow<ArtistDetailUiState>(ArtistDetailUiState.Loading)
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    init {
        loadArtist()
    }

    private fun loadArtist() {
        viewModelScope.launch {
            _uiState.value = ArtistDetailUiState.Loading
            getArtistDetail(artistId)
                .onSuccess { artistId ->
                    _uiState.value = ArtistDetailUiState.Success(artistId)
                }
                .onFailure { error ->
                    _uiState.value = ArtistDetailUiState.Error(
                        error.message ?: "Unknown error"
                    )
                }
        }
    }

    fun retry() = loadArtist()
}