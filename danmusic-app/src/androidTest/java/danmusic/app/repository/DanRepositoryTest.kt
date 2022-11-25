package danmusic.app.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import danmusic.app.network.danApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DanRepositoryTest {
  private val danRepository = DanRepository(danApi)

  @Test
  fun getTopPlaylistsSuccess() {
    runBlocking { danRepository.getTopPlaylists() }.let {
      assertThat(it.size).isGreaterThan(0)
    }
  }

  @Test
  fun get3TopPlaylists() {
    runBlocking { danRepository.getTopPlaylists(limit = 3) }.let {
      assertThat(it.size).isEqualTo(3)
    }
  }
}
