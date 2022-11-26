package danmusic.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danmusic.app.data.Playlist
import danmusic.app.data.source.DanRepository
import kotlinx.coroutines.launch

class DanViewModel constructor(
  private val danRepository: DanRepository
) : ViewModel() {
  var playlists by mutableStateOf(emptyList<Playlist>())
    private set

  fun pullPlaylists() {
    viewModelScope.launch {
      danRepository.getTopPlaylists().let {
        playlists = playlists + it
      }
    }
  }

  fun clearPlaylists() {
    playlists = emptyList()
  }

  init {
    pullPlaylists()
  }
}
