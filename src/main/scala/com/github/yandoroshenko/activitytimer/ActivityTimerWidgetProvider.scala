package com.github.yandoroshenko.activitytimer

import android.appwidget.{AppWidgetManager, AppWidgetProvider}
import android.content.{Context, Intent, _}
import android.util.Log
import android.widget.RemoteViews
import com.github.yandoroshenko.activitytimer.utils._

class ActivityTimerWidgetProvider extends AppWidgetProvider {

  override def onReceive(context: Context, intent: Intent): Unit = {
    Log.i("TimerService", s"onReceive - ${intent.getAction}")

    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = new ComponentName(context, classOf[ActivityTimerWidgetProvider])
    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

    intent.getAction match {
      case UpdateActiveTimeAction =>
        processIntent(context, intent)(millis => {
          Log.i("TimerService", s"Updating millis: $millis")
          val activeTime = transformMillis(millis)
          Log.i("TimerService", s"Active time: $activeTime")
          val views = updateText(context, activeTime)
          appWidgetManager.updateAppWidget(appWidgetIds, views)
        })
      case AppWidgetManager.ACTION_APPWIDGET_ENABLED =>
        storeLong(context, IntervalBeginningTimestamp, System.currentTimeMillis())
        deleteKey(context, MillisKey)
        context.startService(new Intent(context, classOf[TimerService]))
      case _ =>
        super.onReceive(context, intent)
    }
  }

  private def updateText(context: Context, time: ActiveTime): RemoteViews = {
    val v = new RemoteViews(context.getPackageName, R.layout.activity_timer_appwidget)
    v.setTextViewText(R.id.hourText, s"${time.hours}h")
    v.setTextViewText(R.id.minuteText, s"${time.minutes}m")
    v.setTextViewText(R.id.secondText, s"${time.seconds}s")
    v
  }

  def processIntent(context: Context, intent: Intent)(f: Long => Unit): Unit = {
    getLong(context, IntervalBeginningTimestamp) match {
      case Some(lastTimestamp) =>
        val now = System.currentTimeMillis()
        val millis = calculateMillis(context, now, lastTimestamp)
        Log.i("TimerService", s"Storing new millis: $millis")

        storeLong(context, LastUpdateTimestampKey, now)
        storeLong(context, IntervalBeginningTimestamp, now)
        storeLong(context, MillisKey, millis)
        f(millis)
      case _ => ()
    }
  }
}
