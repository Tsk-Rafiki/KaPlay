package ru.rafiki.KaPlay.services.kaudio_media_service

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import ru.rafiki.KaPlay.network.model.Audio
import ru.rafiki.KaPlay.repository.StorageUtil

/**
 * Created by denis.sakovich on 14.12.2017.
 */

object AudioRepository{

    var audioList: ArrayList<Audio>
    init {
        audioList = ArrayList()
    }
    fun loadAudio(context: Context) : Boolean {
        val contentResolver: ContentResolver = context.contentResolver

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection: String = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder: String = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor: Cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val data: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val length: String = (cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) / 1000).toString()
                audioList.add(Audio(data, title, album, artist, length))
            }
        } else
            return false
        cursor.close()
        return true
    }

    fun storeDataAndIndex(context: Context, audioIndex: Int) {
        val storage = StorageUtil(context)
        storage.storeAudio(audioList)
        storage.storeAudioIndex(audioIndex)

    }

}