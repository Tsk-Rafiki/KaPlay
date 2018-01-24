package ru.rafiki.KaPlay.Screens.main_activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
    }

    lateinit var player: KAudioMusicService
    lateinit var audioRepository: AudioRepository

    var isServiceBound = false
    var isPlayerDisabled = false
    lateinit private var serviceConnection: ServiceConnection

    enum class fragmentType { player, playlist, settings}

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
        checkReadExternalPermissions()
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
//        audioRepository = AudioRepository
//        audioRepository.loadAudio(applicationContext)
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
                    isPlayerDisabled = true
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
