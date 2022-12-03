package heartmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import heartmusic.data.source.HeartRepository

class TopPlaylistViewModel constructor(
  repository: HeartRepository
) : ViewModel() {
  val playlists = repository.topPlaylistPager.flow.cachedIn(viewModelScope)
}
