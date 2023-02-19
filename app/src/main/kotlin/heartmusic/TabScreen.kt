package heartmusic

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabScreen(
  val route: String,
  val icon: ImageVector = Icons.Filled.Home,
  @StringRes val resourceId: Int
) {
  object Home : TabScreen("home", Icons.Filled.Home, R.string.home)
  object Top : TabScreen("top", Icons.Filled.KeyboardArrowUp, R.string.top)
  object Search : TabScreen("search", Icons.Filled.Search, R.string.search)
  object Profile : TabScreen("profile", Icons.Filled.Person, R.string.profile)
}

val tabScreens = listOf(
  TabScreen.Home,
  TabScreen.Top,
  TabScreen.Search,
  TabScreen.Profile,
)
