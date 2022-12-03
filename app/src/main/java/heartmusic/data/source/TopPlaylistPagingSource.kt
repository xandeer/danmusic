package heartmusic.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import heartmusic.data.Playlist
import heartmusic.data.source.remote.HeartRemoteDataSource

class TopPlaylistPagingSource(
  private val remote: HeartRemoteDataSource,
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
