package com.elsawu.medicationevent.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun conversionTime(dateTime: Date): String {
        val dateFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
        return dateFormat.format(dateTime) ?: ""
    }

    fun stringToTime(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        return dateFormat.parse(dateString)
    }

    fun neatTime(time: Int?): String {
        return if (time!! < 10) {
            "0$time"
        } else {
            "$time"
        }
    }
}