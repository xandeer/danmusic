package heartmusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import heartmusic.data.Playlist
import heartmusic.data.PlaylistQuerySong
import heartmusic.data.SongUrl
import heartmusic.data.source.HeartRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class TopPlaylistViewModel constructor(
  private val repository: HeartRepository
) : ViewModel() {
  init {
    Timber.tag("HeartViewModel")
  }

  val playlists = repository.topPlaylistPager.flow.cachedIn(viewModelScope)

  private val playlistSongs = mutableMapOf<Long, Flow<PagingData<PlaylistQuerySong>>>()

  fun getSongs(playlistId: Long) = playlistSongs.getOrPut(playlistId) {
    repository.getPlaylistSongs(playlistId).flow.cachedIn(viewModelScope)
      .also {
        Timber.i("getSongs: $playlistId")
      }
  }

  var currentPlaylist by mutableStateOf<Playlist?>(null)

  /**
   * The list of songs in the current playlist screen, may be not the same
   * as the list of songs in the current playing playlist.
   */
  var songs by mutableStateOf<List<PlaylistQuerySong>>(emptyList())
}
