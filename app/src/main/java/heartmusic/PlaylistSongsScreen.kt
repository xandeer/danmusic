package heartmusic

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import heartmusic.data.PlaylistQuerySong
import heartmusic.viewmodel.TopPlaylistViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistSongsScreen() {
  val vm: TopPlaylistViewModel = getViewModel()
  AnimatedVisibility(
    visible = vm.currentPlaylist != null,
    enter = slideInHorizontally(initialOffsetX = { it }),
    exit = slideOutHorizontally(targetOffsetX = { it }),
    modifier = Modifier.fillMaxSize()
  ) {
    val playlist = remember { vm.currentPlaylist!! }
    val state = rememberLazyListState()
    val songs = vm.getSongs(playlistId = playlist.id).collectAsLazyPagingItems()


    BackHandler {
      vm.currentPlaylist = null
    }

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      TopAppBar(title = { Text(playlist.name) })
      Songs(songs = songs, state = state)
    }
  }
}

@Composable
private fun Songs(songs: LazyPagingItems<PlaylistQuerySong>, state: LazyListState) {
  LazyColumn(
    state = state,
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items(songs, key = { it.id }) { song ->
      song?.let { SongItem(song = it) }
    }
  }
}

@Composable
private fun SongItem(
  song: PlaylistQuerySong
) {
  Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      modifier = Modifier
        .size(120.dp)
        .clip(MaterialTheme.shapes.medium)
        .background(Color.LightGray),
      model = song.picUrl, contentDescription = null
    )
    Text(
      text = song.name,
      style = MaterialTheme.typography.bodyLarge,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
  }
}
