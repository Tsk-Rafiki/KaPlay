package ru.rafiki.KaPlay.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by denis.sakovich on 04.12.2017.
 */
data class SimilarTrackResult (
    @SerializedName("similartracks")
    @Expose
    private var similartracks: SimilarTracks? = null
)

data class SimilarAttr (
    @SerializedName("artist")
    @Expose
    var artist: String? = null
)

data class SimilarTracks (
    @SerializedName("track")
    @Expose
    var track: List<SimilarTrack>? = null,
    @SerializedName("@attr")
    @Expose
    var attr: SimilarAttr? = null
)

data class SimilarTrack (
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("playcount")
    @Expose
    var playcount: Int? = null,
    @SerializedName("mbid")
    @Expose
    var mbid: String? = null,
    @SerializedName("match")
    @Expose
    var match: Double? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null,
    @SerializedName("streamable")
    @Expose
    var streamable: Streamable? = null,
    @SerializedName("duration")
    @Expose
    var duration: Int? = null,
    @SerializedName("artist")
    @Expose
    var artist: Artist? = null,
    @SerializedName("image")
    @Expose
    var image: List<Image>? = null
)