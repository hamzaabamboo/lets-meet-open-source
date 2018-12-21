package org.thinc.comprog.letsmeet.common

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.feature.chat.ChatActivity
import android.graphics.BitmapFactory
import io.grpc.internal.ReadableBuffers.openStream
import android.graphics.Bitmap
import java.io.IOException
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "FirebaseMsgServ"
    val CHANNEL_ID = "let's meet notification channel"
    var counter = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        createNotificationChannel()

        remoteMessage?.data?.isNotEmpty()?.let {
            remoteMessage.data.let {
                val title = it["title"]
                val body = it["body"]
                val partyId = it["partyId"]
                val icon = it["icon"]

                val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val intent = Intent(this, ChatActivity::class.java).apply {
                    this.putExtra("partyId", partyId)
                    this.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                var bmp: Bitmap? = null
                try {
                    val `in` = URL(icon).openStream()
                    bmp = BitmapFactory.decodeStream(`in`)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setLargeIcon(bmp)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(body))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(counter++, notification)
            }
            Log.d(TAG, "Data payload" + remoteMessage.data["title"])
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}