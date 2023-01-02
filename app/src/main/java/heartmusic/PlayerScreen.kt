package heartmusic

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.paging.compose.collectAsLazyPagingItems
import heartmusic.data.PlaylistQuerySong
import heartmusic.viewmodel.TopPlaylistViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
internal fun PlayerScreen() {
  val context = LocalContext.current
  val vm: TopPlaylistViewModel = getViewModel()
  val player = remember {
    ExoPlayer.Builder(context).build()
  }

  AnimatedVisibility(
    visible = vm.playingSong != null,
    enter = slideInVertically(initialOffsetY = { it }),
    exit = slideOutVertically(targetOffsetY = { it }),
//    modifier = Modifier.background(MaterialTheme.colorScheme.background)
  ) {
    BackHandler {
      vm.playingSong = null
    }
    AudioPlayer(player)
  }
  DisposableEffect(Unit) {
    onDispose { player.release() }
  }
}

@Composable
private fun AudioPlayer(player: ExoPlayer) {
  val vm: TopPlaylistViewModel = getViewModel()

  LaunchedEffect(key1 = vm.songs) {
    player.clearMediaItems()
    vm.songs.map { MediaItem.fromUri(it.songUrl) }.forEach { player.addMediaItem(it) }
//  player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
    player.playWhenReady = true
    player.prepare()
  }
  AndroidView(factory = {
    PlayerView(it).apply {
      useController = true
      this@apply.player = player
    }
  })
}
