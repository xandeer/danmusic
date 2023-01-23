package heartmusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import heartmusic.data.PlaylistQuerySong
import heartmusic.data.asMediaItems
import heartmusic.logger
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

private val logger get() = logger("PlayerViewModel")

class PlayerViewModel : ViewModel() {
  private val player: ExoPlayer = get(ExoPlayer::class.java)

  var isPlaying by mutableStateOf(false)
    private set

  var currentIndex by mutableStateOf(-1)
    private set

  var playlistId by mutableStateOf(-1L)
    private set

  var songs by mutableStateOf(emptyList<PlaylistQuerySong>())
    private set

  fun play(playlistId: Long, songs: List<PlaylistQuerySong>, song: PlaylistQuerySong) {
    val index = songs.indexOf(song)
    if (playlistId == this.playlistId && index == currentIndex) {
      if (!isPlaying) play()
      return
    }

    resetPlaylist(playlistId, songs)
    currentIndex = index
    startPositionTicker()
  }

  private fun play() {
    isPlaying = true
    player.play()
  }

  private fun resetPlaylist(id: Long, list: List<PlaylistQuerySong>) {
    playlistId = id
    player.clearMediaItems()
    songs = list
    player.addMediaItems(songs.asMediaItems())
    player.prepare()
    play()
  }

  private var isPositionTickerStarted = false

  @OptIn(ObsoleteCoroutinesApi::class)
  private fun startPositionTicker() {
    if (isPositionTickerStarted) return
    isPositionTickerStarted = true
    viewModelScope.launch {
      ticker(30).consumeAsFlow().collect {
        (player.currentPosition to player.duration).also { (position, duration) ->
          if (position >= 0 && duration > 0) {
            updatePosition(position, duration)
          }
        }
      }
    }
  }

  fun appendSongs(list: List<PlaylistQuerySong>) {
    songs = songs + list
  }

  fun toggle() {
    if (isPlaying) {
      pause()
    } else {
      play()
    }
  }

  private fun pause() {
    isPlaying = false
    player.pause()
  }

  var positionMs by mutableStateOf(0L)
    private set
  var durationMs by mutableStateOf(0L)
    private set

  private fun updatePosition(position: Long, duration: Long) {
    positionMs = position
    durationMs = duration
  }

  private val listener = object : Listener {
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
      logger.d("onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.title}")
    }

    private var isLoading = false
    override fun onIsLoadingChanged(isLoading: Boolean) {
      super.onIsLoadingChanged(isLoading)
      this.isLoading = isLoading
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
      super.onIsPlayingChanged(isPlaying)
      this@PlayerViewModel.isPlaying = isLoading || isPlaying
    }

    override fun onPositionDiscontinuity(
      oldPosition: Player.PositionInfo,
      newPosition: Player.PositionInfo,
      reason: Int
    ) {
//      logger.d("onPositionDiscontinuity: ${newPosition.mediaItemIndex}, ${newPosition.positionMs}/${newPosition.contentPositionMs}")
      updateByPosition(newPosition.mediaItemIndex)
    }
  }

  private fun updateByPosition(index: Int) {
    currentIndex = index
  }

  private fun observeSeek() {
    val nextIndex = snapshotFlow { currentIndex }
      .combine(snapshotFlow { playlistId }) { index, _ ->
        index
      }
    viewModelScope.launch {
      nextIndex.collect {
        if (it in songs.indices) {
          logger.d("seekTo: $it")
          player.seekTo(it, 0L)
          play()
        }
      }
    }
  }

  init {
    player.addListener(listener)
    observeSeek()
  }

  override fun onCleared() {
    super.onCleared()
    player.removeListener(listener)
  }
}
