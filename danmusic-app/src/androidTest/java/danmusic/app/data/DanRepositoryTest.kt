package danmusic.app.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import danmusic.app.data.source.DanRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class DanRepositoryTest {
  private val danRepository: DanRepository by inject(DanRepository::class.java)

  @Test
  fun getTopPlaylistsSuccess() {
    Timber.tag("DanRepo")
    runBlocking { danRepository.getTopPlaylists() }.let {
      assertThat(it.size).isGreaterThan(0)
    }
  }

  @Test
  fun getMoreTopPlaylists() {
    val first = runBlocking { danRepository.getTopPlaylists(size = 3) }.also {
      assertThat(it.size).isEqualTo(3)
    }

    runBlocking { danRepository.getTopPlaylists() }.let {
      assertThat(first.first().id).isNotEqualTo(it.first().id)
    }
  }
}
