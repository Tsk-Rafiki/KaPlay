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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import ru.rafiki.KaPlay.R
import ru.rafiki.KaPlay.Screens.main_activity.fragment_adapter.SectionsPagerAdapter
import ru.rafiki.KaPlay.network.model.Audio
import ru.rafiki.KaPlay.repository.StorageUtil
import ru.rafiki.KaPlay.services.kaudio_media_service.KAudioMusicService

class MainActivity : AppCompatActivity() {

    companion object {
        val Broadcast_PLAY_NEW_AUDIO: String = "ru.sdn.audiotestkotlin.PlayNewAudio"
    }

    lateinit var player: KAudioMusicService
    lateinit var audioList: ArrayList<Audio>

    var isServiceBound = false
    lateinit private var serviceConnection: ServiceConnection

    enum class fragmentType { player, playlist, settings}

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

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
        loadAudio()
    }

    private fun playAudio(audioIndex: Int) {
        if (!isServiceBound) {
            val storage: StorageUtil = StorageUtil(applicationContext)
            storage.storeAudio(audioList)
            storage.storeAudioIndex(audioIndex)

            val playerIntent: Intent = Intent(this, KAudioMusicService::class.java)
            startService(playerIntent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            val storage: StorageUtil = StorageUtil(applicationContext)
            storage.storeAudioIndex(audioIndex)
            val broadcastIntent: Intent = Intent(Broadcast_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

    private fun loadAudio() {
        val contentResolver: ContentResolver = contentResolver

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection: String = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder: String = MediaStore.Audio.Media.TITLE + " ASC"
        checkReadExternalPermissions()
        val cursor: Cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        if (cursor.count > 0) {
            audioList = ArrayList()
            while (cursor.moveToNext()) {
                val data: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                audioList.add(Audio(data, title, album, artist))
            }
        }
        cursor.close()

    }

    private fun checkReadExternalPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }

            }
            val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1100
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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
