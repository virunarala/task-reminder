package com.example.taskreminder.taskedit

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskreminder.R
import com.example.taskreminder.databinding.FragmentTaskEditBinding
import com.example.taskreminder.taskadd.TaskEditTextData
import com.example.taskreminder.utils.getSpannableAlarmLabel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TaskEditFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel : TaskEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : FragmentTaskEditBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_task_edit,
            container,false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        val args = TaskEditFragmentArgs.fromBundle(requireArguments())
        binding.titleEditText.setText(args.taskTitle)
        binding.descriptionEditText.setText(args.taskDescription)
        binding.alarmSwitch.setChecked(args.taskAlarmFlag)
        viewModel._dateText.value = args.taskDate
        viewModel._timeText.value = args.taskTime

        //Dynamically setting the text of Alarm switch's title and description strings of different sizes using SpannableString
        binding.alarmSwitch.text = getSpannableAlarmLabel()

        //Setting Alarm Switch visibility
        if(args.taskDate.isEmpty()||args.taskTime.isEmpty())
            binding.alarmSwitch.visibility = View.GONE


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
        lateinit var dateArgument: Date
        var dayArgument: Int
        var monthArgument: Int
        var yearArgument: Int
//        //Using variables to ensure a new DatePicker is created with the Date that was set
//        if(args.taskDate!="") {
//            dateArgument = dateFormat.parse(args.taskDate)!!
//            dayArgument = dateArgument.date
//            monthArgument = dateArgument.month
//            //Since the Date object's year parameter is computed as given year - 1900, 1900 is being added
//            yearArgument = dateArgument.year + 1900
//        }

        //DatePicker *Created as an inner class to have access to ViewModel
        class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

                if(args.taskDate!=""){
                    //Using variables to ensure a new DatePicker is created with the Date that was set
                    dateArgument = dateFormat.parse(args.taskDate)!!
                    dayArgument = dateArgument.date
                    monthArgument = dateArgument.month
                    //Since the Date object's year parameter is computed as given year - 1900, 1900 is being added
                    yearArgument = dateArgument.year + 1900

                    calendar.set(Calendar.DAY_OF_MONTH,dayArgument)
                    calendar.set(Calendar.MONTH,monthArgument)
                    calendar.set(Calendar.YEAR,yearArgument)
                }




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

                if(viewModel._dateText.value!=null && viewModel._timeText.value!=null)
                    binding.alarmSwitch.visibility = View.VISIBLE
            }
        }


        val timeFormat = SimpleDateFormat("HH:mm",Locale.ENGLISH)
        lateinit var timeArgument: Date


        //Using variables to ensure a new DatePicker is created with the Date that was set
        var hourArgument:Int
        var minuteArgument:Int

        //TimePicker *Created as an inner class to have access to ViewModel
        class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

                if(args.taskTime!="") {
                    timeArgument = timeFormat.parse(args.taskTime)!!
                    hourArgument = timeArgument.hours
                    minuteArgument = timeArgument.minutes

                    calendar.set(Calendar.HOUR_OF_DAY, hourArgument)
                    calendar.set(Calendar.MINUTE, minuteArgument)
                }

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

                if(viewModel._dateText.value!=null && viewModel._timeText.value!=null)
                    binding.alarmSwitch.visibility = View.VISIBLE
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