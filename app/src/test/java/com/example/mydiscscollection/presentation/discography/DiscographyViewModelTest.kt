package com.example.mydiscscollection.presentation.discography

import androidx.lifecycle.SavedStateHandle
import com.example.mydiscscollection.domain.model.Release
import com.example.mydiscscollection.domain.usecase.GetArtistReleasesUseCase
import com.example.mydiscscollection.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscographyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getArtistReleases: GetArtistReleasesUseCase = mockk()

    @Test
    fun `init sorts releases by full date descending and derives filter options`() = runTest {
        coEvery {
            getArtistReleases.invoke(7, 1)
        } returns Result.success(
            Triple(
                listOf(
                    release(
                        id = 1,
                        title = "January Release",
                        year = 2023,
                        releasedOn = "2023-01-10",
                        genre = "Rock",
                        label = "Columbia"
                    ),
                    release(
                        id = 2,
                        title = "September Release",
                        year = 2023,
                        releasedOn = "2023-09-21",
                        genre = "Electronic",
                        label = "Warp"
                    ),
                    release(
                        id = 3,
                        title = "Fallback Year Release",
                        year = 2024,
                        releasedOn = null,
                        genre = "Pop",
                        label = "Sony"
                    ),
                ),
                3,
                1
            )
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as DiscographyUiState.Success
        assertEquals(listOf(3, 2, 1), state.releases.map { it.id })
        assertEquals(listOf(2024, 2023), state.availableYears)
        assertEquals(listOf("Electronic", "Pop", "Rock"), state.availableGenres)
        assertEquals(listOf("Columbia", "Sony", "Warp"), state.availableLabels)
    }

    @Test
    fun `apply filters narrows filtered releases`() = runTest {
        coEvery {
            getArtistReleases.invoke(7, 1)
        } returns Result.success(
            Triple(
                listOf(
                    release(id = 1, title = "A", year = 2023, genre = "Rock", label = "EMI"),
                    release(id = 2, title = "B", year = 2022, genre = "Jazz", label = "Blue Note"),
                    release(id = 3, title = "C", year = 2023, genre = "Rock", label = "Sony"),
                ),
                3,
                1
            )
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.applyGenreFilter("Rock")
        viewModel.applyYearFilter(2023)
        viewModel.applyLabelFilter("Sony")

        val state = viewModel.uiState.value as DiscographyUiState.Success
        assertEquals(listOf(3), state.filteredReleases.map { it.id })
        assertEquals(2023, state.activeYear)
        assertEquals("Rock", state.activeGenre)
        assertEquals("Sony", state.activeLabel)
    }

    @Test
    fun `loadNextPage merges releases and keeps descending order`() = runTest {
        coEvery {
            getArtistReleases.invoke(7, 1)
        } returns Result.success(
            Triple(
                listOf(
                    release(id = 1, title = "Older", year = 2023, releasedOn = "2023-01-10"),
                ),
                2,
                2
            )
        )
        coEvery {
            getArtistReleases.invoke(7, 2)
        } returns Result.success(
            Triple(
                listOf(
                    release(id = 2, title = "Newest", year = 2024, releasedOn = "2024-11-05"),
                ),
                2,
                2
            )
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as DiscographyUiState.Success
        assertEquals(listOf(2, 1), state.releases.map { it.id })
        assertEquals(2, state.currentPage)
        assertFalse(state.loadingMore)
        assertFalse(state.hasMore)
        coVerify(exactly = 1) { getArtistReleases.invoke(7, 2) }
    }

    private fun createViewModel(): DiscographyViewModel =
        DiscographyViewModel(
            getArtistReleases = getArtistReleases,
            savedStateHandle = SavedStateHandle(mapOf("artistId" to 7))
        )

    private fun release(
        id: Int,
        title: String,
        year: Int,
        releasedOn: String? = null,
        genre: String? = null,
        label: String? = null
    ): Release = Release(
        id = id,
        title = title,
        year = year,
        releasedOn = releasedOn,
        thumbUrl = null,
        genre = genre,
        label = label,
        format = null,
        role = null
    )
}
