package heartmusic.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritesong")
data class FavoriteSong(
  @PrimaryKey
  val id: Long,
  val name: String,
  val picUrl: String,
  val songUrl: String?
)
