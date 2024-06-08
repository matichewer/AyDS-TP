package ayds.apolo.songinfo.home.model.repository.local.spotify.cache

import ayds.apolo.songinfo.home.model.entities.SpotifySong

interface SongCache {
    fun insertSong(query: String, song: SpotifySong)
    fun getSongByTerm(term: String): SpotifySong?
}


