package heartmusic.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
  @PrimaryKey
  val id: Long,
  val name: String,
  val coverImgUrl: String,
  val description: String,
  val updateTime: Long
) {
  /**
   * To be consistent backend order.
   */
  var indexInResponse: Int = -1
}

data class PlaylistsResponse(
  val code: Int,
  val playlists: List<Playlist>,
)
