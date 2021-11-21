package com.example.taskreminder.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskreminder.database.Task
import androidx.recyclerview.widget.DiffUtil
import com.example.taskreminder.databinding.TaskItemViewBinding

class TaskAdapter : ListAdapter<Task,TaskAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(TaskItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }
    }

    class TaskViewHolder(private val binding : TaskItemViewBinding) : RecyclerView.ViewHolder(binding.root){

        init{
            binding.setClickListener{ view->
                binding.task?.let{ task->
                    navigateToEditFragment(task,view)
                }
            }
        }

        private fun navigateToEditFragment(task : Task,view : View){
            view.findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToTaskEditFragment(
                task.taskTitle,task.taskDescription,task.priorityValue,task.taskDate,task.taskTime,task.alarmFlag,task.taskId))
        }


        fun bind(task : Task){
            binding.task = task
            binding.executePendingBindings()
        }
    }
}