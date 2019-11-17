package com.github.yandoroshenko.activitytimer

import android.appwidget.{AppWidgetManager, AppWidgetProvider}
import android.content.{Context, Intent, _}
import android.util.Log
import android.widget.RemoteViews
import com.github.yandoroshenko.activitytimer.utils._

class ActivityTimerWidgetProvider extends AppWidgetProvider {

  override def onReceive(context: Context, intent: Intent): Unit = {
    Log.w("TimerService", s"onReceive - ${intent.getAction}")

    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = new ComponentName(context, classOf[ActivityTimerWidgetProvider])
    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

    intent.getAction match {
      case UpdateActiveTimeAction =>
        processIntent(context, intent)(millis => {
          val views = updateText(context, transformMillis(millis))
          appWidgetManager.updateAppWidget(appWidgetIds, views)
        })
      case AppWidgetManager.ACTION_APPWIDGET_ENABLED =>
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
    getLong(context, LastTimestampKey) match {
      case Some(lastTimestamp) =>
        val now = System.currentTimeMillis()
        val millis = calculateMillis(lastTimestamp, now, getLong(context, MillisKey).getOrElse(0L))
        storeLong(context, LastTimestampKey, now)
        storeLong(context, MillisKey, millis)
        f(millis)
      case _ => ()
    }
  }
}
