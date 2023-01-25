package heartmusic.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import heartmusic.data.Playlist
import heartmusic.data.source.db.HeartPlaylistDb
import heartmusic.data.source.remote.HeartRemoteDataSource
import heartmusic.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val ITEMS_PER_PAGE = 7

private val logger get() = logger("HeartRepository")

@OptIn(ExperimentalPagingApi::class)
class HeartRepository(
  private val remote: HeartRemoteDataSource,
  private val db: HeartPlaylistDb,
) {
  val topPlaylistPager = Pager(
    config = PagingConfig(pageSize = ITEMS_PER_PAGE, initialLoadSize = 1),
    remoteMediator = TopPlaylistRemoteMediator(db = db, remote = remote)
  ) {
    db.playlists().playlists()
  }

  fun getPlaylistSongs(id: Long) = Pager(
    config = PagingConfig(pageSize = ITEMS_PER_PAGE, initialLoadSize = 1),
    remoteMediator = PlaylistSongsRemoteMediator(id, db = db, remote = remote)
  ) {
    db.playlistSongs().playlistWithSongs(id)
  }

  /**
   * Cached paging parameter, used to get the next page of data.
   */
  private var anchor = 0L

  /**
   * @param size The number of song lists, default is 1.
   * @param anchor Paging parameter, used to get the next page of data.
   * When it is 0, get the latest.
   * It's value is the last [Playlist.updateTime].
   */
  suspend fun getTopPlaylists(size: Int = 1, anchor: Long = this.anchor) =
    withContext(Dispatchers.IO) {
      logger.d("getTopPlaylists: size=$size, anchor=$anchor")
      try {
        remote.getTopPlaylists(size, anchor).playlists.also {
          it.lastOrNull()?.let { last ->
            this@HeartRepository.anchor = last.updateTime
          }
        }
      } catch (e: Exception) {
        logger.e(e, "getTopPlaylists() failed")
        return@withContext emptyList<Playlist>()
      }
    }
}
