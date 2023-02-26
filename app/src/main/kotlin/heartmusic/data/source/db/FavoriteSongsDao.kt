package heartmusic.data.source.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import heartmusic.data.FavoriteSong

@Dao
interface FavoriteSongsDao {
  @Query("SELECT * FROM favoritesong")
  suspend fun getAll(): List<FavoriteSong>

  @Query("SELECT * FROM favoritesong")
  fun getPaging(): PagingSource<Int, FavoriteSong>

  @Query("SELECT * FROM favoritesong LIMIT :size OFFSET :offset")
  suspend fun get(offset: Int, size: Int): List<FavoriteSong>

  @Query("SELECT * FROM favoritesong WHERE id = :songId LIMIT 1")
  suspend fun findById(songId: Long): FavoriteSong?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(songs: List<FavoriteSong>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(song: FavoriteSong)

  @Query("DELETE FROM favoritesong WHERE id = :id")
  suspend fun delete(id: Long)
}
