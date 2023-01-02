package heartmusic.data.source.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import heartmusic.data.PlaylistQuerySong
import heartmusic.data.PlaylistSong
import heartmusic.data.Song
import heartmusic.data.SongUrl

@Dao
interface PlaylistSongsDao {
  @Transaction
  @Query(
    "SELECT s.albumName, s.albumId, s.songId as id, s.name, s.picUrl, s.publishTime, p.`offset`, u.url as songUrl FROM song s " +
      "JOIN playlistsong p " +
      "ON s.songId = p.songId " +
      "JOIN songurl u " +
      "ON s.songId = u.songId " +
      "WHERE s.songId IN (SELECT p.songId FROM playlistsong p WHERE p.playlistId = :playlistId) " +
      "ORDER BY `offset` ASC"
  )
  fun playlistWithSongs(playlistId: Long): PagingSource<Int, PlaylistQuerySong>

  @Transaction
  @Query("SELECT * FROM song WHERE songId IN (SELECT p.songId FROM playlistsong p WHERE p.playlistId = :playlistId)")
  suspend fun allSongsInPlaylist(playlistId: Long): List<Song>

  @Query("SELECT MAX(`offset`) + 1 FROM playlistsong WHERE playlistId = :playlistId")
  suspend fun getNextOffset(playlistId: Long): Int?

  @Query("DELETE FROM playlistsong WHERE playlistId = :playlistId")
  suspend fun deleteByPlaylistId(playlistId: Long)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPlaylistSongs(playlistSongs: List<PlaylistSong>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSongs(songs: List<Song>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSongUrls(songs: List<SongUrl>)
}
