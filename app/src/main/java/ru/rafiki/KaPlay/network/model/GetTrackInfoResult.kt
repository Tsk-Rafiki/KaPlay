package ru.rafiki.KaPlay.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by denis.sakovich on 04.12.2017.
 */
data class Album(
    @SerializedName("artist")
    @Expose
    val artist: String? = null,
    @SerializedName("title")
    @Expose
    val title: String? = null,
    @SerializedName("mbid")
    @Expose
    val mbid: String? = null,
    @SerializedName("url")
    @Expose
    val url: String? = null,
    @SerializedName("image")
    @Expose
    val image: List<Image>? = null,
    @SerializedName("@attr")
    @Expose
    val attr: SimilarAttr? = null
)

data class Artist (
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("mbid")
    @Expose
    var mbid: String? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null
    )

data class Attr (
    @SerializedName("position")
    @Expose
    var position: String? = null
)

data class GetTrackInfoResult (
    @SerializedName("track")
    @Expose
    var track: Track? = null
)

data class Image (
    @SerializedName("#text")
    @Expose
    var text: String? = null,
    @SerializedName("size")
    @Expose
    var size: String? = null
)

class Streamable {
    @SerializedName("#text")
    @Expose
    var text: String? = null
    @SerializedName("fulltrack")
    @Expose
    var fulltrack: String? = null
}

data class Tag (
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null
)

data class Toptags (
    @SerializedName("tag")
    @Expose
    var tag: List<Tag>? = null
)

data class Track (
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("mbid")
    @Expose
    var mbid: String? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null,
    @SerializedName("duration")
    @Expose
    var duration: String? = null,
    @SerializedName("streamable")
    @Expose
    var streamable: Streamable? = null,
    @SerializedName("listeners")
    @Expose
    var listeners: String? = null,
    @SerializedName("playcount")
    @Expose
    var playcount: String? = null,
    @SerializedName("artist")
    @Expose
    var artist: Artist? = null,
    @SerializedName("album")
    @Expose
    var album: Album? = null,
    @SerializedName("toptags")
    @Expose
    var toptags: Toptags? = null,
    @SerializedName("wiki")
    @Expose
    var wiki: Wiki? = null
)

data class Wiki (
    @SerializedName("published")
    @Expose
    var published: String? = null,
    @SerializedName("summary")
    @Expose
    var summary: String? = null,
    @SerializedName("content")
    @Expose
    var content: String? = null
)