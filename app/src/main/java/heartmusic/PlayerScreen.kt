package heartmusic

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import heartmusic.data.PlaylistQuerySong
import heartmusic.data.asMediaItem
import heartmusic.ui.theme.HeartMusicTheme
import heartmusic.viewmodel.PlayerViewModel
import heartmusic.viewmodel.TopPlaylistViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val TAG = "PlayerBar"

private val timber get() = Timber.tag(TAG)

@OptIn(ObsoleteCoroutinesApi::class)
@Composable
internal fun PlayerBar(modifier: Modifier = Modifier) {
  val vm: TopPlaylistViewModel = getViewModel()
  val playerVm: PlayerViewModel = getViewModel()
  val player: ExoPlayer = get()

  val playingSong by remember {
    derivedStateOf { playerVm.songs.getOrNull(playerVm.currentIndex) }
  }

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
    playingSong?.also {
      AudioController(
        imgUrl = it.picUrl,
        title = it.name,
        isPlaying = playerVm.isPlaying,
        position = playerVm.positionMs,
        duration = playerVm.durationMs,
        onTogglePlaying = playerVm::toggle,
        isNextEnabled = isNextEnabled,
        onNext = {
          playerVm.next()
        }
      )
    }
  }

  LaunchedEffect(key1 = playerVm.playlistId) {
    player.clearMediaItems()
    playerVm.playingSong?.asMediaItem()
    player.addMediaItems(playerVm.songs.asMediaItems())
    player.prepare()
  }

  LaunchedEffect(key1 = vm.songs) {
    if (vm.currentPlaylist?.id == playerVm.playlistId) {
      vm.songs.filter { it !in playerVm.songs }
        .also {
          playerVm.appendSongs(it)
          player.addMediaItems(it.asMediaItems())
        }
    }
  }

  LaunchedEffect(key1 = playerVm.currentIndex, key2 = playerVm.playlistId) {
    if (playerVm.currentIndex in 0 until vm.songs.size) {
      timber.d("seekTo: ${playerVm.currentIndex}")
      player.seekTo(playerVm.currentIndex, 0L)
    }
  }

  LaunchedEffect(key1 = playerVm.isPlaying) {
    if (playerVm.isPlaying) {
      player.play()
      ticker(30).consumeAsFlow().collect {
        (player.currentPosition to player.duration).also { (position, duration) ->
          if (position > 0 && duration > 0) {
            playerVm.updatePosition(position, duration)
          }
        }
      }
    } else {
      player.pause()
    }
  }

  DisposableEffect(Unit) {
    player.addListener(object : Listener {
      override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        timber.d("onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.title}")
      }

      override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
      ) {
        timber.d("onPositionDiscontinuity: ${newPosition.mediaItemIndex}, ${newPosition.positionMs}/${newPosition.contentPositionMs}")
        playerVm.updateByPosition(newPosition.mediaItemIndex)
      }
    })
    onDispose {
      timber.d("player dispose")
      player.release()
    }
  }
}

private fun List<PlaylistQuerySong>.asMediaItems(): List<MediaItem> {
  return map { it.asMediaItem() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioController(
  imgUrl: String,
  title: String,
  isPlaying: Boolean,
  position: Long = 0,
  duration: Long = 0,
  onTogglePlaying: () -> Unit,
  isNextEnabled: Boolean = true,
  onNext: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .background(MaterialTheme.colorScheme.surface)
  ) {
    Slider(
      modifier = Modifier
        .height(1.dp)
        .fillMaxWidth()
        .zIndex(1f),
      value = position.toFloat(),
      onValueChange = {
        timber.d("onValueChange: $it")
      },
      valueRange = 0f..duration.toFloat(),
      thumb = {},
    )
    Row(
      modifier = Modifier
        .fillMaxSize()
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
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 12.dp)
      ) {
        Text(
          text = title,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.titleSmall
        )
        Text(
          text = "${position.toTime()}/${duration.toTime()}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
      }
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
}

private fun Long.toTime(): String {
  val minute = TimeUnit.MILLISECONDS.toMinutes(this)
  val second = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minute)
  return "%02d:%02d".format(minute, second)
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
      title = "Hello Feat: Add a progress slider Feat: Add a progress slider",
      position = 45000,
      duration = 155000,
      isPlaying = isPlaying,
      onTogglePlaying = {
        isPlaying = !isPlaying
      },
      onNext = { }
    )
  }
}
