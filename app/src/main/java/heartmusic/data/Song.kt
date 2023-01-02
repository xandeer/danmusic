package heartmusic.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  foreignKeys = [ForeignKey(
    entity = Song::class,
    parentColumns = ["songId"],
    childColumns = ["songId"]
  ), ForeignKey(
    entity = Playlist::class,
    parentColumns = ["playlistId"],
    childColumns = ["playlistId"]
  )],
  indices = [Index("songId"), Index("playlistId")]
)
data class PlaylistSong(
  val playlistId: Long = 0,
  val songId: Long = 0,
  val offset: Int = 0,
) {
  @PrimaryKey(autoGenerate = true) var id: Long = 0
}

data class PlaylistQuerySong(
  val id: Long,
  val name: String,
  val picUrl: String,
  val offset: Int,
  val songUrl: String,
)

@Entity
data class Song(
  @PrimaryKey @ColumnInfo(name = "songId") val id: Long,
  val name: String,
  val publishTime: Long,
  @Embedded val al: Album,
) {
//  @Embedded var ar: List<Artist> = emptyList()
}

//@Entity
data class Artist(
//  @PrimaryKey
  @ColumnInfo(name = "artistId")
  val id: Long,
  @ColumnInfo(name = "artistName")
  val name: String
)

//@Entity
data class Album(
//  @PrimaryKey
  @ColumnInfo(name = "albumId")
  val id: Long,
  @ColumnInfo(name = "albumName")
  val name: String,
  val picUrl: String
)

data class PlaylistSongsResponse(
  val code: Int,
  val songs: List<Song>,
)

data class SongUrlResponse(
  val data: List<SongUrl>,
)

@Entity
data class SongUrl(
  @PrimaryKey @ColumnInfo(name = "songId") val id: Long,
  val url: String,
)
