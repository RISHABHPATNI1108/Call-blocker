package com.pratilipi.assignment.models

import java.util.*

class ContactsModel {
    var displayName: String? = null
    var phoneNumber: ArrayList<String>? = null
    var photoUri: String? = null
    override fun toString(): String {
        return "model{" +
                "displayName='" + displayName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", photoUri='" + photoUri + '\'' +
                '}'
    }
}