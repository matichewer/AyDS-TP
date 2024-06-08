package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.Song
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyModule
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.ResultSetToSpotifySongMapperImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlQueriesImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.cache.SongCache
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException


interface SongRepository {
    fun getSongByTerm(term: String): Song
}


internal class SongRepositoryImp (
    private val songCache: SongCache,
    private val songLocalStorage: SpotifyLocalStorage
){



    internal val spotifyDB = SpotifySqlDBImpl(
        SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl()
    )
    val spotifyTrackService = SpotifyModule.spotifyTrackService


    ///// Wiki
    var retrofit: Retrofit? = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/w/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    var wikipediaAPI = retrofit!!.create(WikipediaAPI::class.java)
    //// end wiki

    fun getSongByTerm(term: String): SearchResult {

            var spotifySong: SpotifySong? = songCache.getSongByTerm(term)
        if (spotifySong != null) {
            spotifySong.isCacheStored = true
            return spotifySong
        } else {
            spotifySong = spotifyDB.getSongByTerm(term)
            if (spotifySong != null) {
                spotifySong.isLocallyStored = true
                // update the cache
                cache[term] = spotifySong
                return spotifySong
            } else {
                spotifySong = spotifyTrackService.getSong(term)
                if (spotifySong != null) {
                    spotifyDB.insertSong(term, spotifySong)
                    return spotifySong
                }
            }

        /////// Last chance, get anything from the wiki
        val callResponse: Response<String>
        try {
            callResponse = wikipediaAPI.getInfo(term).execute()
            System.out.println("JSON " + callResponse.body())
            val gson = Gson()
            val jobj: JsonObject = gson.fromJson(callResponse.body(), JsonObject::class.java)
            val query = jobj["query"].asJsonObject
            val snippetObj = query["search"].asJsonArray.firstOrNull()
            if (snippetObj != null) {
                val snippet = snippetObj.asJsonObject["snippet"]
                return SpotifySong("", snippet.asString, " - ", " - ", " - ", "", "")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return EmptySong
    }

    private fun getSongFromCache(term: String): SpotifySong? {
        val spotifySong: SpotifySong? = cache[term]
        return spotifySong
    }
}