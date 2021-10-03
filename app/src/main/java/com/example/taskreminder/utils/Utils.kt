package com.example.taskreminder.utils

import android.content.res.Resources
import com.example.taskreminder.R

fun convertPriorityStringToInt(priority : String,resources : Resources):Int{
    val priorityArray = resources.getStringArray(R.array.priority_array)
    return when(priority){
        priorityArray[0]->0 //Very important
        priorityArray[1]->1 //High
        priorityArray[2]->2 //Medium
        priorityArray[3]->3 //Low
        priorityArray[4]->4 //Unimportant
        else->1
    }
}