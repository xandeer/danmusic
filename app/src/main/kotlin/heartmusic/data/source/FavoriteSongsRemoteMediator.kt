package heartmusic.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import heartmusic.data.FavoriteSong
import heartmusic.data.source.db.HeartPlaylistDb
import heartmusic.data.source.remote.HeartRemoteDataSource
import heartmusic.logger

@OptIn(ExperimentalPagingApi::class)
class FavoriteSongsRemoteMediator(
  private val db: HeartPlaylistDb,
  private val remote: HeartRemoteDataSource,
) : RemoteMediator<Int, FavoriteSong>() {
  private val logger get() = logger("FavoriteSongsRemoteMediator")

  private val favoriteSongsDao = db.favoriteSongs()

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, FavoriteSong>
  ): MediatorResult {
    return try {
      val offset = when (loadType) {
        LoadType.REFRESH -> 0
        LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> {
          state.pages.size
//            ?: return MediatorResult.Success(endOfPaginationReached = true)
        }
      }
      var songs = favoriteSongsDao.get(
        offset, size = when (loadType) {
          LoadType.REFRESH -> state.config.initialLoadSize
          else -> state.config.pageSize
        }
      )

      @Suppress("SENSELESS_COMPARISON")
      val songUrls = songs.map { it.id }.joinToString(",").let {
        remote.getSongUrls(it).data
      }.filter { it.url != null }

      val validIds = songUrls.map { it.id }
      songs = songs.filter { it.id in validIds }

      logger.d("type: $loadType, size: ${songs.size}, offset: $offset")

      db.withTransaction {
        if (loadType == LoadType.REFRESH) {
          favoriteSongsDao.insertAll(
            favoriteSongsDao.getAll().map {
              it.copy(songUrl = null)
            }
          )
        }

        favoriteSongsDao.insertAll(songs)
      }
      MediatorResult.Success(endOfPaginationReached = songs.isEmpty())
    } catch (e: Exception) {
      logger.e(e, "load failed")
      MediatorResult.Error(e)
    }
  }
}
