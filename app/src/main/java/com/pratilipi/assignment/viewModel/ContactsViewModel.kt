package com.pratilipi.assignment.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pratilipi.assignment.R
import com.pratilipi.assignment.models.BlockedCalls
import com.pratilipi.assignment.models.BlockedNumber
import com.pratilipi.assignment.models.ContactsModel
import com.pratilipi.assignment.repo.BlockedContactsAndCallsRepo
import com.pratilipi.assignment.repo.BlockedContactsAndCallsRepo.InsertAndDeleteListener
import com.pratilipi.assignment.repo.ContactsRepo
import io.reactivex.Flowable
import java.util.*

class ContactsViewModel(application: Application) : AndroidViewModel(application), InsertAndDeleteListener {

    companion object {
        private val TAG = ContactsViewModel::class.java.name
    }

    private val contactsRepo: ContactsRepo = ContactsRepo(application)
    private val repo: BlockedContactsAndCallsRepo = BlockedContactsAndCallsRepo(application, this)

    var phoneNumber = MutableLiveData<String>()

    private var messageErrorData = MutableLiveData<String?>()

    val contactsData = MutableLiveData<ArrayList<ContactsModel?>?>()
    val contacts: Unit
        get() {
            contactsData.postValue(contactsRepo.contactList)
        }

    fun blockNumber() {
        val phoneNumber = phoneNumber.value
        if (contactsRepo.isValidPhoneNumber(phoneNumber!!)) {
            repo.insertBlockNumber(phoneNumber)
        } else {
            messageErrorData.postValue(getApplication<Application>().getString(R.string.invalid_phone_number))
        }
    }

    val blockedNumbers: Flowable<List<BlockedNumber?>?>?
        get() = repo.allBlockedContacts
    val blockedCall: Flowable<List<BlockedCalls?>?>?
        get() = repo.allBlockedCalls

    fun blockContact(contactsModel: ContactsModel?) {
        if (contactsModel?.phoneNumber?.size != 0) {
            repo.insertBlockNumber(contactsModel)
        } else {
            messageErrorData.postValue(getApplication<Application>().getString(R.string.no_phone_in_contact))
        }
    }

    fun unBlockContact(blockedNumber: BlockedNumber?) {
        repo.deleteBlockNumber(blockedNumber)
    }

    fun getMessageErrorData(): LiveData<String?> {
        return messageErrorData
    }

    override fun onInsertedSuccessfully() {
        messageErrorData.postValue(getApplication<Application>().getString(R.string.insert_successfully))
    }

    override fun onDeletedSuccessfully() {}

    override fun onErrorInInsertOrDelete(error: String?) {
        messageErrorData.postValue(error)
    }

}