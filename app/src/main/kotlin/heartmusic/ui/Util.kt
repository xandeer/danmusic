package heartmusic.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import heartmusic.ui.theme.HeartMusicTheme

fun Modifier.swallowClick(): Modifier = clickableWithoutRipple {}

private fun Modifier.clickableWithoutRipple(
  enabled: Boolean = true,
  onClick: () -> Unit,
): Modifier = composed {
  clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    enabled = enabled,
    onClick = onClick,
  )
}

val LazyPagingItems<*>.refreshing: State<Boolean>
  @Composable get() = remember {
    derivedStateOf { loadState.refresh is LoadState.Loading }
  }

val LazyPagingItems<*>.appending: State<Boolean>
  @Composable get() = remember {
    derivedStateOf { loadState.append is LoadState.Loading }
  }

fun ComponentActivity.setContent(content: @Composable () -> Unit) {
  setContent {
    HeartMusicTheme(dynamicColor = false) {
      content()
    }
  }
}
