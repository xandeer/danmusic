package heartmusic

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewModelScope
import heartmusic.ui.theme.HeartMusicTheme
import heartmusic.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    render()
    observePlayerError()
  }

  private fun render() {
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

  private fun observePlayerError() {
    get<PlayerViewModel>().apply {
      viewModelScope.launch {
        errorFlow.collect {
          Toast.makeText(this@MainActivity, "Something wrong with player", Toast.LENGTH_SHORT)
            .show()
        }
      }
    }
  }
}
