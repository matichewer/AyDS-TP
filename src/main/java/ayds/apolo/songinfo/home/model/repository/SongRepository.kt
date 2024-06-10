package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.Song
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SongBroker
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage
import ayds.apolo.songinfo.home.model.repository.local.spotify.cache.SongCache


interface SongRepository {
    fun getSongByTerm(term: String): Song
}

internal class SongRepositoryImpl(
    private val songCache: SongCache,
    private val songLocalStorage: SpotifyLocalStorage,
    private val songBroker: SongBroker,
) : SongRepository {

    override fun getSongByTerm(term: String): Song {
        var song = songCache.getSongByTerm(term)

        if (song != null) {
            song.setSongIsCacheStored()
        } else {
            song = songLocalStorage.getSongByTerm(term)
            if (song != null) {
                song.setSongIsLocallyStored()
                songCache.insertSong(term, song)
            } else {
                song = songBroker.getSong(term)
                if (song != null) {
                    songCache.insertSong(term, song)
                    songLocalStorage.insertSong(term, song)
                }
            }
        }

        return song ?: EmptySong
    }

    private fun SpotifySong.setSongIsLocallyStored() {
        this.isLocallyStored = true
    }

    private fun SpotifySong.setSongIsCacheStored() {
        this.isCacheStored = true
    }
}