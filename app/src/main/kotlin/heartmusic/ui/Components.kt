package heartmusic.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable

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
