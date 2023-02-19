package heartmusic

sealed class SubScreen(val route: String) {
  object Songs : SubScreen("songs") {
    fun route(playlistId: Long) = "$route/$playlistId"
  }
}
