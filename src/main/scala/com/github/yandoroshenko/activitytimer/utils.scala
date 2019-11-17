package com.github.yandoroshenko.activitytimer

import java.util.concurrent.TimeUnit

import android.content.Context
import android.util.Log

object utils {


  def storeLong(
      context: Context,
      key: String,
      value: Long
    ): Boolean = {
    getSharedPreferences(context).edit().putLong(key, value).commit()
  }

  def deleteKey(context: Context, key: String): Boolean =
    getSharedPreferences(context).edit().remove(key).commit()

  def getLong(context: Context, key: String): Option[Long] =
    getSharedPreferences(context).getLong(key, -1) match {
      case -1 =>
        None
      case v =>
        Some(v)
    }

  private def getSharedPreferences(context: Context) = context.getSharedPreferences(PreferencesFile, 0)

  def calculateMillis(lastTimestamp: Long, currentTimestamp: Long, millis: Long): Long = {
    val nowDays = daysFromMillis(currentTimestamp)
    val lastTimestampDays = daysFromMillis(lastTimestamp)
    
    val res = if (nowDays != lastTimestampDays) millisSinceMidnight(currentTimestamp) else millis + currentTimestamp - lastTimestamp

    res
  }

  def transformMillis(millis: Long): ActiveTime = {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - hours * 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - hours * 3600 - minutes * 60
    ActiveTime(hours, minutes, seconds)
  }

  private def daysFromMillis(millis: Long) = TimeUnit.MILLISECONDS.toDays(millis)

  private def millisSinceMidnight(millis: Long) = {
    val res = millis - daysFromMillis(millis) * 1000 * 60 * 60 * 24
    Log.w("TimerService", s"Millis since midnight - $res")
    res
  }
}
