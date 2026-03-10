package com.example.mydiscscollection.data.remote.mapper

import com.example.mydiscscollection.data.remote.dto.ArtistDetailDto
import com.example.mydiscscollection.data.remote.dto.ArtistSearchResultDto
import com.example.mydiscscollection.data.remote.dto.ImageDto
import com.example.mydiscscollection.data.remote.dto.MemberDto
import com.example.mydiscscollection.data.remote.mapper.ArtistMapper.toDomain
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ArtistMapperTest {

    @Test
    fun `ArtistSearchResultDto maps id and name correctly`() {
        val dto = ArtistSearchResultDto(
            id = 100,
            title = "David Bowie",
            thumb = "https://image.url/photo.jpg",
            type = ""
        )

        val result = dto.toDomain()

        assertEquals(100, result.id)
        assertEquals("David Bowie", result.name)
    }

    @Test
    fun `ArtistSearchResultDto imageUrl is set when thumb is not blank`() {
        val dto = ArtistSearchResultDto(
            id    = 1,
            title = "Queen",
            thumb = "https://image.url/queen.jpg",
            type = ""
        )

        val result = dto.toDomain()

        assertEquals("https://image.url/queen.jpg", result.imageUrl)
    }

    @Test
    fun `ArtistSearchResultDto imageUrl is null when thumb is blank`() {
        val dto = ArtistSearchResultDto(
            id    = 1,
            title = "Queen",
            thumb = "",
            type = ""
        )

        val result = dto.toDomain()

        assertNull(result.imageUrl)
    }

    @Test
    fun `ArtistSearchResultDto imageUrl is null when thumb is null`() {
        val dto = ArtistSearchResultDto(
            id    = 1,
            title = "Queen",
            thumb = null,
            type = ""
        )

        val result = dto.toDomain()

        assertNull(result.imageUrl)
    }

    @Test
    fun `ArtistDetailDto maps basic fields correctly`() {
        val dto = ArtistDetailDto(
            id          = 200,
            name        = "The Beatles",
            profile     = "Iconic British band",
            images      = null,
            members     = null,
            releasesUrl = "https://api.discogs.com/artists/200/releases",
        )

        val result = dto.toDomain()

        assertEquals(200, result.id)
        assertEquals("The Beatles", result.name)
        assertEquals("Iconic British band", result.biography)
    }

    @Test
    fun `ArtistDetailDto biography is empty string when profile is null`() {
        val dto = ArtistDetailDto(
            id          = 1,
            name        = "Artist",
            profile     = null,
            images      = null,
            members     = null,
            releasesUrl = "",
        )

        val result = dto.toDomain()

        assertEquals("", result.biography)
    }

    @Test
    fun `ArtistDetailDto picks primary image first`() {
        val dto = ArtistDetailDto(
            id      = 1,
            name    = "Artist",
            profile = null,
            images  = listOf(
                ImageDto(type = "secondary", uri = "https://secondary.jpg"),
                ImageDto(type = "primary", uri = "https://primary.jpg"),
            ),
            members     = null,
            releasesUrl = "",
        )

        val result = dto.toDomain()

        assertEquals("https://primary.jpg", result.imageUrl)
    }

    @Test
    fun `ArtistDetailDto falls back to first image when no primary`() {
        val dto = ArtistDetailDto(
            id      = 1,
            name    = "Artist",
            profile = null,
            images  = listOf(
                ImageDto(type = "secondary", uri = "https://first.jpg"),
                ImageDto(type = "secondary", uri = "https://second.jpg"),
            ),
            members     = null,
            releasesUrl = "",
        )

        val result = dto.toDomain()

        assertEquals("https://first.jpg", result.imageUrl)
    }

    @Test
    fun `ArtistDetailDto imageUrl is null when images list is empty`() {
        val dto = ArtistDetailDto(
            id          = 1,
            name        = "Artist",
            profile     = null,
            images      = emptyList(),
            members     = null,
            releasesUrl = "",
        )

        val result = dto.toDomain()

        assertNull(result.imageUrl)
    }

    @Test
    fun `ArtistDetailDto maps members correctly`() {
        val dto = ArtistDetailDto(
            id = 1,
            name = "The Beatles",
            profile = null,
            images = null,
            members = listOf(
                MemberDto(id = 10, name = "John Lennon", active = true),
                MemberDto(id = 11, name = "Paul McCartney", active = true),
            ),
            releasesUrl = "",
        )

        val result = dto.toDomain()

        assertEquals(2, result.members.size)
        assertEquals("John Lennon", result.members[0].name)
        assertTrue(result.members[0].isActive)
    }

    @Test
    fun `ArtistDetailDto members is empty list when members is null`() {
        val dto = ArtistDetailDto(
            id          = 1,
            name        = "Solo Artist",
            profile     = null,
            images      = null,
            members     = null,
            releasesUrl = "",
        )

        val result = dto.toDomain()

        assertTrue(result.members.isEmpty())
    }

}