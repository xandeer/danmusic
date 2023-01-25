package heartmusic.data.source.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import heartmusic.data.Playlist

@Dao
interface HeartPlaylistDao {
  @Query("SELECT * FROM playlists ORDER BY indexInResponse ASC")
  fun playlists(): PagingSource<Int, Playlist>

  @Query("DELETE FROM playlists WHERE playlistId = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM playlists ORDER BY indexInResponse ASC")
  suspend fun getAll(): List<Playlist>

  @Query("SELECT * FROM playlists WHERE playlistId = :id")
  suspend fun getById(id: Long): Playlist?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(playlists: List<Playlist>)

  @Query("delete from playlists")
  suspend fun deleteAll()

  @Query("SELECT MAX(indexInResponse) + 1 FROM playlists")
  suspend fun getNextIndex(): Int?
}
