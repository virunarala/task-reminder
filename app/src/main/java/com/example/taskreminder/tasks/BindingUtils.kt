package com.example.taskreminder.tasks

import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskreminder.R
import com.example.taskreminder.database.Task


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView : RecyclerView, data : List<Task>?){
    val adapter = recyclerView.adapter as TaskAdapter
//    val parent = recyclerView.parent as LinearLayout
//    val emptyListTextView = parent.findViewById(R.id.emptyListTextView) as TextView
        adapter.submitList(data)
}

@BindingAdapter("setPriority")
fun TextView.setPriorityStringFromInt(priority : Int?){
    val priorityArray = resources.getStringArray(R.array.priority_array)
    priority?.let{
        text = when(priority){
            0->priorityArray[0]
            1->priorityArray[1]
            2->priorityArray[2]
            3->priorityArray[3]
            4->priorityArray[4]
            else->priorityArray[1]
        }
    }
}


@BindingAdapter("setAlarm")
fun ImageButton.setAlarmDrawableFromBool(alarm : Boolean?){
    alarm?.let {
        if (alarm)
            setImageResource(R.drawable.ic_baseline_access_alarm_24_enabled)
        else
            setImageResource(R.drawable.ic_baseline_access_alarm_24_diasbled)
    }
}