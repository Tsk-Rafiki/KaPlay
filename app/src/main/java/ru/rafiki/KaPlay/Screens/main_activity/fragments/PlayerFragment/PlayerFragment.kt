package ru.rafiki.KaPlay.Screens.main_activity.fragments.PlayerFragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_player.view.*
import ru.rafiki.KaPlay.network.LastFmApi
import ru.rafiki.KaPlay.R
import ru.rafiki.KaPlay.Screens.main_activity.MainActivity
import ru.rafiki.KaPlay.repository.StorageUtil
import ru.rafiki.KaPlay.services.kaudio_media_service.AudioRepository
import ru.rafiki.KaPlay.services.kaudio_media_service.KAudioMusicService

/**
 * Created by denis.sakovich on 04.12.2017.
 */
class PlayerFragment : Fragment(), View.OnClickListener{

    var api_key: String = "2aa7df3efc89883966893b7e54131845"
    lateinit var player: KAudioMusicService
    var isPlaying: Boolean = false

    var isServiceBound = false
    lateinit private var serviceConnection: ServiceConnection

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("PlayerFragment", "OnCreateView")
        val rootView = inflater.inflate(R.layout.fragment_player, container, false)
        val lastFmApi = LastFmApi.create()
        lastFmApi.getTrackInfo("track.getInfo", api_key, "cher", "believe")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    Log.d("myLog", result.toString())

                }
                        , { error ->
                    error.printStackTrace()
                })

        rootView.button_next.setOnClickListener(this)
        rootView.button_stop.setOnClickListener(this)
        rootView.button_play.setOnClickListener(this)
        rootView.button_prev.setOnClickListener(this)

        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder: KAudioMusicService.LocalBinder = service as KAudioMusicService.LocalBinder
                player = binder.getService()
                isServiceBound = true
                Toast.makeText(this@PlayerFragment.context, "Service bound", Toast.LENGTH_SHORT).show()
            }
        }
        return rootView
    }

    override fun onClick(v: View?) {
        if (v != null)
        when (v.id) {
            R.id.button_next -> ButtonNextClicked()
            R.id.button_stop -> ButtonPauseClicked()
            R.id.button_play -> ButtonPlayClicked()
            R.id.button_prev -> ButtonPrevClicked()
        }

    }

    private fun ButtonNextClicked() {
        val broadcastIntent = Intent(MainActivity.Broadcast_NEXT_AUDIO)
        activity.sendBroadcast(broadcastIntent)
    }

    private fun ButtonPauseClicked() {
        isPlaying = if (isPlaying) {
            playAudio(0)
            true
        } else {
            stopAudio()
            false
        }
    }

    private fun ButtonPlayClicked() {
        isPlaying = if (!isPlaying) {
            playAudio(0)
            true
        } else {
            stopAudio()
            false
        }
    }

    private fun ButtonPrevClicked() {
        val broadcastIntent = Intent(MainActivity.Broadcast_PREV_AUDIO)
        activity.sendBroadcast(broadcastIntent)
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }

    private fun playAudio(audioIndex: Int) {
        if (!isServiceBound) {
            AudioRepository.storeDataAndIndex(context, audioIndex)
            val playerIntent = Intent(activity.applicationContext, KAudioMusicService::class.java)
            activity.startService(playerIntent)
            activity.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            val storage = StorageUtil(context)
            storage.storeAudioIndex(audioIndex)
            val broadcastIntent = Intent(MainActivity.Broadcast_PLAY_AUDIO)
            activity.sendBroadcast(broadcastIntent)
        }
    }

    private fun stopAudio() {
        val broadcastIntent = Intent(MainActivity.Broadcast_STOP_AUDIO)
        activity.sendBroadcast(broadcastIntent)
    }

}