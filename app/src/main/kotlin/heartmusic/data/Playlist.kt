package heartmusic.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
  @PrimaryKey @ColumnInfo(name = "playlistId") val id: Long,
  val name: String,
  val coverImgUrl: String,
  val description: String
) {
  var updateTime: Long = 0
  /**
   * To be consistent backend order.
   */
  var indexInResponse: Int = -1
}

data class PlaylistsResponse(
  val code: Int,
  val playlists: List<Playlist>,
)
