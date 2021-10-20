package com.example.taskreminder.tasks

import android.app.Application
import androidx.lifecycle.*
import com.example.taskreminder.database.Task
import com.example.taskreminder.database.TaskDao
import com.example.taskreminder.database.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val database : TaskDao) : ViewModel() {

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