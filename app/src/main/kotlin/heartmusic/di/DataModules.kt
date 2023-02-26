package heartmusic.di

import androidx.media3.exoplayer.ExoPlayer
import heartmusic.data.source.HeartRepository
import heartmusic.data.source.LocalFavoriteRepository
import heartmusic.data.source.db.HeartPlaylistDb
import heartmusic.data.source.remote.HeartRemoteDataSource
import heartmusic.viewmodel.FavoriteSongViewModel
import heartmusic.viewmodel.PlayerViewModel
import heartmusic.viewmodel.TopPlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val DataModule = module {
  single {
    Retrofit.Builder()
      .baseUrl("https://heartmusic.vercel.app/")
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(HeartRemoteDataSource::class.java)
  }

  single { HeartPlaylistDb.create(androidContext(), false) }

  singleOf(::HeartRepository)

  viewModelOf(::TopPlaylistViewModel)

  singleOf(::PlayerViewModel)
  single {
    ExoPlayer.Builder(androidContext())
      .build()
      .apply {
        playWhenReady = true
      }
  }

  singleOf(::LocalFavoriteRepository)
  singleOf(::FavoriteSongViewModel)
}
