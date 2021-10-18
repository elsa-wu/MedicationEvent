package com.elsawu.medicationevent.httputils.service

import retrofit2.Call
import retrofit2.http.GET
import com.elsawu.medicationevent.beans.MedicationEvent

interface MedicationEventService {
    @GET("propeller_mobile_assessment_data.json")
    fun getMedicationEvent(): Call<MedicationEvent>
}