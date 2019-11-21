package com.github.yandoroshenko.activitytimer

import java.util.{GregorianCalendar, Timer, TimerTask}

import android.app.Service
import android.content.{BroadcastReceiver, Context, Intent, IntentFilter}
import android.os.IBinder
import android.util.Log
import com.github.yandoroshenko.activitytimer.utils._

class TimerService extends Service {

  val Delay: Long = 0
  val Period: Long = 10 * 1000

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    Log.i("TimerService", "Starting service")
    registerScreenOffReceiver()

    new Timer(true).schedule(new TimerTask {
      override def run(): Unit = {
        sendBroadcast(new Intent(UpdateActiveTimeAction))
      }
    }, Delay, Period)
    Log.i("TimerService", "Service started")
  }

  private def registerScreenOffReceiver(): Unit = {
    Log.i("TimerService", "Registering receiver")

    val receiver = new BroadcastReceiver() {
      override def onReceive(context: Context, intent: Intent): Unit = {
        intent.getAction match {
          case Intent.ACTION_SCREEN_ON =>
            Log.i("TimerService", "Received SCREEN_ON")
            storeLong(context, IntervalBeginningTimestamp, System.currentTimeMillis())
          case Intent.ACTION_SCREEN_OFF =>
            Log.i("TimerService", "Received SCREEN_OFF")

            getLong(context, IntervalBeginningTimestamp).map { lastTimestamp =>

              val newMillis = calculateMillis(context, System.currentTimeMillis(), lastTimestamp)
              Log.i("TimerService", s"Storing new millis: $newMillis")

              storeLong(context, MillisKey, newMillis)
              deleteKey(context, IntervalBeginningTimestamp)
            }
        }
      }
    }
    val filter = new IntentFilter(Intent.ACTION_SCREEN_OFF)
    filter.addAction(Intent.ACTION_SCREEN_ON)
    registerReceiver(receiver, filter)
    Log.i("TimerService", "Receiver registered")
  }
}
