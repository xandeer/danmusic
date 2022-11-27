package danmusic.app.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import danmusic.app.data.Playlist
import danmusic.app.data.source.remote.DanRemoteDataSource

class PlaylistPagingSource(
  private val remote: DanRemoteDataSource,
) : PagingSource<Long, Playlist>() {
  override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Playlist> {
    return try {
      val playlists = remote.getTopPlaylists(params.loadSize, params.key ?: 0).playlists

      LoadResult.Page(
        data = playlists,
        prevKey = params.key,
        nextKey = playlists.lastOrNull()?.updateTime,
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }

  override fun getRefreshKey(state: PagingState<Long, Playlist>): Long? {
    return state.anchorPosition?.let {
      state.closestPageToPosition(it)?.prevKey
    }
  }
}
