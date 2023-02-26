package heartmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import heartmusic.data.FavoriteSong
import heartmusic.data.source.LocalFavoriteRepository

class FavoriteSongViewModel(private val repo: LocalFavoriteRepository) : ViewModel() {
  val songs = repo.getFavoriteSongs().flow.cachedIn(viewModelScope)

  suspend fun isFavorite(id: Long) = repo.getSongById(id) != null

  suspend fun delete(id: Long) = repo.delete(id)

  suspend fun insert(song: FavoriteSong) = repo.insert(song)
}
