package heartmusic

import java.util.concurrent.TimeUnit

typealias Millis = Long

/**
 * Convert milliseconds to minutes and seconds.
 * @return A string in the format of "mm:ss".
 */
fun Millis.toMinutesSeconds(): String {
  val minute = TimeUnit.MILLISECONDS.toMinutes(this)
  val second = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minute)
  return "%02d:%02d".format(minute, second)
}
