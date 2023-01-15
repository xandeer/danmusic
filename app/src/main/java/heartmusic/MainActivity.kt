package heartmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import heartmusic.ui.theme.HeartMusicTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      HeartMusicTheme {
        ConstraintLayout(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
        ) {
          val (player) = createRefs()
          TopPlaylistsScreen()
          PlaylistSongsScreen()

          PlayerBar(modifier = Modifier.constrainAs(player) {
            bottom.linkTo(parent.bottom)
          })
        }
      }
    }
  }
}
