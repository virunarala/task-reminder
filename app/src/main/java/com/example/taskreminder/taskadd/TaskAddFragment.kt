package com.example.taskreminder.taskadd

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.style.RelativeSizeSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskreminder.R
import com.example.taskreminder.database.TaskDatabase
import com.example.taskreminder.databinding.FragmentTaskAddBinding
import com.example.taskreminder.utils.getSpannableAlarmLabel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TaskAddFragment : Fragment(), AdapterView.OnItemSelectedListener{

    private val viewModel : TaskAddViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentTaskAddBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_task_add,
        container,false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        //Dynamically setting the text of Alarm switch's title and description strings of different sizes using SpannableString
        binding.alarmSwitch.text = getSpannableAlarmLabel()

        //Populating the PrioritySpinner
        ArrayAdapter.createFromResource(requireContext(),R.array.priority_array,android.R.layout.simple_spinner_item)
            .also{adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.prioritySpinner.adapter = adapter
                binding.prioritySpinner.setSelection(1)
            }
        binding.prioritySpinner.onItemSelectedListener=this



        //Handling click event of Add Task button
        viewModel.isAddTaskClicked.observe(viewLifecycleOwner) {
            if (it) {
                if (TextUtils.isEmpty(binding.titleEditText.text))
                    binding.titleEditText.error = "Title is required"
                else {
                    viewModel.addTask(
                        TaskEditTextData(
                            binding.titleEditText.text.toString(),
                            binding.descriptionEditText.text.toString(),
                            binding.prioritySpinner.selectedItem.toString(),
                            binding.datePicker.text.toString(),
                            binding.timePicker.text.toString(),
                            binding.alarmSwitch.isChecked
                        )
                    )
                }
            }
        }


        //Toast displayed on creation of a new task and Navigation to TaskFragment
        viewModel.hasTaskCreated.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "New Task Added", Toast.LENGTH_SHORT).show()
                findNavController().navigate(TaskAddFragmentDirections.actionTaskAddFragmentToTaskFragment())
            }
        }


        val calendar = Calendar.getInstance()


        //DatePicker *Created as an inner class to have access to ViewModel
        class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {



            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)

                return DatePickerDialog(requireContext(),this,year,month,day)
            }

            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                calendar.set(Calendar.DAY_OF_MONTH,day)
                calendar.set(Calendar.MONTH,month)
                calendar.set(Calendar.YEAR,year)

                //Formatting Date into a String
                val dateFormatString = "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat(dateFormatString,Locale.ENGLISH)
                val date = dateFormat.format(calendar.time)

                //Setting ViewModel's Date
                viewModel._dateText.value = date

                if(viewModel._dateText.value!=null && viewModel._timeText.value!=null)
                    binding.alarmSwitch.visibility = View.VISIBLE
            }
        }

        //TimePicker *Created as an inner class to have access to ViewModel
        class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                return TimePickerDialog(requireContext(),this,hour,minute, DateFormat.is24HourFormat(requireContext()))
            }

            override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY,hour)
                calendar.set(Calendar.MINUTE,minute)

                val timeFormatString = "HH:mm"
                val timeFormat = SimpleDateFormat(timeFormatString, Locale.ENGLISH)
                val time = timeFormat.format(calendar.time)

                viewModel._timeText.value = time
                
                if(viewModel._dateText.value!=null && viewModel._timeText.value!=null)
                    binding.alarmSwitch.visibility = View.VISIBLE
            }
        }

        //Handling click event of DatePicker
        viewModel.isDatePickerClicked.observe(viewLifecycleOwner) {
            if (it) {
                val fragment = DatePickerFragment()
                fragment.show(parentFragmentManager, "datePicker")

                viewModel.datePickerClickComplete()

            }
        }

        //Handling click event of TimePicker
        viewModel.isTimePickerClicked.observe(viewLifecycleOwner) {
            if (it) {
                val fragment = TimePickerFragment()
                fragment.show(parentFragmentManager, "timePicker")

                viewModel.timePickerClickComplete()

            }
        }


        return binding.root
    }



    //PrioritySpinner callback
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

    }

    //PrioritySpinner callback
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}