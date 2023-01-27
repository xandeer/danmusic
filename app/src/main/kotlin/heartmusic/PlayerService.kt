package heartmusic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.snapshotFlow
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import heartmusic.data.PlaylistQuerySong
import heartmusic.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class PlayerService : Service() {
  companion object {
    private const val CHANNEL_ID = "hm"
    private const val ACTION_PAUSE = "pause"
    private const val ACTION_PLAY = "play"
    private const val ACTION_NEXT = "next"
  }

  private val notificationManager by lazy {
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  }
  private val broadcastReceiver by lazy { PlayerBroadcastReceiver() }

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    registerReceiver(broadcastReceiver, IntentFilter())
    observePlayerStates()
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(broadcastReceiver)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun observePlayerStates() {
    val vm: PlayerViewModel = get(PlayerViewModel::class.java)
    vm.viewModelScope.launch {
      combine(
        vm.playingSong,
        snapshotFlow { vm.isPlaying },
        snapshotFlow { vm.hasNext }) { song, isPlaying, hasNext ->
        song?.let {
          createNotification(it, isPlaying = isPlaying, hasNext = hasNext)
        }
      }.collect {
        it?.also { notification ->
          startForeground(1, notification)
        }
      }
    }
  }

  private fun createNotification(
    song: PlaylistQuerySong,
    isPlaying: Boolean,
    hasNext: Boolean
  ) =
    NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_menu_slideshow)
      .setContentTitle(song.name)
      .setContentIntent(contentIntent)
      .apply {
        if (isPlaying) {
          addAction(android.R.drawable.ic_media_pause, "Pause", pauseIntent)
        } else {
          addAction(android.R.drawable.ic_media_play, "Play", playIntent)
        }
        if (hasNext) {
          addAction(android.R.drawable.ic_media_next, "Next", nextIntent)
        }
      }
      .build()

  private val contentIntent by lazy {
    Intent(this, MainActivity::class.java).let {
      it.action = Intent.ACTION_MAIN
      it.addCategory(Intent.CATEGORY_LAUNCHER)
      it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

      PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
    }
  }

  private val pauseIntent by lazy { createActionIntent(ACTION_PAUSE) }
  private val playIntent by lazy { createActionIntent(ACTION_PLAY) }
  private val nextIntent by lazy { createActionIntent(ACTION_NEXT) }

  private fun createActionIntent(action: String) =
    Intent(this, PlayerBroadcastReceiver::class.java).let {
      it.action = action
      PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
    }

  private fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel("hm", "hm", NotificationManager.IMPORTANCE_HIGH).apply {
        description = "HeartMusic Player"
      }
      // Register the channel with the system
      notificationManager.createNotificationChannel(channel)
    }
  }

  class PlayerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      val vm: PlayerViewModel = get(PlayerViewModel::class.java)
      when (intent?.action) {
        ACTION_PLAY,
        ACTION_PAUSE -> vm.toggle()

        ACTION_NEXT -> vm.next()
      }
    }
  }
}
