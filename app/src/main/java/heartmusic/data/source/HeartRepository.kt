package heartmusic.data.source

import heartmusic.data.Playlist
import heartmusic.data.source.remote.HeartRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class HeartRepository(private val remote: HeartRemoteDataSource) {
  private val timber by lazy { Timber.tag("DanRepository") }

  fun playListPagingSource() = TopPlaylistPagingSource(remote)

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
      timber.d("getTopPlaylists: size=$size, anchor=$anchor")
      try {
        remote.getTopPlaylists(size, anchor).playlists.also {
          it.lastOrNull()?.let { last ->
            this@HeartRepository.anchor = last.updateTime
          }
        }
      } catch (e: Exception) {
        timber.e(e, "getTopPlaylists() failed")
        return@withContext emptyList<Playlist>()
      }
    }
}
