package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SongBroker
import ayds.apolo.songinfo.home.model.repository.local.spotify.cache.SongCache
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SongRepositoryTest {

    private val songCache: SongCache = mockk(relaxUnitFun = true)
    private val spotifyLocalStorage: SpotifyLocalStorage = mockk(relaxUnitFun = true)
    private val songBroker: SongBroker = mockk(relaxUnitFun = true)

    private val songRepository: SongRepository =
        SongRepositoryImpl(songCache, spotifyLocalStorage, songBroker)


    @Test
    fun `given an existing song in cache, it should return song and mark it as stored`() {
        val song = SpotifySong(
            "id", "song", "artist", "album",
            "date", "url", "image",
            false, false
        )

        every { songCache.getSongByTerm("id") } returns song

        val result = songRepository.getSongByTerm("id")

        assertEquals(song, result)
        assertTrue(song.isCacheStored)
    }

    @Test
    fun `given an existing song, local storage should return song, mark it as local and update the cache`() {
        val song = SpotifySong(
            "id", "song", "artist", "album",
            "date", "url", "image",
            false, false
        )

        every { songCache.getSongByTerm("id") } returns null
        every { spotifyLocalStorage.getSongByTerm("id") } returns song

        val result = songRepository.getSongByTerm("id")

        assertEquals(song, result)
        assertTrue(song.isLocallyStored)
        verify { songCache.insertSong("id", song) }
    }

    @Test
    fun `given an existing song, song broker should return song and update cache and storage`() {
        val song : SpotifySong = mockk()

        every { songCache.getSongByTerm("id") } returns null
        every { spotifyLocalStorage.getSongByTerm("id") } returns null
        every { songBroker.getSong("id") } returns song

        val result = songRepository.getSongByTerm("id")

        assertEquals(song, result)
        verify { songCache.insertSong("id", song) }
        verify { spotifyLocalStorage.insertSong("id", song) }
    }

    @Test
    fun `given an non existing song, song broker should return empty song`() {
        every { songCache.getSongByTerm("id") } returns null
        every { spotifyLocalStorage.getSongByTerm("id") } returns null
        every { songBroker.getSong("id") } returns null

        val result = songRepository.getSongByTerm("id")

        assertEquals(EmptySong, result)
    }
}