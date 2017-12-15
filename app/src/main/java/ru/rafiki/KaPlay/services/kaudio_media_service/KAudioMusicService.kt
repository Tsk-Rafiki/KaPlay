package ru.rafiki.KaPlay.services.kaudio_media_service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.os.Binder
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.PhoneStateListener.LISTEN_NONE
import android.telephony.TelephonyManager
import android.util.Log
import ru.rafiki.KaPlay.R
import ru.rafiki.KaPlay.Screens.main_activity.MainActivity
import ru.rafiki.KaPlay.network.model.Audio
import ru.rafiki.KaPlay.network.model.PlaybackStatus
import ru.rafiki.KaPlay.repository.StorageUtil
import java.io.IOException

/**
 * Created by denis.sakovich on 05.12.2017.
 */
class KAudioMusicService : Service(),
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener{

    private var mediaPlayer: MediaPlayer
    lateinit private var audioManager: AudioManager
    private var becomingNoisyReceiver: BroadcastReceiver
    lateinit private var phoneStateListener: PhoneStateListener
    lateinit private var telephonyManager: TelephonyManager
    lateinit private var audioList: ArrayList<Audio>
    lateinit private var activeAudio: Audio
    private var playNewAudio: BroadcastReceiver
    lateinit var mediaSession: MediaSessionCompat
    lateinit var transportControls: MediaControllerCompat.TransportControls
    var mediaSessionManager: MediaSessionManager? = null

    private var audioIndex: Int = -1
    private var ongoingCall: Boolean = false
    private var resumePosition: Int = 0

    companion object {
        var ACTION_PLAY: String = "ru.sdn.audiotestkotlin.ACTION_PLAY"
        var ACTION_PAUSE: String = "ru.sdn.audiotestkotlin.ACTION_PAUSE"
        var ACTION_PREVIOUS: String = "ru.sdn.audiotestkotlin.ACTION_PREVIOUS"
        var ACTION_NEXT: String = "ru.sdn.audiotestkotlin.ACTION_NEXT"
        var ACTION_STOP: String = "ru.sdn.audiotestkotlin.ACTION_STOP"
        var NOTIFICATION_ID: Int = 101
    }

    init {
        mediaPlayer = MediaPlayer()
        becomingNoisyReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                pauseMedia()
                buildNotification(PlaybackStatus.PAUSED)
            }
        }

        playNewAudio = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                audioIndex = StorageUtil(applicationContext).loadAudioIndex()
                if (audioIndex != -1 && audioIndex < audioList.size) {
                    activeAudio = audioList.get(audioIndex)
                } else {
                    stopSelf()
                }

                stopMedia()
                mediaPlayer.reset()
                initMediaPlayer()
                updateMetaData()
                buildNotification(PlaybackStatus.PLAYING)

            }
        }

    }

    private val iBinder: IBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        callStateListener()
        registerBecomingNoisyReceiver()
        register_playNewAudio()
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        with(mediaPlayer) {
            setOnCompletionListener(this@KAudioMusicService)
            setOnErrorListener(this@KAudioMusicService)
            setOnPreparedListener(this@KAudioMusicService)
            setOnBufferingUpdateListener(this@KAudioMusicService)
            setOnSeekCompleteListener(this@KAudioMusicService)
            setOnInfoListener(this@KAudioMusicService)
            reset()
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        try {
            mediaPlayer.setDataSource(activeAudio.data)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }
        mediaPlayer.prepareAsync()

    }

    private fun initMediaSession() {
        if (mediaSessionManager != null) return
        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession = MediaSessionCompat(applicationContext, "AudioPlayer")
        transportControls = mediaSession.controller.transportControls
        mediaSession.isActive = true
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        updateMetaData()
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                resumeMedia()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onPause() {
                super.onPause()
                pauseMedia()
                buildNotification(PlaybackStatus.PAUSED)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                skipToNext()
                updateMetaData()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                skipToPrevious()
                updateMetaData()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onStop() {
                super.onStop()
                removeNotification()
                stopSelf()
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
            }
        })
    }

    private fun updateMetaData() {
        val albumArt: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.abc_ab_share_pack_mtrl_alpha)
        mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.album)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.title)
                .build())
    }

    
    private fun skipToNext() {
        if (audioIndex == audioList.size - 1) {
            audioIndex = 0
            activeAudio = audioList[audioIndex]
        } else {
            activeAudio = audioList[++audioIndex]
        }

        StorageUtil(applicationContext).storeAudioIndex(audioIndex)
        stopMedia()
        mediaPlayer.reset()
        initMediaPlayer()
    }

    private fun skipToPrevious() {
        if (audioIndex == 0) {
            audioIndex = audioList.size - 1
            activeAudio = audioList[audioIndex]
        } else {
            activeAudio = audioList[--audioIndex]
        }
        StorageUtil(applicationContext).storeAudioIndex(audioIndex)
        stopMedia()
        mediaPlayer.reset()
        initMediaPlayer()
    }

    private fun buildNotification(playbackStatus: PlaybackStatus) {
        var notificationAction: Int = android.R.drawable.ic_media_pause
        var playPauseAction: PendingIntent? = null
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause
            playPauseAction = playbackAction(1)
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play
            playPauseAction = playbackAction(0)
        }
        val largeIcon: Bitmap = BitmapFactory.decodeResource(resources, android.R.drawable.ic_dialog_info)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setShowWhen(false)
                .setStyle(MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                        .setShowActionsInCompactView(0, 1, 2))
                .setColor(resources.getColor(R.color.colorPrimary))
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentText(activeAudio.artist)
                .setContentTitle(activeAudio.album)
                .setContentInfo(activeAudio.title)
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", playPauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2))
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun removeNotification() {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun playbackAction(actionNumber: Int): PendingIntent? {
        val playbackAction = Intent(this, KAudioMusicService.javaClass)
        return when(actionNumber) {
            0 -> {
                playbackAction.action = ACTION_PLAY
                PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            1 -> {
                playbackAction.action = ACTION_PAUSE
                PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            2 -> {
                playbackAction.action = ACTION_NEXT
                PendingIntent.getService(this , actionNumber, playbackAction, 0)
            }
            3 -> {
                playbackAction.action = ACTION_PREVIOUS
                PendingIntent.getService(this , actionNumber, playbackAction, 0)
            }
            else -> null
        }
    }

    private fun handleIncomingActions(playbackIntent: Intent?) {
        if (playbackIntent == null || playbackIntent.action == null) return
        val actionString: String = playbackIntent.action
        when (actionString) {
            ACTION_PLAY -> transportControls.play()
            ACTION_PAUSE -> transportControls.pause()
            ACTION_NEXT -> transportControls.skipToNext()
            ACTION_PREVIOUS -> transportControls.skipToPrevious()
            ACTION_STOP -> transportControls.stop()
        }
    }

    override fun onBind(intent: Intent?) = iBinder

    override fun onCompletion(mp: MediaPlayer?) {
        stopMedia()
        stopSelf()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        playMedia()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when(what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ->
                Log.d(this.javaClass.simpleName, "Media error not valid for progressive playback $extra")
            MediaPlayer.MEDIA_ERROR_SERVER_DIED ->
                    Log.d(this.javaClass.simpleName, "Media error server died $extra")
            MediaPlayer.MEDIA_ERROR_UNKNOWN ->
                    Log.d(this.javaClass.simpleName, "Media error unknown $extra")
        }
        return false
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when(focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!mediaPlayer.isPlaying) mediaPlayer.start()
                mediaPlayer.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if(mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if(mediaPlayer.isPlaying) mediaPlayer.pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if(mediaPlayer.isPlaying) mediaPlayer.setVolume(0.1f, 0.1f)
            }

        }
    }

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        try {
            val storage = StorageUtil(applicationContext)
            audioList = storage.loadAudio()
            audioIndex = storage.loadAudioIndex()

            if (audioIndex != -1 && audioIndex < audioList.size) {
                activeAudio = audioList[audioIndex]
            } else {
                stopSelf()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            stopSelf()
        }

        if (requestAudioFocus() == false) {
            stopSelf()
        }

        if (mediaSessionManager == null) {
            try {
                initMediaPlayer()
                initMediaSession()
            } catch (e: RemoteException) {
                e.printStackTrace()
                stopSelf()
            }
            buildNotification(PlaybackStatus.PLAYING)
        }
        handleIncomingActions(intent)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun register_playNewAudio() {
        val filter = IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO)
        registerReceiver(playNewAudio, filter)
    }

    private fun callStateListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String?) {
                when(state) {
                    TelephonyManager.CALL_STATE_OFFHOOK,
                        TelephonyManager.CALL_STATE_RINGING -> {
                        pauseMedia()
                        ongoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if(ongoingCall) {
                            ongoingCall = false
                            resumeMedia()
                        }
                    }
                }
            }
        }
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMedia()
        mediaPlayer.release()
        removeAudioFocus()
        telephonyManager.listen(phoneStateListener, LISTEN_NONE)
        removeNotification()
        unregisterReceiver(becomingNoisyReceiver)
        unregisterReceiver(playNewAudio)
        StorageUtil(applicationContext).clearCachedAudioPlayList()
    }

    private fun removeAudioFocus() : Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this)
    }

    private fun requestAudioFocus() : Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result: Int = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun playMedia() {
        if(!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    private fun stopMedia() {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
    }

    private fun pauseMedia() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            resumePosition = mediaPlayer.currentPosition
        }
    }

    private fun resumeMedia() {
        if(!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(resumePosition)
            mediaPlayer.start()
        }
    }

    class LocalBinder : Binder() {
        fun getService() : KAudioMusicService = KAudioMusicService()

    }
}