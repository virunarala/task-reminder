package com.example.taskreminder.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.taskreminder.utils.sendNotification

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val taskId = intent.getLongExtra("taskId",0)
        val taskTitle = intent.getStringExtra("taskTitle") ?: "A Task is due today"

        val notificationManager = ContextCompat.getSystemService(context,
            NotificationManager::class.java) as NotificationManager

        notificationManager.sendNotification(taskId.toInt(),taskTitle,context)
    }
}