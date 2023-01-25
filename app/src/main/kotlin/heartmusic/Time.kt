package heartmusic

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

typealias Millis = Long

/**
 * Convert milliseconds to minutes and seconds.
 * @return A string in the format of "mm:ss".
 */
fun Millis.toMinutesSeconds(): String {
  return milliseconds.let {
    val minutes = it.inWholeMinutes
    val secondsDuration = it - minutes.minutes
    "%02d:%02d".format(minutes, secondsDuration.inWholeSeconds)
  }
}
