package heartmusic.app.di

import heartmusic.app.data.source.HeartRepository
import heartmusic.app.data.source.remote.HeartRemoteDataSource
import heartmusic.app.viewmodel.TopPlaylistViewModel
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

  singleOf(::HeartRepository)

  viewModelOf(::TopPlaylistViewModel)
}
