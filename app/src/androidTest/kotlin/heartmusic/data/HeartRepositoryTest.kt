package heartmusic.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import heartmusic.data.source.HeartRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class HeartRepositoryTest {
  private val repo: HeartRepository by inject(HeartRepository::class.java)

  @Test
  fun getTopPlaylistsSuccess() {
    Timber.tag("DanRepo")
    runBlocking { repo.getTopPlaylists() }.let {
      assertThat(it.size).isGreaterThan(0)
    }
  }
}
