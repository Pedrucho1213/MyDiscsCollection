package com.example.mydiscscollection.presentation.discography

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydiscscollection.domain.usecase.GetArtistReleasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscographyViewModel @Inject constructor(
    private val getArtistReleases: GetArtistReleasesUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val artistId: Int = checkNotNull(savedStateHandle["artistId"])

    private val _uiState = MutableStateFlow<DiscographyUiState>(DiscographyUiState.Loading)
    val uiState: StateFlow<DiscographyUiState> = _uiState.asStateFlow()

    init {
        loadReleases()
    }

    private fun loadReleases(){
        viewModelScope.launch {
            _uiState.value = DiscographyUiState.Loading
            getArtistReleases(artistId)
                .onSuccess{ releases ->
                    _uiState.value = DiscographyUiState.Success(
                        releases = releases,
                        availableYears = releases.mapNotNull { it.year }.distinct().sorted().reversed(),
                        availableGenres = releases.mapNotNull { it.genre }.distinct().sorted(),
                        availableLabels = releases.mapNotNull { it.label }.distinct().sorted()
                    )
                }
                .onFailure {
                    _uiState.value = DiscographyUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun applyYearFilter(year: Int?){
        val current = _uiState.value as? DiscographyUiState.Success ?: return
        _uiState.value = current.copy(activeYear = year)
    }

    fun applyGenreFilter(genre: String?){
        val current = _uiState.value as? DiscographyUiState.Success ?: return
        _uiState.value = current.copy(activeGenre = genre)
    }

    fun applyLabelFilter(label: String?){
        val current = _uiState.value as? DiscographyUiState.Success ?: return
        _uiState.value = current.copy(activeLabel = label)
    }

    fun clearFilters(){
        val current = _uiState.value as? DiscographyUiState.Success ?: return
        _uiState.value = current.copy(activeYear = null, activeGenre = null, activeLabel = null)
    }

    fun retry(){
        loadReleases()
    }
}