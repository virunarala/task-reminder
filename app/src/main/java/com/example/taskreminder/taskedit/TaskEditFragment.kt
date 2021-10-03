package com.example.taskreminder.taskedit

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.RelativeSizeSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskreminder.R
import com.example.taskreminder.database.TaskDatabase
import com.example.taskreminder.databinding.FragmentTaskEditBinding
import com.example.taskreminder.taskadd.TaskEditTextData
import java.text.SimpleDateFormat
import java.util.*

class TaskEditFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel : TaskEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : FragmentTaskEditBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_task_edit,
            container,false)




        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDao
        val viewModelFactory = TaskEditViewModelFactory(dataSource,application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(TaskEditViewModel::class.java)

        binding.viewModel = viewModel


        val args = TaskEditFragmentArgs.fromBundle(requireArguments())
        binding.titleEditText.setText(args.taskTitle)
        binding.descriptionEditText.setText(args.taskDescription)
        binding.alarmSwitch.setChecked(args.taskAlarmFlag)
        viewModel._dateText.value = args.taskDate
        viewModel._timeText.value = args.taskTime

        //Dynamically setting the text of Alarm switch's title and description strings of different sizes using SpannableString
        val s="Alarm\nWould you like to reminded with an alarm?"
        val span : SpannableString = SpannableString(s)
        span.setSpan(RelativeSizeSpan(1.75f),0,5,0)
        binding.alarmSwitch.text = span


        //Populating the PrioritySpinner
        ArrayAdapter.createFromResource(requireContext(),R.array.priority_array,android.R.layout.simple_spinner_item)
            .also{ adapter->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.prioritySpinner.adapter = adapter
                //Selecting the Priority item of the spinner based on the value passed from TaskFragment to TaskEditFragment
                binding.prioritySpinner.setSelection(args.taskPriority)
            }
        binding.prioritySpinner.onItemSelectedListener = this

        //Handling the Cancel button : Cancel the Edit to go back to TaskFragment(Navigate Up)
        binding.cancelTextViewButton.setOnClickListener{
            findNavController().navigateUp()
        }


        //Handling click event of UpdateTask button
        viewModel.isUpdateTaskClicked.observe(viewLifecycleOwner, {
            if(it){
                viewModel.updateTask(
                    args.taskId,
                    TaskEditTextData(binding.titleEditText.text.toString(),
                    binding.descriptionEditText.text.toString(),
                    binding.prioritySpinner.selectedItem.toString(),
                    binding.datePicker.text.toString(),
                    binding.timePicker.text.toString(),
                    binding.alarmSwitch.isChecked)
                )
            }
        })

        //Toast displayed on updating the task and Navigation to TaskFragment
        viewModel.hasTaskUpdated.observe(viewLifecycleOwner,{
            if(it){
                Toast.makeText(context,"Changes Saved",Toast.LENGTH_SHORT).show()
                findNavController().navigate(TaskEditFragmentDirections.actionTaskEditFragmentToTaskFragment())
            }
        })

        //Handling click event of DeleteTask button
        viewModel.isDeleteTaskClicked.observe(viewLifecycleOwner, {
            if(it){
                viewModel.deleteTask(args.taskId)
            }
        })

        //Toast displayed on deleting the task and Navigation to TaskFragment
        viewModel.hasTaskDeleted.observe(viewLifecycleOwner,{
            if(it){
                Toast.makeText(context,"Task Deleted",Toast.LENGTH_SHORT).show()
                findNavController().navigate(TaskEditFragmentDirections.actionTaskEditFragmentToTaskFragment())
            }
        })


        val calendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
        val dateArgument = dateFormat.parse(args.taskDate)!!

        //Using variables to ensure a new DatePicker is created with the Date that was set
        var dayArgument = dateArgument.date
        var monthArgument = dateArgument.month
        //Since the Date object's year parameter is computed as given year - 1900, 1900 is being added
        var yearArgument = dateArgument.year + 1900


        //DatePicker *Created as an inner class to have access to ViewModel
        class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

                calendar.set(Calendar.DAY_OF_MONTH,dayArgument)
                calendar.set(Calendar.MONTH,monthArgument)
                calendar.set(Calendar.YEAR,yearArgument)



                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)


                return DatePickerDialog(requireContext(),this,year,month,day)
            }

            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                calendar.set(Calendar.DAY_OF_MONTH,day)
                calendar.set(Calendar.MONTH,month)
                calendar.set(Calendar.YEAR,year)

                //Changing Calendar's Args to represent the most recent changes
                dayArgument = calendar.get(Calendar.DAY_OF_MONTH)
                monthArgument = calendar.get(Calendar.MONTH)
                yearArgument = calendar.get(Calendar.YEAR)

                //Formatting Date into a String
                val date = dateFormat.format(calendar.time)

                //Setting ViewModel's Date
                viewModel._dateText.value = date
            }
        }


        val timeFormat = SimpleDateFormat("HH:mm",Locale.ENGLISH)
        val timeArgument = timeFormat.parse(args.taskTime)!!

        //Using variables to ensure a new DatePicker is created with the Date that was set
        var hourArgument = timeArgument.hours
        var minuteArgument = timeArgument.minutes

        //TimePicker *Created as an inner class to have access to ViewModel
        class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

                calendar.set(Calendar.HOUR_OF_DAY,hourArgument)
                calendar.set(Calendar.MINUTE,minuteArgument)

                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                return TimePickerDialog(requireContext(),this,hour,minute, DateFormat.is24HourFormat(requireContext()))
            }

            override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY,hour)
                calendar.set(Calendar.MINUTE,minute)

                hourArgument = calendar.get(Calendar.HOUR_OF_DAY)
                minuteArgument = calendar.get(Calendar.MINUTE)


                val time = timeFormat.format(calendar.time)

                viewModel._timeText.value = time
            }
        }


        //Handling click event of DatePicker
        viewModel.isDatePickerClicked.observe(viewLifecycleOwner, {
            if(it){
                val fragment = DatePickerFragment()
                fragment.show(parentFragmentManager,"datePicker")

                viewModel.datePickerClickComplete()
            }
        })

        //Handling click event of TimePicker
        viewModel.isTimePickerClicked.observe(viewLifecycleOwner, {
            if(it){
                val fragment = TimePickerFragment()
                fragment.show(parentFragmentManager,"timePicker")

                viewModel.timePickerClickComplete()
            }
        })


        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}