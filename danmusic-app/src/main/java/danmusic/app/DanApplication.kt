package danmusic.app

import android.app.Application
import danmusic.app.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class DanApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    startKoin {
      androidLogger()
      androidContext(this@DanApplication)
      modules(DataModule)
    }
  }
}
