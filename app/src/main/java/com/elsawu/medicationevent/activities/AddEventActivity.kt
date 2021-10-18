package com.elsawu.medicationevent.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.elsawu.medicationevent.beans.MedicationEvent
import com.elsawu.medicationevent.databinding.ActivityAddEventBinding
import com.elsawu.medicationevent.utils.DateTimeUtil
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEventBinding
    private var medicationName: String = ""
    private var medicationType: String = ""
    private var dateString: String = ""
    private var timeString: String = ""
    private lateinit var dateTime: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getIntExtra("maxId", -1) + 1
        val medicationList =
            intent.getSerializableExtra("medicationList") as List<MedicationEvent.User.Medication>
        initSpinner(medicationList)

        binding.buttonAdd.setOnClickListener {

            if (dateString.isEmpty() || timeString.isEmpty()) {
                Toast.makeText(this, "Please select a date and time.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (medicationName.isEmpty() || medicationType.isEmpty()) {
                Toast.makeText(this, "Please choose a medication.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dateTime.after(Date())) {
                Toast.makeText(
                    this,
                    "Please record a time earlier than current time.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // send new added event back to EventListActivity
            val intent = Intent()
            val newEvent = MedicationEvent.Event(null, dateTime, medicationName, medicationType, id)
            intent.putExtra("newEvent", newEvent)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    // create the Spinner for the medications
    private fun initSpinner(medications: List<MedicationEvent.User.Medication>) {
        val medicationArray = arrayOfNulls<String>(medications.size + 1)
        // add a blank item as the first item to spinner
        medicationArray[0] = ""
        for (i in medications.indices) {
            medicationArray[i + 1] = medications[i].name
        }
        val spinnerMedicationName = binding.spinnerMedicationName
        spinnerMedicationName.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, medicationArray
        )
        spinnerMedicationName.setSelection(0)
        spinnerMedicationName.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    medicationName = medications[position - 1].name
                    medicationType = medications[position - 1].medicationtype
                } else {
                    medicationName = ""
                    medicationType = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                TODO("Nothing to implement")
            }
        }
    }

    // button click event handler
    fun buttonClicked(view: View) {
        when (view) {
            binding.textViewDataTime -> { // textView to pick date and time
                // datePicker
                val dateCalendar = Calendar.getInstance()
                var mYear = dateCalendar[Calendar.YEAR]
                var mMonth = dateCalendar[Calendar.MONTH]
                var mDay = dateCalendar[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    this, { _, year, month, dayOfMonth ->
                        mYear = year
                        mMonth = month
                        mDay = dayOfMonth
                        dateString = "${year}-${DateTimeUtil.neatTime(month + 1)}-" +
                                DateTimeUtil.neatTime(dayOfMonth)

                        // TimePicker
                        val timeCalendar = Calendar.getInstance()
                        var mHour = timeCalendar[Calendar.HOUR_OF_DAY]
                        var mMinute = timeCalendar[Calendar.MINUTE]

                        val timePickerDialog = TimePickerDialog(
                            this, { _, hourOfDay, minute ->
                                mHour = hourOfDay
                                mMinute = minute
                                timeString =
                                    "${DateTimeUtil.neatTime(hourOfDay)}:${
                                        DateTimeUtil.neatTime(
                                            minute
                                        )
                                    }"
                                // join selected date and time to "yyyy-MM-dd HH:mm"
                                dateTime =
                                    DateTimeUtil.stringToTime("$dateString $timeString") ?: Date()
                                // date and time display format "MMM d, yyyy h:mm a"
                                binding.textViewDataTime.text =
                                    DateTimeUtil.conversionTime(dateTime)
                            }, mHour, mMinute, true
                        )
                        timePickerDialog.show()
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.show()
            }
        }
    }
}