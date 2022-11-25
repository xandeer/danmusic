package danmusic.app.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DanApi {
  @GET("top/playlist/highquality")
  suspend fun getTopPlaylists(
    @Query("limit") limit: Int = 1
  ): PlaylistsResponse
}

private val retrofit = Retrofit.Builder()
  .baseUrl("https://danmusic.vercel.app/")
  .addConverterFactory(MoshiConverterFactory.create())
  .build()

val danApi: DanApi = retrofit.create(DanApi::class.java)
