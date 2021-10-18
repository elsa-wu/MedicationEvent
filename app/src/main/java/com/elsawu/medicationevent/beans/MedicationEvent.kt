package com.elsawu.medicationevent.beans

import java.util.*
import java.io.Serializable

data class MedicationEvent(
    val user: User,
    val events: List<Event>
) {
    data class User(
        val name: String,
        val address1: String,
        val address2: String,
        val uid: String,
        val sex: String,
        val dob: String,
        val disease: String,
        val medications: List<Medication>
    ) {
        data class Medication(
            val name: String,
            val medicationtype: String
        ) : Serializable
    }

    data class Event(
        val uid: String?,
        val datetime: Date,
        val medication: String,
        val medicationtype: String,
        val id: Int
    ) : Serializable
}


