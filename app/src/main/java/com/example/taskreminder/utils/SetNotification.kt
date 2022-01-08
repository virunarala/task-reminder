package com.example.taskreminder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.example.taskreminder.receiver.AlarmReceiver
import com.example.taskreminder.taskadd.TaskEditTextData
import java.text.SimpleDateFormat
import java.util.*

fun setNotification(taskId: Long, task: TaskEditTextData, application: Context) {

    //Fields Alarm and Notification features
    val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager



    val notifyIntent = Intent(application, AlarmReceiver::class.java)
        .putExtra("taskId", taskId)
        .putExtra("taskTitle", task.title)

    //Creating a PendingIntent for Notification
    val notifyPendingIntent = PendingIntent.getBroadcast(
        application,
        taskId.toInt(),
        notifyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
        )

    if (task.date != "" && task.time != "") {
        val triggerDate =
            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(task.date)!!
        val triggerTime = SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(task.time)!!

        val day = triggerDate.date
        val month = triggerDate.month
        val year = triggerDate.year + 1900

        //Setting the notification to go off 3 hours before the specified time
        //If due is set at 12.30, Notification wil go off at 9.00
        val triggerHour = triggerTime.hours - 3
        //Variable unused for now
        val triggerMinute = triggerTime.minutes


        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
//      calendar.set(year,month,day,triggerHour,0,0) Uncomment after testing
        //For testing
        calendar.set(year, month, day, triggerTime.hours, triggerTime.minutes, 0)

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            notifyPendingIntent
        )
    }

}