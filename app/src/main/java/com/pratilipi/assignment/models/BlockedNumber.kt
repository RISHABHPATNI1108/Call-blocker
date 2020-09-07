package com.pratilipi.assignment.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "blockednumbers")
class BlockedNumber(phoneNumber: String, name: String?) {
    @ColumnInfo(name = "phone_number")
    @PrimaryKey
    var phoneNumber = ""

    @ColumnInfo(name = "Name")
    var name: String?
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockedNumber) return false
        return phoneNumber == other.phoneNumber
    }

    override fun hashCode(): Int {
        return Objects.hash(phoneNumber)
    }

    override fun toString(): String {
        return "BlockedNumber{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                '}'
    }

    init {
        this.phoneNumber = phoneNumber
        this.name = name
    }
}