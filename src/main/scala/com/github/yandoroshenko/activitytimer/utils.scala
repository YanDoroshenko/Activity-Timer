package com.github.yandoroshenko.activitytimer

import java.util.GregorianCalendar
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

  def transformMillis(millis: Long): ActiveTime = {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - hours * 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - hours * 3600 - minutes * 60
    ActiveTime(hours, minutes, seconds)
  }

  def daysFromMillis(millis: Long): Long = TimeUnit.MILLISECONDS.toDays(millis)

  def millisSinceMidnight(millis: Long): Long = {
    val res = millis - daysFromMillis(millis) * 1000 * 60 * 60 * 24
    res
  }

  def getOffset(): Int = new GregorianCalendar().getTimeZone.getRawOffset

  def calculateMillis(
      context: Context,
      now: Long,
      lastTimestamp: Long
    ): Long = {
    val offset = getOffset()

    val daysNow = daysFromMillis(now + offset)

    val millis = getLong(context, LastUpdateTimestampKey) match {
      case Some(t) if daysFromMillis(t + offset) != daysNow && daysFromMillis(lastTimestamp + offset) != daysNow =>
        val ms = millisSinceMidnight(now + offset)
        Log.i("TimerService", s"New day, millis since midnight: $ms")
        ms
      case Some(t) if daysFromMillis(t + offset) != daysNow =>
        val ms = now - lastTimestamp
        Log.i("TimerService", s"New day, ms: $ms")
        ms
      case _ =>
        val ms = getLong(context, MillisKey).getOrElse(0L) + now - lastTimestamp
        Log.i("TimerService", s"New ms: $ms")
        ms
    }

    millis
  }
}
