package ru.rafiki.KaPlay.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by denis.sakovich on 04.12.2017.
 */
class Album {
    @SerializedName("artist")
    @Expose
    private val artist: String? = null
    @SerializedName("title")
    @Expose
    private val title: String? = null
    @SerializedName("mbid")
    @Expose
    private val mbid: String? = null
    @SerializedName("url")
    @Expose
    private val url: String? = null
    @SerializedName("image")
    @Expose
    private val image: List<Image>? = null
    @SerializedName("@attr")
    @Expose
    private val attr: SimilarAttr? = null
}

class Artist {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("mbid")
    @Expose
    var mbid: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
}

class Attr {
    @SerializedName("position")
    @Expose
    var position: String? = null
}

class GetTrackInfoResult {
    @SerializedName("track")
    @Expose
    var track: Track? = null
}

class Image {
    @SerializedName("#text")
    @Expose
    var text: String? = null
    @SerializedName("size")
    @Expose
    var size: String? = null
}

class Streamable {
    @SerializedName("#text")
    @Expose
    var text: String? = null
    @SerializedName("fulltrack")
    @Expose
    var fulltrack: String? = null
}

class Tag {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
}

class Toptags {
    @SerializedName("tag")
    @Expose
    var tag: List<Tag>? = null
}

class Track {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("mbid")
    @Expose
    var mbid: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("duration")
    @Expose
    var duration: String? = null
    @SerializedName("streamable")
    @Expose
    var streamable: Streamable? = null
    @SerializedName("listeners")
    @Expose
    var listeners: String? = null
    @SerializedName("playcount")
    @Expose
    var playcount: String? = null
    @SerializedName("artist")
    @Expose
    var artist: Artist? = null
    @SerializedName("album")
    @Expose
    var album: Album? = null
    @SerializedName("toptags")
    @Expose
    var toptags: Toptags? = null
    @SerializedName("wiki")
    @Expose
    var wiki: Wiki? = null
}

class Wiki {
    @SerializedName("published")
    @Expose
    var published: String? = null
    @SerializedName("summary")
    @Expose
    var summary: String? = null
    @SerializedName("content")
    @Expose
    var content: String? = null
}