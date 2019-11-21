package com.github.yandoroshenko

package object activitytimer {
  case class ActiveTime(
      hours: Long,
      minutes: Long,
      seconds: Long
    )

  val MillisKey = "millis"
  val IntervalBeginningTimestamp = "screenOnTimestamp"
  val LastUpdateTimestampKey = "lastUpdateTimestamp"

  val PreferencesFile = "com.github.yandoroshenko.activitytimer"
  val UpdateActiveTimeAction = "com.github.yandoroshenko.activitytimer.UpdateActiveTimeAction"
}
