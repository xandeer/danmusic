package heartmusic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import heartmusic.data.Playlist
import heartmusic.ui.theme.HeartMusicTheme
import heartmusic.viewmodel.TopPlaylistViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopPlaylistsScreen() {
  val vm: TopPlaylistViewModel = getViewModel()
  val playlists = vm.playlists.collectAsLazyPagingItems()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    TopAppBar(title = { Text("Playlists") })
    Playlists(playlists = playlists)
  }
  LoadStates(loadState = playlists.loadState) {
    playlists.retry()
  }
}

@Composable
private fun LoadStates(
  loadState: CombinedLoadStates,
  retry: () -> Unit
) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    when (loadState.refresh) {
      is LoadState.Loading -> CircularProgressIndicator()
      is LoadState.Error -> RetryButton(onClick = retry)
      else -> Unit
    }
  }
}

@Composable
private fun RetryButton(onClick: () -> Unit) {
  Button(onClick = onClick) {
    Text(
      text = "Loading failed, tap to retry",
    )
  }
}

@Preview
@Composable
private fun PreviewRetryButton() {
  HeartMusicTheme {
    RetryButton(onClick = {})
  }
}

@Composable
private fun Playlists(playlists: LazyPagingItems<Playlist>) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(16.dp, 8.dp)
  ) {
    items(playlists) {
      it?.let { playlist ->
        PlayListItem(playlist = playlist)
      }
    }
  }
}

@Composable
private fun PlayListItem(playlist: Playlist) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      modifier = Modifier
        .size(120.dp)
        .clip(MaterialTheme.shapes.medium)
        .background(Color.LightGray),
      model = playlist.coverImgUrl, contentDescription = null
    )

    Column {
      Text(
        text = playlist.name,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = playlist.description,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
      )
    }
  }
}

@Preview
@Composable
private fun PreviewPlaylistItem() {
  HeartMusicTheme {
    val playlist = Playlist(
      id = 1,
      name = "Playlist 1",
      description = "Description 1",
      coverImgUrl = "https://picsum.photos/200/300",
      updateTime = 0
    )
    PlayListItem(playlist)
  }
}
