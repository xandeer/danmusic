package danmusic.app.data.source

import danmusic.app.data.source.remote.DanRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import danmusic.app.data.Playlist
import timber.log.Timber

class DanRepository(private val remote: DanRemoteDataSource) {
  private val timber by lazy { Timber.tag("DanRepository") }

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
      remote.getTopPlaylists(size, anchor).playlists.also {
        it.lastOrNull()?.let { last ->
          this@DanRepository.anchor = last.updateTime
        }
        timber.d("playlists: $it")
      }
    }
}
