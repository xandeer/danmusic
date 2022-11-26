package danmusic.app.data.source.remote

import danmusic.app.data.PlaylistsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DanRemoteDataSource {
  @GET("top/playlist/highquality")
  suspend fun getTopPlaylists(
    @Query("limit") size: Int = 1,
    @Query("before") anchor: Long = 0
  ): PlaylistsResponse
}
