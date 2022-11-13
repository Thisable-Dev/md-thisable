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
import com.devtedi.tedi.utils.ConstVal
import com.devtedi.tedi.utils.SharedPrefManager

class Notification(ctx : Context, params : WorkerParameters) : Worker(ctx, params)
{
    private lateinit var  pref : SharedPrefManager
    override fun doWork(): Result {
        try {
            //setupNotification()
            pref = SharedPrefManager(applicationContext)
            pref.setBooleanPreference(ConstVal.IS_MODEL_UPDATE, true)
          //  prepareTheModel()
            return Result.success()
        }
        catch (e : Exception)
        {
            return Result.failure()
        }
    }

    private fun getPendingIntent() : PendingIntent
    {
        val intent = Intent(applicationContext, HomeFragment::class.java)
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
            .setContentTitle(applicationContext.getString(R.string.label_judul_notifikasi))
            .setContentText(applicationContext.getString(R.string.label_konten_notifikasi))
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

