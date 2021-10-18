package com.elsawu.medicationevent.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elsawu.medicationevent.R
import com.elsawu.medicationevent.beans.MedicationEvent
import com.elsawu.medicationevent.databinding.ActivityMainBinding
import com.elsawu.medicationevent.httputils.HTTPServiceBuilder
import com.elsawu.medicationevent.httputils.service.MedicationEventService
import com.elsawu.medicationevent.utils.DateTimeUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class EventListActivity : AppCompatActivity() {

    // view binding
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var addEventActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var medicationList: ArrayList<MedicationEvent.User.Medication>
    private var medicationEventList = mutableListOf<MedicationEvent.Event>()
    // max id is for adding new event
    private var maxEventId = -1

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.my_event_title)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.recyclerViewEvent.layoutManager =
            LinearLayoutManager(applicationContext)

        // receive message from AddEventActivity
        addEventActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val newEventItem =
                    result.data?.getSerializableExtra("newEvent") as MedicationEvent.Event
                // add new added event to the event list
                medicationEventList.add(newEventItem)
                medicationEventList.sortByDescending { event -> event.datetime }
                maxEventId = newEventItem.id
                // refresh event list RecyclerView
                (activityMainBinding.recyclerViewEvent.adapter as RecyclerView.Adapter).notifyDataSetChanged()
            }
        }

        // request event list
        medicationEventRequest()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val mMenu: Menu = menu
        val item: MenuItem = mMenu.findItem(R.id.action_custom_button)
        item.setOnMenuItemClickListener { // go to AddEventActivity
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putExtra("maxId", maxEventId)
            intent.putExtra("medicationList", medicationList)
            addEventActivityResultLauncher.launch(intent)
            false
        }
        return true
    }

    // Send HTTP request to get the event list
    private fun medicationEventRequest() {
        val request = HTTPServiceBuilder.buildService(MedicationEventService::class.java)
        request.getMedicationEvent().enqueue(object : Callback<MedicationEvent> {
            override fun onResponse(
                call: Call<MedicationEvent>,
                response: Response<MedicationEvent>
            ) {
                if (response.isSuccessful) {
                    val medicationEvent = (response.body() ?: run {
                        return@run
                    }) as MedicationEvent
                    medicationEventList =
                        medicationEvent.events as MutableList<MedicationEvent.Event>
                    maxEventId = medicationEventList.maxOf { it.id }
                    medicationEventList.sortByDescending { it.datetime }
                    activityMainBinding.recyclerViewEvent.adapter =
                        EventRecyclerViewAdapter(medicationEventList)
                    medicationList = ArrayList(medicationEvent.user.medications)
                }
            }

            override fun onFailure(call: Call<MedicationEvent>, t: Throwable) {
                Toast.makeText(
                    this@EventListActivity,
                    "Fetch Medication Event List Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Custom RecyclerViewAdapter for the event list RecyclerView
    private class EventRecyclerViewAdapter(private val mList: List<MedicationEvent.Event>) :
        RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>() {

        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
            return ViewHolder(view)
        }

        // binds the list items to a view
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val eventItem = mList[position]
            // set the text to the textview from itemHolder class
            holder.eventIdTextView.text = eventItem.id.toString()
            holder.medicationTypeTextView.text = eventItem.medication
            holder.medicationNameTextView.text = eventItem.medicationtype
            holder.dateTimeTextView.text = DateTimeUtil.conversionTime(eventItem.datetime)

        }

        // return the number of the items in the list
        override fun getItemCount(): Int {
            return mList.size
        }

        // Holds the views for adding it to image and text
        class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val eventIdTextView: TextView = itemView.findViewById(R.id.event_id)
            val medicationTypeTextView: TextView = itemView.findViewById(R.id.medication_name)
            val medicationNameTextView: TextView = itemView.findViewById(R.id.medication_type)
            val dateTimeTextView: TextView = itemView.findViewById(R.id.date_time)
        }
    }
}