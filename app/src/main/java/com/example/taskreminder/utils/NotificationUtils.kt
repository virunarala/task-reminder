package com.example.taskreminder.utils

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.taskreminder.R



fun NotificationManager.sendNotification(notificationId : Int,messageBody : String, applicationContext : Context){

    val notificationBuilder = NotificationCompat.Builder(applicationContext,
        applicationContext.getString(R.string.task_notification_channel_id))

        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(applicationContext.getString(R.string.task_notification_title))
        .setContentText(messageBody)

    notify(notificationId,notificationBuilder.build())
}
