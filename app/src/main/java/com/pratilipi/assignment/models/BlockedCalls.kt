package com.pratilipi.assignment.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pratilipi.assignment.room.Converter
import java.util.*

@Entity(tableName = "blocked_calls")
class BlockedCalls {
    @ColumnInfo(name = "time_stamp")
    @PrimaryKey
    @TypeConverters(Converter::class)
    var date = Date(System.currentTimeMillis())

    @ColumnInfo(name = "phone_number")
    var phoneNumber: String? = null
    override fun toString(): String {
        return "BlockedCalls{" +
                "date=" + date +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockedCalls) return false
        return date == other.date
    }

    override fun hashCode(): Int {
        return Objects.hash(date)
    }
}