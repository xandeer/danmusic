package heartmusic

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import heartmusic.data.asMediaItem
import heartmusic.ui.theme.HeartMusicTheme
import heartmusic.viewmodel.PlayerViewModel
import heartmusic.viewmodel.TopPlaylistViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun PlayerBar(modifier: Modifier = Modifier) {
  val vm: TopPlaylistViewModel = getViewModel()
  val playerVm: PlayerViewModel = getViewModel()
  val player: ExoPlayer = get()

  val playingSong = vm.playingSong

  AnimatedVisibility(
    modifier = modifier.shadow(4.dp),
    visible = playingSong != null,
    enter = slideInVertically(initialOffsetY = { it }),
    exit = slideOutVertically(targetOffsetY = { it })
  ) {
    val isNextEnabled by remember(playerVm.currentIndex, vm.songs) {
      derivedStateOf {
        playerVm.currentIndex.inc() < vm.songs.size
      }
    }
    AudioController(
      imgUrl = playingSong!!.picUrl,
      title = playingSong.name,
      isPlaying = playerVm.isPlaying,
      onTogglePlaying = playerVm::toggle,
      isNextEnabled = isNextEnabled,
      onNext = {
        playerVm.play(playerVm.currentIndex.inc())
      }
    )
  }

  LaunchedEffect(key1 = vm.songs) {
    player.clearMediaItems()
    player.addMediaItems(vm.songs.map { it.asMediaItem() })
    player.prepare()
  }
  LaunchedEffect(key1 = playerVm.currentIndex, key2 = vm.songs) {
    if (playerVm.currentIndex in 0 until vm.songs.size) {
      player.seekTo(playerVm.currentIndex, 0L)
    }
  }
  LaunchedEffect(key1 = playerVm.isPlaying) {
    if (playerVm.isPlaying) {
      player.play()
    } else {
      player.pause()
    }
  }
  DisposableEffect(Unit) {
    onDispose { player.release() }
  }
}

@Composable
private fun AudioController(
  imgUrl: String,
  title: String,
  isPlaying: Boolean,
  onTogglePlaying: () -> Unit,
  isNextEnabled: Boolean = true,
  onNext: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .background(MaterialTheme.colorScheme.surface)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      model = imgUrl,
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape),
      contentDescription = title
    )
    Text(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 12.dp),
      text = title,
      style = MaterialTheme.typography.titleSmall
    )
    IconButton(onClick = onTogglePlaying) {
      Icon(
        imageVector = if (isPlaying) Icons.Filled.ArrowDropDown
        else Icons.Filled.PlayArrow,
        contentDescription = null
      )
    }
    IconButton(onClick = onNext, enabled = isNextEnabled) {
      Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
    }
  }
}

@Composable
@Preview
private fun PreviewController() {
  HeartMusicTheme {
    var isPlaying by remember {
      mutableStateOf(true)
    }
    AudioController(
      imgUrl = Uri.EMPTY.toString(),
      title = "Hello",
      isPlaying = isPlaying,
      onTogglePlaying = {
        isPlaying = !isPlaying
      },
      onNext = { }
    )
  }
}
