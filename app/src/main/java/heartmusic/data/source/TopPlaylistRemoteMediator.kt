package heartmusic.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import heartmusic.data.Playlist
import heartmusic.data.source.db.HeartPlaylistDb
import heartmusic.data.source.remote.HeartRemoteDataSource
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class TopPlaylistRemoteMediator(
  private val db: HeartPlaylistDb,
  private val remote: HeartRemoteDataSource,
) : RemoteMediator<Int, Playlist>() {
  private val playlistDao = db.playlists()
  private val cacheTimeDao = db.cacheTime()

  init {
    Timber.tag("HeartRemoteMediator")
  }

  override suspend fun initialize(): InitializeAction {
    val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
    val lastUpdated = db.withTransaction {
      cacheTimeDao.lastTopPlaylistUpdateTime()
    }
    return if (System.currentTimeMillis() - lastUpdated > cacheTimeout) {
      InitializeAction.LAUNCH_INITIAL_REFRESH
    } else {
      Timber.i("Skip initial refresh.")
      InitializeAction.SKIP_INITIAL_REFRESH
    }
  }

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, Playlist>
  ): MediatorResult {
    return try {
      val loadAnchor = when (loadType) {
        LoadType.REFRESH -> 0
        LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> {
          state.lastItemOrNull()?.updateTime
            ?: return MediatorResult.Success(endOfPaginationReached = true)
        }
      }
      val playlists = remote.getTopPlaylists(
        size = when (loadType) {
          LoadType.REFRESH -> state.config.initialLoadSize
          else -> state.config.pageSize
        },
        anchor = loadAnchor
      ).playlists

      Timber.d("type: $loadType, size: ${playlists.size}, anchor: $loadAnchor")

      db.withTransaction {
        if (loadType == LoadType.REFRESH) {
          playlistDao.deleteAll()

          cacheTimeDao.updateTopPlaylistUpdateTime()
        }

        val nextIndex = playlistDao.getNextIndex() ?: 0
        playlists.forEachIndexed { index, playlist ->
          playlist.indexInResponse = nextIndex + index
        }

        playlistDao.insertAll(playlists)
      }
      MediatorResult.Success(endOfPaginationReached = playlists.isEmpty())
    } catch (e: Exception) {
      Timber.e(e, "load failed")
      MediatorResult.Error(e)
    }
  }
}
