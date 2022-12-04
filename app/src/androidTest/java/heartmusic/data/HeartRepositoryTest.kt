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

  @Test
  fun getMoreTopPlaylists() {
    val first = runBlocking { repo.getTopPlaylists(size = 3) }.also {
      assertThat(it.size).isEqualTo(3)
    }

    runBlocking { repo.getTopPlaylists() }.let {
      assertThat(first.first().playlistId).isNotEqualTo(it.first().playlistId)
    }
  }
}
