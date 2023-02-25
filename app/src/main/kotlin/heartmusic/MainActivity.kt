package heartmusic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import heartmusic.ui.setContent
import heartmusic.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    render()
    observePlayerError()
    startService(Intent(this, PlayerService::class.java))
  }

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @OptIn(ExperimentalMaterial3Api::class)
  private fun render() {
    setContent {
      val navController = rememberNavController()

      Scaffold(bottomBar = {
        BottomNavigation(
          backgroundColor = MaterialTheme.colorScheme.background,
        ) {
          val navBackStackEntry by navController.currentBackStackEntryAsState()
          val currentDestination = navBackStackEntry?.destination

          tabScreens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            val contentColor by animateColorAsState(
              targetValue = if (selected) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onBackground
            )
            val scale = remember { Animatable(1f) }
            val spring = SpringSpec<Float>(stiffness = 300f, dampingRatio = 0.5f)
            LaunchedEffect(selected) {
              scale.animateTo(if (selected) 1.1f else 1f, spring)
              scale.animateTo(1f, spring)
            }
            BottomNavigationItem(
              modifier = Modifier.scale(scale.value),
              icon = { Icon(screen.icon, contentDescription = null, tint = contentColor) },
              label = { Text(stringResource(id = screen.resourceId), color = contentColor) },
              selected = selected,
              onClick = {
                if (selected && currentDestination?.route != currentDestination?.parent?.startDestinationRoute) {
                  navController.popBackStack()
                } else {
                  navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              })
          }
        }
      }) { innerPadding ->
        ConstraintLayout(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
        ) {
          val (player) = createRefs()
          BackHandler {
            moveTaskToBack(false)
          }
          NavHost(
            navController = navController,
            startDestination = TabScreen.Top.route
          ) {
            top(navController)

            (tabScreens - TabScreen.Top).forEach { ts ->
              composable(ts.route) {
                PlaceHolderScreen(ts)
              }
            }
          }

          PlayerBar(modifier = Modifier.constrainAs(player) {
            bottom.linkTo(parent.bottom)
          })
        }
      }
    }
  }

  private fun NavGraphBuilder.top(navController: NavController) {
    navigation(startDestination = "top-lists", route = TabScreen.Top.route) {
      composable("top-lists") { TopPlaylistsScreen(navController) }
      composable("${SubScreen.Songs.route}/{playlistId}") {
        it.arguments?.getString("playlistId").also { playlistId ->
          if (playlistId != null) {
            PlaylistSongsScreen(navController, playlistId.toLong())
          }
        }
      }
    }
  }

  @Composable
  private fun PlaceHolderScreen(screen: TabScreen) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
      Text(text = stringResource(id = screen.resourceId))
    }
  }

  private fun observePlayerError() {
    get<PlayerViewModel>().apply {
      viewModelScope.launch {
        errorFlow.collect {
          Toast.makeText(
            this@MainActivity,
            "Something wrong with player, try to pull refresh.",
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
  }
}
