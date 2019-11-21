package com.github.yandoroshenko.activitytimer

import java.util.{Date, GregorianCalendar}

import com.github.yandoroshenko.activitytimer.utils._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class UtilsSpec extends WordSpec with Matchers {
  "millis since midnight" should {
    "return 0 at midnight" in {
      val midnight = new Date(2000, 0, 1).getTime + new GregorianCalendar().getTimeZone.getRawOffset
      millisSinceMidnight(midnight) shouldBe 0
    }

    "return correct millis" in {
      val now = System.currentTimeMillis()
      millisSinceMidnight(now) shouldBe now % (1000 * 60 * 60 * 24)
    }
  }

  "days from millis" should {
    "return 0 if millis are less than one day" in {
      val millis = 1000 * 60 * 60 * 24 - 1
      daysFromMillis(millis) shouldBe 0
    }

    "return 1 if millis are exactly one day" in {
      val millis = 1000 * 60 * 60 * 24
      daysFromMillis(millis) shouldBe 1
    }

    "return correct days" in {
      val millis = 1574190417043L
      val refDays = 18219
      daysFromMillis(millis) shouldBe refDays
    }
  }
}
