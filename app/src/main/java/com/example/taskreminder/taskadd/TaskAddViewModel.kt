package com.example.taskreminder.taskadd

import android.content.Context
import androidx.lifecycle.*
import com.example.taskreminder.database.Task
import com.example.taskreminder.utils.convertPriorityStringToInt
import com.example.taskreminder.database.TaskDao
import com.example.taskreminder.utils.setNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class TaskAddViewModel @Inject constructor
    (private val database : TaskDao, @ApplicationContext val application: Context) : ViewModel() {


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
                priorityValue = convertPriorityStringToInt(task.priority,application.resources),
                taskDate = task.date,
                taskTime = task.time,
                alarmFlag = task.alarm
            )
            val taskId = insert(newTask)
            hasTaskCreated.value = true


            if(newTask.alarmFlag)
                setNotification(taskId,task,application)

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