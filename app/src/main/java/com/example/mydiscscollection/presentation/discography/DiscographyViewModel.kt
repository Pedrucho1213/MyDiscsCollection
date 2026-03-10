package com.example.mydiscscollection.presentation.discography

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydiscscollection.domain.model.Release
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
            getArtistReleases(artistId, page = 1)
                .onSuccess{ (releases, totalItems, totalPages) ->
                    val sortedReleases = releases.sortedByReleaseDateDescending()
                    _uiState.value = DiscographyUiState.Success(
                        releases = sortedReleases,
                        totalItems = totalItems,
                        currentPage = 1,
                        hasMore = totalPages > 1,
                        availableYears = sortedReleases.mapNotNull { it.year }.distinct().sorted().reversed(),
                        availableGenres = sortedReleases.mapNotNull { it.genre }.distinct().sorted(),
                        availableLabels = sortedReleases.mapNotNull { it.label }.distinct().sorted()
                    )
                }
                .onFailure {
                    _uiState.value = DiscographyUiState.Error(it.message ?: "Unknown error")
                }
        }
    }
    fun loadNextPage() {
        val current = _uiState.value as? DiscographyUiState.Success ?: return
        if (current.loadingMore || !current.hasMore) return

        viewModelScope.launch {
            _uiState.value = current.copy(loadingMore = true)

            getArtistReleases(artistId, page = current.currentPage + 1)
                .onSuccess { (newReleases, _, totalPages) ->
                    val nextPage = current.currentPage + 1
                    val mergedReleases = (current.releases + newReleases)
                        .sortedByReleaseDateDescending()
                    _uiState.value = current.copy(
                        releases = mergedReleases,
                        currentPage = nextPage,
                        loadingMore = false,
                        hasMore = nextPage < totalPages,
                        availableYears = mergedReleases.mapNotNull { it.year }.distinct().sorted().reversed(),
                        availableGenres = mergedReleases.mapNotNull { it.genre }.distinct().sorted(),
                        availableLabels = mergedReleases.mapNotNull { it.label }.distinct().sorted(),
                    )
                }
                .onFailure {
                    _uiState.value = current.copy(loadingMore = false)
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

private fun List<Release>.sortedByReleaseDateDescending(): List<Release> =
    sortedWith(
        compareByDescending<Release> { it.releaseSortKey() }
            .thenByDescending { it.title }
    )

private fun Release.releaseSortKey(): Long {
    val parts = releasedOn
        ?.split("-")
        ?.map { value -> value.toIntOrNull() ?: 0 }
        .orEmpty()

    val yearPart = parts.getOrNull(0) ?: (year ?: 0)
    val monthPart = parts.getOrNull(1) ?: 0
    val dayPart = parts.getOrNull(2) ?: 0

    return (yearPart.toLong() * 10_000L) + (monthPart.toLong() * 100L) + dayPart.toLong()
}
