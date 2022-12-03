package heartmusic.data.source.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction

private const val TOP_PLAYLIST = "top_playlist"

@Dao
interface CacheTimeDao {
  @Query("SELECT * FROM cache_time WHERE type = :type")
  suspend fun cacheTimeByType(type: String): CacheTime?

  @Transaction
  suspend fun lastTopPlaylistUpdateTime(): Long {
    return cacheTimeByType(TOP_PLAYLIST)?.time ?: 0
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(cache: CacheTime)

  @Transaction
  suspend fun updateTopPlaylistUpdateTime() {
    insert(CacheTime(TOP_PLAYLIST, System.currentTimeMillis()))
  }
}

@Entity(tableName = "cache_time")
data class CacheTime(
  @PrimaryKey
  val type: String,
  val time: Long,
)
