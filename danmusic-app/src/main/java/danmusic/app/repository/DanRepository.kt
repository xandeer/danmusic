package danmusic.app.repository

import danmusic.app.network.DanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DanRepository(private val api: DanApi) {
  suspend fun getTopPlaylists(limit: Int = 1) = withContext(Dispatchers.IO) {
    api.getTopPlaylists(limit).playlists
  }
}
