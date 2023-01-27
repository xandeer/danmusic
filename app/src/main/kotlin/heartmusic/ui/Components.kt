package heartmusic.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProgressIndicator() {
//  CircularProgressIndicator(
//    modifier = Modifier.size(36.dp),
//    strokeWidth = 2.5.dp,
//    color = MaterialTheme.colorScheme.onSurface
//  )
  PullRefreshIndicator(
    refreshing = true,
    state = rememberPullRefreshState(
      refreshing = true,
      onRefresh = { })
  )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoxScope.PullRefreshingIndicator(
  refreshing: Boolean,
  state: PullRefreshState,
  modifier: Modifier = Modifier
) {
  // todo: why the indicator is visible when not pulling?
  if (refreshing || state.progress > 0) {
    PullRefreshIndicator(
      refreshing = refreshing, state = state,
      modifier = modifier.align(Alignment.TopCenter)
    )
  }
}
