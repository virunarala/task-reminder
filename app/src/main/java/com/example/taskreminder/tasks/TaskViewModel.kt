package com.example.taskreminder.tasks

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.taskreminder.database.Task
import com.example.taskreminder.database.TaskDao
import com.example.taskreminder.database.TaskDatabase
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class TaskViewModel(val database : TaskDao, application : Application) : AndroidViewModel(application) {

    private val _task = MutableLiveData<List<Task?>?>()
    val task : LiveData<List<Task?>?>
            get() = _task

    private val _taskCount = MutableLiveData<Int>()
    val taskCount : LiveData<Int>
        get() = _taskCount


    init{
        showData()
    }




    private fun showData(){
        viewModelScope.launch{
            _task.value = getAllTasks()
            _taskCount.value = task.value?.size ?: 0
        }
    }

    private suspend fun getAllTasks() : List<Task?>{
        return withContext(Dispatchers.IO){
            database.getAllTasks()
        }
    }





}