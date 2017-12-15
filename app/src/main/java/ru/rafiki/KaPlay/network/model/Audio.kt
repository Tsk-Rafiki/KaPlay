package ru.rafiki.KaPlay.network.model

import java.io.Serializable

/**
 * Created by denis.sakovich on 11.12.2017.
 */
class Audio(
    var data: String,
    var title: String,
    var album: String,
    var artist: String,
    var length: String) : Serializable