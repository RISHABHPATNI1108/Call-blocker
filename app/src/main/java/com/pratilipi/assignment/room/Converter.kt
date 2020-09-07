package com.pratilipi.assignment.room

import android.util.Log
import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Converter {
    private val TAG = Converter::class.java.name

    // Set timezone value as GMT 존맛탱... to make time as reasonable
    var df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.getDefault())
    @JvmStatic
    @TypeConverter
    fun timeToDate(value: String?): Date? {
        if (value != null) {
            try {
                return df.parse(value)
            } catch (e: ParseException) {
                Log.e(TAG, "" + e.message)
            }
        }
        return null
    }

    @JvmStatic
    @TypeConverter
    fun dateToTime(value: Date?): String? {
        return if (value != null) {
            df.format(value)
        } else {
            null
        }
    }

    init {
        df.timeZone = TimeZone.getDefault()
    }
}