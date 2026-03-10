package com.example.mydiscscollection.presentation.search

import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.usecase.SearchArtistsUseCase
import com.example.mydiscscollection.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchArtists: SearchArtistsUseCase = mockk()

    @Test
    fun `onQueryChanged emits results after debounce when search succeeds`() = runTest {
        val artists = listOf(
            Artist(id = 1, name = "Muse", imageUrl = null),
        )
        coEvery {
            searchArtists.invoke("Muse", 1)
        } returns Result.success(Triple(artists, 1, 1))

        val viewModel = SearchViewModel(searchArtists)

        viewModel.onQueryChanged("Muse")

        assertEquals(SearchUiState.Loading, viewModel.uiState.value)
        advanceTimeBy(299)
        coVerify(exactly = 0) { searchArtists.invoke(any(), any()) }

        advanceTimeBy(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value as SearchUiState.Results
        assertEquals("Muse", state.query)
        assertEquals(artists, state.artists)
        assertEquals(1, state.totalItem)
        assertFalse(state.hasMore)
    }

    @Test
    fun `onQueryChanged emits error when search fails`() = runTest {
        coEvery {
            searchArtists.invoke("Blur", 1)
        } returns Result.failure(Exception("Network error"))

        val viewModel = SearchViewModel(searchArtists)

        viewModel.onQueryChanged("Blur")
        advanceTimeBy(300)
        advanceUntilIdle()

        val state = viewModel.uiState.value as SearchUiState.Error
        assertEquals("Network error", state.message)
    }

    @Test
    fun `loadNextPage appends artists and updates pagination state`() = runTest {
        val firstPageArtists = listOf(
            Artist(id = 1, name = "Daft Punk", imageUrl = null),
        )
        val secondPageArtists = listOf(
            Artist(id = 2, name = "Justice", imageUrl = null),
        )
        coEvery {
            searchArtists.invoke("Electronic", 1)
        } returns Result.success(Triple(firstPageArtists, 2, 2))
        coEvery {
            searchArtists.invoke("Electronic", 2)
        } returns Result.success(Triple(secondPageArtists, 2, 2))

        val viewModel = SearchViewModel(searchArtists)

        viewModel.onQueryChanged("Electronic")
        advanceTimeBy(300)
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as SearchUiState.Results
        assertEquals(listOf(1, 2), state.artists.map { it.id })
        assertEquals(2, state.currentPage)
        assertFalse(state.loadingMore)
        assertFalse(state.hasMore)
        coVerify(exactly = 1) { searchArtists.invoke("Electronic", 2) }
    }
}
