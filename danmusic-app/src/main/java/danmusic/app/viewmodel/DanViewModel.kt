package danmusic.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import danmusic.app.data.Playlist
import danmusic.app.data.source.DanRepository
import kotlinx.coroutines.flow.Flow

private const val ITEMS_PER_PAGE = 7

class DanViewModel constructor(
  private val danRepository: DanRepository
) : ViewModel() {

  val playlists: Flow<PagingData<Playlist>> = Pager(
    config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
    pagingSourceFactory = { danRepository.playListPagingSource() }
  )
    .flow
    .cachedIn(viewModelScope)
}
