package com.example.taskreminder.database

import androidx.room.*


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task : Task) : Long

    @Update
    fun update(task : Task)

    @Query("SELECT * FROM task_table WHERE taskId = :taskId")
    fun getTask(taskId : Long) : Task?

    @Query("SELECT * FROM task_table ORDER BY priority ASC")
    fun getAllTasks() : List<Task?>

    @Query("DELETE FROM task_table WHERE taskId = :taskId")
    fun delete(taskId : Long)

    //For Testing
    @Query("SELECT * FROM task_table ORDER BY taskId DESC LIMIT 1")
    fun getLastTask(): Task?

    @Query("SELECT 1 FROM task_table LIMIT 1")
    fun isNotEmpty() : Int?
}