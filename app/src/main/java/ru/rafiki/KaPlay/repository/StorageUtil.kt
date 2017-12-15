package ru.rafiki.KaPlay.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.rafiki.KaPlay.network.model.Audio

/**
 * Created by denis.sakovich on 11.12.2017.
 */
class StorageUtil(val context: Context) {
    private val STORAGE: String = "ru.sdn.audiotestkotlin.STORAGE"
    lateinit private var preference: SharedPreferences

    public fun storeAudio(arrayList: ArrayList<Audio>) {
        preference = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preference.edit()
        val gson: Gson = Gson()
        val json: String = gson.toJson(arrayList)
        editor.putString("audioArrayList", json)
        editor.apply()
    }

    public fun loadAudio(): ArrayList<Audio> {
        preference = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = preference.getString("audioArrayList", null)
        return gson.fromJson(json, object : TypeToken<ArrayList<Audio>>(){}.type)
    }

    public fun storeAudioIndex(index: Int) {
        preference = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt("audioIndex", index)
        editor.apply()
    }

    public fun loadAudioIndex(): Int {
        preference = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preference.getInt("audioIndex", -1)
    }

    public fun clearCachedAudioPlayList() {
        preference = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preference.edit()
        editor.clear()
        editor.commit()
    }
}