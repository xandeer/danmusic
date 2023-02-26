package heartmusic.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import heartmusic.data.FavoriteSong
import heartmusic.data.source.db.HeartPlaylistDb
import heartmusic.data.source.remote.HeartRemoteDataSource

class LocalFavoriteRepository(
  private val db: HeartPlaylistDb,
  private val remote: HeartRemoteDataSource,
) {
  @OptIn(ExperimentalPagingApi::class)
  fun getFavoriteSongs() = Pager(
    config = PagingConfig(pageSize = ITEMS_PER_PAGE, initialLoadSize = 1),
    remoteMediator = FavoriteSongsRemoteMediator(db = db, remote = remote)
  ) {
    db.favoriteSongs().getPaging()
  }

  private val dao = db.favoriteSongs()

  suspend fun getSongById(id: Long) = dao.findById(id)

  suspend fun delete(id: Long) = dao.delete(id)

  suspend fun insert(song: FavoriteSong) = dao.insert(song)
}
