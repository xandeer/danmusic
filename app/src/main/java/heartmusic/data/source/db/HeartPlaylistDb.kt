package heartmusic.data.source.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import heartmusic.data.Playlist
import heartmusic.data.PlaylistSong
import heartmusic.data.Song
import heartmusic.data.SongUrl
import java.util.concurrent.TimeUnit

@Database(
  entities = [
    Playlist::class,
    CacheTime::class,
    Song::class,
    PlaylistSong::class,
    SongUrl::class,
  ],
  version = 1,
  exportSchema = false
)
abstract class HeartPlaylistDb : RoomDatabase() {
  companion object {
    fun create(context: Context, useInMemory: Boolean): HeartPlaylistDb {
      val databaseBuilder = if (useInMemory) {
        Room.inMemoryDatabaseBuilder(context, HeartPlaylistDb::class.java)
      } else {
        Room.databaseBuilder(context, HeartPlaylistDb::class.java, "heartmusic.db")
      }
      return databaseBuilder
        .fallbackToDestructiveMigration()
        .build()
    }
  }

  abstract fun playlists(): HeartPlaylistDao

  abstract fun playlistSongs(): PlaylistSongsDao

  abstract fun cacheTime(): CacheTimeDao
}

val dbCacheTimeout = TimeUnit.MILLISECONDS.convert(100, TimeUnit.MINUTES)
