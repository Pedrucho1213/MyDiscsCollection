package com.example.mydiscscollection.domain.usecase

import com.example.mydiscscollection.domain.model.Release
import com.example.mydiscscollection.domain.repository.ArtistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetArtistReleasesUseCaseTest {
    private val repository: ArtistRepository = mockk()
    private val useCase = GetArtistReleasesUseCase(repository)

    @Test
    fun `returns success when repository succeeds`() = runTest {
        val fakeReleases = listOf(
            Release(
                id = 1,
                title = "Blackstar",
                year = 2016,
                genre = "Art Rock",
                label = "Columbia",
                thumbUrl = null,
                format = "",
                role = ""
            ),
            Release(id = 2, title = "The Next Day", year = 2013, genre = "Art Rock", label = "Columbia", thumbUrl = null, format = "", role = ""),
        )
        coEvery {
            repository.getArtistReleases(100, 1)
        } returns Result.success(Triple(fakeReleases, 2, 1))

        val result = useCase(100, page = 1)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.first?.size)
    }

    @Test
    fun `returns failure when repository fails`() = runTest {
        coEvery {
            repository.getArtistReleases(any(), any())
        } returns Result.failure(Exception("Timeout"))

        val result = useCase(100)

        assertTrue(result.isFailure)
        assertEquals("Timeout", result.exceptionOrNull()?.message)
    }

    @Test
    fun `uses page 1 by default`() = runTest {
        coEvery {
            repository.getArtistReleases(any(), 1)
        } returns Result.success(Triple(emptyList(), 0, 0))

        useCase(100)

        coVerify { repository.getArtistReleases(100, 1) }
    }

    @Test
    fun `delegates correct parameters to repository`() = runTest {
        coEvery {
            repository.getArtistReleases(100, 3)
        } returns Result.success(Triple(emptyList(), 0, 0))

        useCase(100, page = 3)

        coVerify(exactly = 1) { repository.getArtistReleases(100, 3) }
    }
}