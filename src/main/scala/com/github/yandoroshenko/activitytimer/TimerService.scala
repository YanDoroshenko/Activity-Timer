package com.github.yandoroshenko.activitytimer

import java.util.{Timer, TimerTask}

import android.app.Service
import android.content.{BroadcastReceiver, Context, Intent, IntentFilter}
import android.os.IBinder
import android.util.Log
import com.github.yandoroshenko.activitytimer.utils._

class TimerService extends Service {

  val Delay: Long = 0
  val Period: Long = 10 * 1000

  Log.w("TimerService", "Service object is created")

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    Log.w("TimerService", "TimerService started")
    registerScreenOffReceiver()

    new Timer(true).schedule(new TimerTask {
      override def run(): Unit = {
        sendBroadcast(new Intent(UpdateActiveTimeAction))
      }
    }, Delay, Period)
  }

  private def registerScreenOffReceiver(): Unit = {
    val receiver = new BroadcastReceiver() {
      override def onReceive(context: Context, intent: Intent): Unit = {
        intent.getAction match {
          case Intent.ACTION_SCREEN_ON =>
            storeLong(context, LastTimestampKey, System.currentTimeMillis())
          case Intent.ACTION_SCREEN_OFF =>
            getLong(context, LastTimestampKey).map { lastTimestamp =>
              val newMillis = calculateMillis(lastTimestamp, System.currentTimeMillis(), getLong(context, MillisKey).getOrElse(0L))
              storeLong(context, MillisKey, newMillis)
              deleteKey(context, LastTimestampKey)
            }
        }
      }
    }
    val filter = new IntentFilter(Intent.ACTION_SCREEN_OFF)
    filter.addAction(Intent.ACTION_SCREEN_ON)
    registerReceiver(receiver, filter)
    Log.w("TimerService", "Registered broadcast receiver")
  }
}
