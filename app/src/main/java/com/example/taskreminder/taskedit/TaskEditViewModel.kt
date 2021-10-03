package com.example.taskreminder.taskedit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskreminder.utils.convertPriorityStringToInt
import com.example.taskreminder.database.Task
import com.example.taskreminder.database.TaskDao
import com.example.taskreminder.taskadd.TaskEditTextData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskEditViewModel(val database : TaskDao,application : Application) :
    AndroidViewModel(application) {

    private val _isDatePickerClicked = MutableLiveData<Boolean>()
    val isDatePickerClicked : LiveData<Boolean>
        get() = _isDatePickerClicked

    private val _isTimePickerClicked = MutableLiveData<Boolean>()
    val isTimePickerClicked : LiveData<Boolean>
        get() = _isTimePickerClicked

    private val _isUpdateTaskClicked = MutableLiveData<Boolean>()
    val isUpdateTaskClicked : LiveData<Boolean>
        get() = _isUpdateTaskClicked

    private val _isDeleteTaskClicked = MutableLiveData<Boolean>()
    val isDeleteTaskClicked : LiveData<Boolean>
        get() = _isDeleteTaskClicked


    val _dateText = MutableLiveData<String>()
    val dateText : LiveData<String>
        get() = _dateText

    val _timeText = MutableLiveData<String>()
    val timeText : LiveData<String>
        get() = _timeText


    val hasTaskUpdated = MutableLiveData<Boolean>()

    val hasTaskDeleted = MutableLiveData<Boolean>()


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


    fun onUpdateTaskClick(){
        _isUpdateTaskClicked.value = true
    }

    fun onDeleteTaskClick(){
        _isDeleteTaskClicked.value = true
    }



    fun updateTask(taskId : Long, task : TaskEditTextData){
        viewModelScope.launch{
            val newTask = Task(
                taskId = taskId,
                taskTitle = task.title,
                taskDescription = task.description,
                priorityValue = convertPriorityStringToInt(task.priority,getApplication<Application>().resources),
                taskDate = task.date,
                taskTime = task.time,
                alarmFlag = task.alarm
            )
            update(newTask)
            hasTaskUpdated.value = true
        }
    }


    private suspend fun update(task : Task){
        withContext(Dispatchers.IO){
            database.update(task)
        }
    }

    fun deleteTask(taskId : Long){
        viewModelScope.launch{
            delete(taskId)
            hasTaskDeleted.value = true
        }
    }


    private suspend fun delete(taskId : Long){
        withContext(Dispatchers.IO){
            database.delete(taskId)
        }
    }
}