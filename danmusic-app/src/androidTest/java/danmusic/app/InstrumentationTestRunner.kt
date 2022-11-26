package danmusic.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.test.runner.AndroidJUnitRunner
import danmusic.app.di.DataModule
import org.koin.core.context.startKoin
import timber.log.Timber

class InstrumentationTestRunner : AndroidJUnitRunner() {
  override fun newApplication(
    cl: ClassLoader?,
    className: String?,
    context: Context?
  ): Application {
    return super.newApplication(cl, TestApplication::class.java.name, context)
  }

  class TestApplication : Application() {
    override fun onCreate() {
      super.onCreate()
      Timber.plant(object : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
          println(
            "${
              when (priority) {
                Log.VERBOSE -> "V"
                Log.DEBUG -> "D"
                Log.INFO -> "I"
                Log.WARN -> "W"
                Log.ERROR -> "E"
                Log.ASSERT -> "A"
                else -> "L?"
              }
            } | $tag | ${message.replace("\n", " ")}"
          )
        }
      })
      startKoin {
        modules(DataModule)
      }
    }
  }
}
