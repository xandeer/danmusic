package heartmusic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TimeKtTests : StringSpec({
  "1_000.toMinutesSeconds() should return 00:01" {
    1_000L.toMinutesSeconds() shouldBe "00:01"
  }
  "(1_000 * 60 + 1_000 * 40).toMinutesSeconds() should return 01:40" {
    (1_000L * 60 + 1_000 * 40).toMinutesSeconds() shouldBe "01:40"
  }
  "(1_000L * 60 * 70 + 1_000 * 35).toMinutesSeconds() should return 70:35" {
    (1_000L * 60 * 70 + 1_000 * 35).toMinutesSeconds() shouldBe "70:35"
  }
})
