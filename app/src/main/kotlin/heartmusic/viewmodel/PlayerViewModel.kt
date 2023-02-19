package heartmusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import heartmusic.Millis
import heartmusic.data.PlaylistQuerySong
import heartmusic.data.asMediaItems
import heartmusic.logger
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs

class PlayerViewModel(private val player: ExoPlayer) : ViewModel() {
  internal val logger get() = logger("PlayerViewModel")
  
  var isPlaying by mutableStateOf(false)
    internal set

  private var currentIndex by mutableStateOf(-1)

  var playlistId by mutableStateOf(-1L)
    private set

  val playingSong
    get() = snapshotFlow { songs }.combine(snapshotFlow { currentIndex }) { songs, index ->
      songs.getOrNull(index)
    }.distinctUntilChanged()

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
    // If the current song is the last one and it's finished, seek to the beginning.
    if (!player.hasNextMediaItem() &&
      abs(player.currentPosition - player.duration) < 100
    ) {
      player.seekToDefaultPosition()
    } else {
      player.play()
    }
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
        if (!isPlaying) return@collect
        (player.currentPosition to player.duration).also { (position, duration) ->
          if (position >= 0 && duration > 0) {
//            logger.d("position: $position, duration: $duration")
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

  var hasNext by mutableStateOf(false)
    internal set

  fun next() {
    player.seekToNext()
  }

  internal fun pause() {
    isPlaying = false
    player.pause()
  }

  var position: Millis by mutableStateOf(0)
    private set
  var duration: Millis by mutableStateOf(0)
    private set

  private fun updatePosition(position: Millis, duration: Millis) {
    this.position = position
    this.duration = duration
  }

  private val _errorFlow = MutableSharedFlow<PlaybackException>()
  val errorFlow: Flow<PlaybackException> get() = _errorFlow

  private val listener = object : Listener {
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
      logger.d("onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.title}")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
      super.onIsPlayingChanged(isPlaying)
      this@PlayerViewModel.isPlaying = isPlaying
    }

    override fun onPositionDiscontinuity(
      oldPosition: Player.PositionInfo,
      newPosition: Player.PositionInfo,
      reason: Int
    ) {
//      logger.d("onPositionDiscontinuity: ${newPosition.mediaItemIndex}, ${newPosition.positionMs}/${newPosition.contentPositionMs}")
      updateByPosition(newPosition.mediaItemIndex)
    }

    override fun onPlayerError(error: PlaybackException) {
      super.onPlayerError(error)
      pause()
      viewModelScope.launch { _errorFlow.emit(error) }
      logger.e(error, "onPlayerError: ${error.message}")
    }

    override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
      super.onAvailableCommandsChanged(availableCommands)
      hasNext = availableCommands.contains(Player.COMMAND_SEEK_TO_NEXT)
    }
  }

  internal fun updateByPosition(index: Int) {
    currentIndex = index
  }

  private fun observeSeek() {
    viewModelScope.launch {
      playingSong.collect {
        songs.indexOf(it).also { idx ->
          if (idx > -1) {
            logger.d("seekTo: $it")
            player.seekTo(idx, 0L)
            play()
          }
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
