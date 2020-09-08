package com.pratilipi.assignment.models

data class ContactsModel(var displayName: String? = "",
                         var phoneNumber: ArrayList<String>? = ArrayList(),
                         var photoUri: String? = "")