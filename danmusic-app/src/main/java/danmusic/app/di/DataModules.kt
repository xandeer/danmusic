package danmusic.app.di

import danmusic.app.data.source.DanRepository
import danmusic.app.data.source.remote.DanRemoteDataSource
import danmusic.app.viewmodel.DanViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val DataModule = module {
  single {
    Retrofit.Builder()
      .baseUrl("https://danmusic.vercel.app/")
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(DanRemoteDataSource::class.java)
  }

  singleOf(::DanRepository)

  viewModelOf(::DanViewModel)
}
