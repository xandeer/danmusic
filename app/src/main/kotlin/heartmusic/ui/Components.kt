package heartmusic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoadingScreen(visible: Boolean) {
  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(),
    exit = fadeOut()
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .swallowClick()
        .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator()
    }
  }
}

@Preview
@Composable
private fun LoadingScreenPreview() {
  LoadingScreen(true)
}
