package heartmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import heartmusic.data.Playlist
import heartmusic.data.source.HeartRepository
import kotlinx.coroutines.flow.Flow

private const val ITEMS_PER_PAGE = 7

class TopPlaylistViewModel constructor(
  private val repository: HeartRepository
) : ViewModel() {

  val playlists: Flow<PagingData<Playlist>> = Pager(
    config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
    pagingSourceFactory = { repository.playListPagingSource() }
  )
    .flow
    .cachedIn(viewModelScope)
}
