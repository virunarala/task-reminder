package com.example.taskreminder.tasks

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.taskreminder.R
import com.example.taskreminder.database.TaskDao
import com.example.taskreminder.database.TaskDatabase
import com.example.taskreminder.databinding.FragmentTaskBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private val viewModel : TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentTaskBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_task,
        container,false)


        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        //Setting the RecyclerView Adapter
        val adapter = TaskAdapter()
        binding.taskList.adapter = adapter

        //The following line DOES NOT work. Need to implement a workaround to disable EdgeEffect when there aren't enough items
        //in RecyclerView
//        binding.taskList.overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS

        //Showing a descriptive TextView when the list of Tasks is empty
        viewModel.taskCount.observe(viewLifecycleOwner) {
            if (it == 0) {
                binding.emptyListTextView.visibility = View.VISIBLE
            }
        }


        binding.addTaskFab.setOnClickListener {
            view?.apply{
                findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToTaskAddFragment())
            }
        }

        //Calling the function to create a NotificationChannel at the start of the app
        //NOTE: It is safe to call this repeatedly because trying to create an existing channel results in no operation being performed.
        createChannel(getString(R.string.task_notification_channel_id),getString(R.string.task_notification_channel_name))

        return binding.root
    }

    //Function to create a NotificationChannel
    private fun createChannel(channelId : String, channelName : String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Task Reminders"

            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}