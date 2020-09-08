package com.pratilipi.assignment.repo

import android.content.Context
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Patterns
import com.pratilipi.assignment.models.ContactsModel
import java.util.*

class ContactsRepo(private val context: Context) {

    /**
     * Method to get List Of Contacts from Contacts content provider.
     *
     * @return Array list of contacts, size can be 0 or more.
     */
    val contactList: ArrayList<ContactsModel?>?
        get() {
            val contacts = ArrayList<ContactsModel?>()
            val cr = context.contentResolver
            val cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null)

            // Check if cursor has some values and parse from the cursor till it has next value
            if (cur?.count ?: 0 > 0) {
                while (cur!!.moveToNext()) {
                    val contact = ContactsModel()
                    val id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME))
                    val photoUri = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

                    contact.displayName = name
                    contact.photoUri = photoUri

                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                        val pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)

                        if (pCur != null) {

                            val phoneNumber = ArrayList<String>()
                            while (pCur.moveToNext()) {
                                val phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER))
                                if (!phoneNumber.contains(phoneNo)) {
                                    phoneNumber.add(phoneNo)
                                }
                            }

                            contact.phoneNumber = phoneNumber
                            contacts.add(contact)
                            pCur.close()
                        }
                    }
                }
            }

            cur?.close()
            return contacts
        }

    /**
     * Check if phone number is valid, if empty
     *
     * @return Boolean true iv phone number is valid,
     * if phone is empty or is not a phone number returns false.
     */
    fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (TextUtils.isEmpty(phoneNumber)) {
            false
        } else Patterns.PHONE.matcher(phoneNumber).matches()
    }
}