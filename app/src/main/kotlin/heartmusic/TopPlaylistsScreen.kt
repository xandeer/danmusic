package heartmusic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import heartmusic.data.Playlist
import heartmusic.ui.ProgressIndicator
import heartmusic.ui.PullRefreshingIndicator
import heartmusic.ui.appending
import heartmusic.ui.refreshing
import heartmusic.ui.theme.HeartMusicTheme
import heartmusic.viewmodel.TopPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopPlaylistsScreen(
  navController: NavController,
) {
  val vm: TopPlaylistViewModel = getViewModel()
  val playlists = vm.playlists.collectAsLazyPagingItems()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    TopAppBar(modifier = Modifier
      // Overlay the pull refresh indicator.
      .zIndex(1f)
      .clickable {
        scope.launch {
          state.animateScrollToItem(0)
        }
      },
      title = { Text(stringResource(id = TabScreen.Top.resourceId)) })
    Playlists(
      state = state,
      playlists = playlists
    ) { playlist ->
      navController.navigate(SubScreen.Songs.route(playlist.id)) {
        restoreState = true
      }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Playlists(
  playlists: LazyPagingItems<Playlist>,
  state: LazyListState,
  onItemClick: (Playlist) -> Unit = {}
) {
  val refreshing by playlists.refreshing
  val pullRefreshState = rememberPullRefreshState(refreshing, playlists::refresh)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .pullRefresh(state = pullRefreshState)
  ) {
    val appending by playlists.appending
    LazyColumn(
      state = state,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 64.dp)
    ) {
      if (!refreshing) {
        items(playlists) {
          it?.let { playlist ->
            PlayListItem(playlist = playlist, onClick = onItemClick)
          }
        }
      }
      if (appending) {
        item { ProgressIndicator() }
      }
    }

    PullRefreshingIndicator(refreshing = refreshing, state = pullRefreshState)
  }
}

@Composable
private fun PlayListItem(playlist: Playlist, onClick: (Playlist) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        onClick(playlist)
      },
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
      coverImgUrl = "https://picsum.photos/200/300"
    )
    PlayListItem(playlist) {}
  }
}
