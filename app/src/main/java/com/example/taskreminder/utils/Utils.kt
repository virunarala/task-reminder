package com.example.taskreminder.utils

import android.content.res.Resources
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
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

fun getSpannableAlarmLabel(): SpannableString{
    val s="Alarm\nWould you like to reminded with an alarm?"
    val span : SpannableString = SpannableString(s)
    span.setSpan(RelativeSizeSpan(1.75f),0,5,0)
    return span
}