package com.devtedi.tedi.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devtedi.tedi.MainActivity
import com.devtedi.tedi.R
import com.devtedi.tedi.presentation.home.HomeFragment

class Notification(ctx : Context, params : WorkerParameters) : Worker(ctx, params)
{

    override fun doWork(): Result {
        setupNotification()

        return Result.success()
    }

    private fun getPendingIntent() : PendingIntent
    {
        val intent = Intent(applicationContext, MainActivity::class.java)
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(request_code, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun setupNotification()
    {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID )
            .setSmallIcon(R.drawable.ic_logo_tedi)
            .setContentTitle("Judul Notifikasi")
            .setContentText("Content Notifikasi")
            .setContentIntent(this.getPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

            notification.setChannelId(NOTIFICATION_CHANNEL_ID)

            notificationManager.createNotificationChannel(channel)

        }

        notificationManager.notify(request_code, notification.build())


    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1"
        const val CHANNEL_NAME = "notification_compat"
        const val request_code : Int = 0
    }
}

