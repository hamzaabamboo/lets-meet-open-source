package org.thinc.comprog.letsmeet.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import androidx.core.app.NotificationCompat
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.feature.main.MainActivity


class ScheduledNotificationRecevier : BroadcastReceiver() {
    val TAG = "Schedule Recevier"
    val CHANNEL = "let's meet notification channel"
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent!!.getStringExtra("title")
        val id = intent.getStringExtra("id")
        val body = "Your party is in 5 min !"

        val notification = NotificationCompat.Builder(context!!, CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("$title is happening")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1929, notification)
    }
}