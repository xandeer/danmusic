package heartmusic

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import heartmusic.data.PlaylistQuerySong
import heartmusic.data.asMediaItems
import heartmusic.ui.LoadingScreen
import heartmusic.viewmodel.PlayerViewModel
import heartmusic.viewmodel.TopPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistSongsScreen() {
  val vm: TopPlaylistViewModel = getViewModel()
  AnimatedVisibility(
    visible = vm.currentPlaylist != null,
    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
    modifier = Modifier.fillMaxSize()
  ) {
    val playerVm: PlayerViewModel = getViewModel()
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val playlist = remember { vm.currentPlaylist!! }
    val songs = vm.getSongs(playlist = playlist).collectAsLazyPagingItems()

    BackHandler {
      vm.currentPlaylist = null
    }

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      TopAppBar(modifier = Modifier.clickable {
        scope.launch {
          state.animateScrollToItem(0)
        }
      },
        title = { Text(text = playlist.name) })
      Songs(songs = songs, state = state) {
        playerVm.play(playlist.id, vm.songs, it)
      }
    }

    LoadingScreen(visible = songs.loadState.refresh is LoadState.Loading)

    val player: ExoPlayer = get()
    LaunchedEffect(key1 = songs.itemSnapshotList.items) {
      vm.songs = songs.itemSnapshotList.items

      if (vm.currentPlaylist?.id == playerVm.playlistId) {
        vm.songs.filter { it !in playerVm.songs }
          .also {
            playerVm.appendSongs(it)
            player.addMediaItems(it.asMediaItems())
          }
      }
    }
  }
}

@Composable
private fun Songs(
  songs: LazyPagingItems<PlaylistQuerySong>,
  state: LazyListState,
  onItemClick: (PlaylistQuerySong) -> Unit = {}
) {
  LazyColumn(
    state = state,
    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 164.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // todo: why there are duplicated items with the same id?
    items(songs, key = { "${it.id}-$it" }) { song ->
      song?.let { SongItem(song = it, onClick = onItemClick) }
    }
  }
}

@Composable
private fun SongItem(
  song: PlaylistQuerySong,
  onClick: (PlaylistQuerySong) -> Unit = {}
) {
  Row(
    Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.medium)
      .clickable { onClick(song) },
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
