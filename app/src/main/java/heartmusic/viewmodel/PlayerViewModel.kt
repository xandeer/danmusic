package heartmusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem

class PlayerViewModel : ViewModel() {
  var isPlaying by mutableStateOf(false)
    private set

  var currentIndex by mutableStateOf(-1)
    private set

  fun play(index: Int) {
    isPlaying = true
    currentIndex = index
  }

  fun pause() {
    isPlaying = false
  }

  fun toggle() {
    isPlaying = !isPlaying
  }
}
