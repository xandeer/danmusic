package heartmusic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimeKtTests : StringSpec({
  "1.seconds.inWholeMilliseconds.toMinutesSeconds() should return 00:01" {
    1.seconds.inWholeMilliseconds.toMinutesSeconds() shouldBe "00:01"
  }
  "(1.minutes + 40.seconds).inWholeMilliseconds.toMinutesSeconds() should return 01:40" {
    (1.minutes + 40.seconds).inWholeMilliseconds.toMinutesSeconds() shouldBe "01:40"
  }
  "(70.minutes + 35.seconds).inWholeMilliseconds.toMinutesSeconds() should return 70:35" {
    (70.minutes + 35.seconds).inWholeMilliseconds.toMinutesSeconds() shouldBe "70:35"
  }
})
