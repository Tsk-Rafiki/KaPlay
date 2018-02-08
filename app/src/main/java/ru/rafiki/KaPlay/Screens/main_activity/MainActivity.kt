package ru.rafiki.KaPlay.Screens.main_activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import ru.rafiki.KaPlay.R
import ru.rafiki.KaPlay.Screens.main_activity.fragment_adapter.SectionsPagerAdapter
import ru.rafiki.KaPlay.services.kaudio_media_service.AudioRepository
import ru.rafiki.KaPlay.services.kaudio_media_service.KAudioMusicService

class MainActivity : AppCompatActivity() {

    companion object {
        val Broadcast_PLAY_AUDIO: String = "ru.sdn.audiotestkotlin.PlayAudio"
        val Broadcast_STOP_AUDIO: String = "ru.sdn.audiotestkotlin.StopAudio"
        val Broadcast_NEXT_AUDIO: String = "ru.sdn.audiotestkotlin.NextAudio"
        val Broadcast_PREV_AUDIO: String = "ru.sdn.audiotestkotlin.PrevAudio"
        val Broadcast_SEEK_TO_AUDIO: String = "ru.sdn.audiotestkotlin.SeekToAudio"
        val Broadcast_DESTROY_SERVICE: String = "ru.sdn.audiotestkotlin.SeekToAudio"
        val Broadcast_IMAGE_PATH: String = "ru.sdn.audiotestkotline.ImagePath"
        val KEY_ARTIST: String = "artist"
        val KEY_ALBUM: String = "album"
    }

    lateinit var player: KAudioMusicService
    lateinit var audioRepository: AudioRepository

    var isServiceBound = false
    var isMusicFilesExist = true
    lateinit private var serviceConnection: ServiceConnection

    enum class FragmentType { player, playlist, settings}

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkReadExternalPermissions()
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mSectionsPagerAdapter?.setParametersToPlayerFragment(AudioRepository.getFilesCount())
        container.adapter = mSectionsPagerAdapter

        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder: KAudioMusicService.LocalBinder = service as KAudioMusicService.LocalBinder
                player = binder.getService()
                isServiceBound = true
                Toast.makeText(this@MainActivity, "Service bound", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkReadExternalPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText( this,"Storage permission is needed to load music files", Toast.LENGTH_SHORT).show()
                } else {
                    val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1100
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
                }
            } else {
                audioRepository = AudioRepository
                if (!audioRepository.loadAudio(applicationContext)) {
                    isMusicFilesExist = false
                    Toast.makeText(this, "Music not found!", Toast.LENGTH_SHORT).show()
                    Log.d("myLog", "Music not found!")
                }
                Log.d("myLog", "checkReadExternalPermissions() SDK.ver >= 23")
                Log.d("myLog", "Play list size: ${audioRepository.audioList.size}")
            }
        } else {
            audioRepository = AudioRepository
            audioRepository.loadAudio(applicationContext)
            Log.d("myLog", "checkReadExternalPermissions() SDK.ver < 23")
            Log.d("myLog", "Play list size: ${audioRepository.audioList.size}")
        }
    }

    fun isMusicFilesExists(): Boolean {
        return isMusicFilesExist
    }
}
