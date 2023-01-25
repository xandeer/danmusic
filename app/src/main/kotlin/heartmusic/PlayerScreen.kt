package heartmusic

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import heartmusic.ui.theme.HeartMusicTheme
import heartmusic.viewmodel.PlayerViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

private val logger get() = logger("PlayerBar")

@Composable
internal fun PlayerBar(modifier: Modifier = Modifier) {
  val playerVm: PlayerViewModel = getViewModel()

  val playingSong by remember {
    derivedStateOf { playerVm.songs.getOrNull(playerVm.currentIndex) }
  }

  AnimatedVisibility(
    modifier = modifier,
    visible = playingSong != null,
    enter = slideInVertically(initialOffsetY = { it }),
    exit = slideOutVertically(targetOffsetY = { it })
  ) {
    val player: ExoPlayer = get()
    playingSong?.also {
      AudioController(
        imgUrl = it.picUrl,
        title = it.name,
        isPlaying = playerVm.isPlaying,
        position = playerVm.position,
        duration = playerVm.duration,
        onTogglePlaying = playerVm::toggle,
        isNextEnabled = player.hasNextMediaItem(),
        onNext = player::seekToNext
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioController(
  imgUrl: String,
  title: String,
  isPlaying: Boolean,
  position: Millis = 0,
  duration: Millis = 0,
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
        logger.d("onValueChange: $it")
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
      var rotation by remember { mutableStateOf(0f) }
      LaunchedEffect(key1 = imgUrl) {
        rotation = 0f
      }
      if (isPlaying) {
        LaunchedEffect(Unit) {
          animate(
            initialValue = rotation,
            targetValue = rotation + 360,
            animationSpec = infiniteRepeatable(
              animation = tween(
                durationMillis = 10000,
                easing = LinearEasing
              ),
              repeatMode = RepeatMode.Restart
            )
          ) { value, _ ->
            rotation = value
          }
        }
      }

      AsyncImage(
        model = imgUrl,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .rotate(rotation),
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
          text = "${position.toMinutesSeconds()}/${duration.toMinutesSeconds()}",
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
