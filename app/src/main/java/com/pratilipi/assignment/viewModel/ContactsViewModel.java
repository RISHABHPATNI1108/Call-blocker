package com.pratilipi.assignment.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pratilipi.assignment.models.ContactsModel;
import com.pratilipi.assignment.R;
import com.pratilipi.assignment.repo.BlockedContactsAndCallsRepo;
import com.pratilipi.assignment.repo.ContactsRepo;
import com.pratilipi.assignment.models.BlockedCalls;
import com.pratilipi.assignment.models.BlockedNumber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public class ContactsViewModel extends AndroidViewModel implements BlockedContactsAndCallsRepo.InsertAndDeleteListener {

  private static final String TAG = ContactsViewModel.class.getName();
  private ContactsRepo contactsRepo;
  private BlockedContactsAndCallsRepo repo;
  public MutableLiveData<String> phoneNumber = new MutableLiveData<>();
  public MutableLiveData<String> messageErrorData = new MutableLiveData<>();
  private MutableLiveData<ArrayList<ContactsModel>> contactsData = new MutableLiveData<>();

  public ContactsViewModel(@NonNull Application application) {
    super(application);
    repo = new BlockedContactsAndCallsRepo(application , this);
    contactsRepo = new ContactsRepo(application);
  }

  public void getContacts() {
    contactsData.postValue(contactsRepo.getContactList());
  }

  public void blockNumber() {
    String phoneNumber = this.phoneNumber.getValue();
    Log.d(TAG , "");
    if (contactsRepo.isValidPhoneNumber(phoneNumber)) {
      repo.insertBlockNumber(phoneNumber);
    } else {
      messageErrorData.postValue(getApplication().getString(R.string.invalid_phone_number));
    }
  }

  public Flowable<List<BlockedNumber>> getBlockedNumbers() {
    return repo.getAllBlockedContacts();
  }

  public Flowable<List<BlockedCalls>> getBlockedCall() {
    return repo.getAllBlockedCalls();
  }

  public void blockContact(ContactsModel contactsModel) {
    Log.d(TAG, "blockContact" + contactsModel.getPhoneNumber().size());
    if (contactsModel.getPhoneNumber().size() != 0) {
      repo.insertBlockNumber(contactsModel);
    } else {
      messageErrorData.postValue(getApplication().getString(R.string.no_phone_in_contact));
    }
  }

  public void unBlockContact(BlockedNumber blockedNumber) {
    repo.deleteBlockNumber(blockedNumber);
  }

  public LiveData<String> getMessageErrorData() {
    return messageErrorData;
  }

  public LiveData<ArrayList<ContactsModel>> getContactsData() {
    return contactsData;
  }


  @Override
  public void onInsertedSuccessfully() {
    messageErrorData.postValue(getApplication().getString(R.string.insert_successfully));
  }

  @Override
  public void onDeletedSuccessfully() {

  }

  @Override
  public void onErrorInInsertOrDelete(String error) {
    messageErrorData.postValue(error);
  }
}
