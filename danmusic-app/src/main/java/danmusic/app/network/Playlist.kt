package danmusic.app.network

data class Playlist(
  val name: String,
  val id: Long,
  val coverImgUrl: String,
  val description: String,
)

data class PlaylistsResponse(
  val code: Int,
  val playlists: List<Playlist>,
)
