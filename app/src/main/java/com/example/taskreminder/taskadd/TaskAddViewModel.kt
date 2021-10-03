package com.example.taskreminder.taskadd

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskreminder.database.Task
import com.example.taskreminder.utils.convertPriorityStringToInt
import com.example.taskreminder.database.TaskDao
import com.example.taskreminder.receiver.AlarmReceiver
import com.example.taskreminder.utils.sendNotification
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class TaskAddViewModel(val database : TaskDao, application : Application) : AndroidViewModel(application) {


    private val _isDatePickerClicked = MutableLiveData<Boolean>()
    val isDatePickerClicked : LiveData<Boolean>
        get() = _isDatePickerClicked

    private val _isTimePickerClicked = MutableLiveData<Boolean>()
    val isTimePickerClicked : LiveData<Boolean>
        get() = _isTimePickerClicked

    private val _isAddTaskClicked = MutableLiveData<Boolean>()
    val isAddTaskClicked : LiveData<Boolean>
        get() = _isAddTaskClicked


    val _dateText = MutableLiveData<String>()
    val dateText : LiveData<String>
        get() = _dateText

    val _timeText = MutableLiveData<String>()
    val timeText : LiveData<String>
        get() = _timeText

    val hasTaskCreated = MutableLiveData<Boolean>()


    //Fields Alarm and Notification features
    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager





    fun onDatePickerClicked(){
        _isDatePickerClicked.value = true
    }

    fun datePickerClickComplete(){
        _isDatePickerClicked.value = false
    }

    fun onTimePickerClicked(){
        _isTimePickerClicked.value = true
    }

    fun timePickerClickComplete(){
        _isTimePickerClicked.value = false
    }


    fun onAddTaskClick(){
        _isAddTaskClicked.value = true
    }





    fun addTask(task : TaskEditTextData){
        viewModelScope.launch{
            val newTask = Task(
                taskTitle = task.title,
                taskDescription = task.description,
                priorityValue = convertPriorityStringToInt(task.priority,getApplication<Application>().resources),
                taskDate = task.date,
                taskTime = task.time,
                alarmFlag = task.alarm
            )
            val taskId = insert(newTask)
            hasTaskCreated.value = true

//            val notificationManager = ContextCompat.getSystemService(getApplication(),
//                NotificationManager::class.java) as NotificationManager
//
//            notificationManager.sendNotification(taskId,task.title,getApplication())

            val notifyIntent = Intent(getApplication(), AlarmReceiver::class.java)
                .putExtra("taskId",taskId)
                .putExtra("taskTitle",task.title)

            //Creating a PendingIntent for Notification
            val notifyPendingIntent = PendingIntent.getBroadcast(
                getApplication(),
                taskId.toInt(),
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val triggerDate = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse(task.date)!!
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
//            calendar.set(year,month,day,triggerHour,0,0) Uncomment after testing
            //For testing
            calendar.set(year,month,day,triggerTime.hours,triggerTime.minutes,0)

            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                notifyPendingIntent
            )


        }
    }

    suspend fun insert(task : Task) : Long{
        var taskId : Long
        withContext(Dispatchers.IO){
            taskId = database.insert(task)
        }
        return taskId
    }


}

//Data class to get the contents of EditTexts in the fragment
data class TaskEditTextData(
    val title : String,
    val description : String,
    val priority : String,
    val date : String,
    val time : String,
    val alarm : Boolean
)