package ru.rafiki.KaPlay.repository

import io.reactivex.Observable
import ru.rafiki.KaPlay.network.LastFmApi
import ru.rafiki.KaPlay.network.model.GetTrackInfoResult
import ru.rafiki.KaPlay.network.model.SimilarTrackResult

/**
 * Created by denis.sakovich on 04.12.2017.
 */
class AboutArtistRepository(val api: LastFmApi) {

    fun getTrackAbout(api_key: String, artist: String, track: String) : Observable<GetTrackInfoResult> {
        return api.getTrackInfo("track.getInfo", api_key, artist, track)
    }

    fun getSimilarTrack(api_key: String, artist: String, track: String, limit: String) : Observable<SimilarTrackResult> {
        return api.getSimilarTrack("track.getSimilar", api_key, artist, track, limit, "json")
    }

}