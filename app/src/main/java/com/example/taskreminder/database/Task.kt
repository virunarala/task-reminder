package com.example.taskreminder.database

import androidx.room.*

@Entity(tableName="task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId : Long = 0L,

    @ColumnInfo(name="title")
    val taskTitle : String,

    @ColumnInfo(name="description")
    val taskDescription : String,

    @ColumnInfo(name="priority")
    val priorityValue : Int = 1,

    @ColumnInfo(name="date")
    val taskDate : String,

    @ColumnInfo(name="time")
    val taskTime : String,

    @ColumnInfo(name="alarm")
    val alarmFlag : Boolean
)

