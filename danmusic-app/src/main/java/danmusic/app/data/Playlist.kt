package danmusic.app.data

data class Playlist(
  val name: String,
  val id: Long,
  val coverImgUrl: String,
  val description: String,
  val updateTime: Long
)

data class PlaylistsResponse(
  val code: Int,
  val playlists: List<Playlist>,
)
