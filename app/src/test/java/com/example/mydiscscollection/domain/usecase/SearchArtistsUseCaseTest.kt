package com.example.mydiscscollection.domain.usecase

import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.repository.ArtistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchArtistsUseCaseTest {
    private val repository: ArtistRepository = mockk()
    private val useCase = SearchArtistsUseCase(repository)

    @Test
    fun `returns success when repository succeeds`() = runTest {
        val fakeArtists = listOf(
            Artist(id = 1, name = "David Bowie", imageUrl = null),
        )
        coEvery {
            repository.searchArtists("David Bowie", 1)
        } returns Result.success(Triple(fakeArtists, 1, 1))

        val result = useCase("David Bowie", page = 1)

        TestCase.assertTrue(result.isSuccess)
        TestCase.assertEquals(1, result.getOrNull()?.first?.size)
    }

    @Test
    fun `returns failure when repository fails`() = runTest {
        coEvery {
            repository.searchArtists(any(), any())
        } returns Result.failure(Exception("Network error"))

        val result = useCase("Beatles")

        TestCase.assertTrue(result.isFailure)
        TestCase.assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `uses page 1 by default`() = runTest {
        coEvery {
            repository.searchArtists(any(), 1)
        } returns Result.success(Triple(emptyList(), 0, 0))

        useCase("Beatles")

        coVerify { repository.searchArtists("Beatles", 1) }
    }

    @Test
    fun `delegates correct parameters to repository`() = runTest {
        coEvery {
            repository.searchArtists("Queen", 2)
        } returns Result.success(Triple(emptyList(), 0, 0))

        useCase("Queen", page = 2)

        coVerify(exactly = 1) { repository.searchArtists("Queen", 2) }
    }

}