package heartmusic.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import heartmusic.data.PlaylistQuerySong

class PlayerViewModel : ViewModel() {
  var isPlaying by mutableStateOf(false)
    private set

  var currentIndex by mutableStateOf(-1)
    private set

  var playlistId by mutableStateOf(-1L)
    private set

  var songs = emptyList<PlaylistQuerySong>()
    private set

  val playingSong by derivedStateOf { songs.getOrNull(currentIndex) }

  fun play(playlist: Long, songs: List<PlaylistQuerySong>, song: PlaylistQuerySong) {
    this.songs = songs
    playlistId = playlist
    isPlaying = true
    currentIndex = songs.indexOf(song)
  }

  fun appendSongs(songs: List<PlaylistQuerySong>) {
    this.songs = this.songs + songs
  }

  fun next() {
    require(currentIndex < songs.size - 1) { "No next song" }
    currentIndex = currentIndex.inc()
  }

  fun updateByPosition(index: Int) {
    currentIndex = index
  }

  fun pause() {
    isPlaying = false
  }

  fun toggle() {
    isPlaying = !isPlaying
  }

  var positionMs by mutableStateOf(0L)
    private set
  var durationMs by mutableStateOf(0L)
    private set

  fun updatePosition(position: Long, duration: Long) {
    positionMs = position
    durationMs = duration
  }
}
