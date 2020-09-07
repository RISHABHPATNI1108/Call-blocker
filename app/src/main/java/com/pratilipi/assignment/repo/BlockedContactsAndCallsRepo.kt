package com.pratilipi.assignment.repo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.pratilipi.assignment.models.ContactsModel;
import com.pratilipi.assignment.models.BlockedCalls;
import com.pratilipi.assignment.models.BlockedNumber;
import com.pratilipi.assignment.room.BlockedNumberDatabase;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BlockedContactsAndCallsRepo {

  private static final String TAG = BlockedContactsAndCallsRepo.class.getName();
  private InsertAndDeleteListener listener;
  private BlockedNumberDatabase database;

  public BlockedContactsAndCallsRepo(Context context, InsertAndDeleteListener listener) {
    this.listener = listener;
    database = BlockedNumberDatabase.getInstance(context);
  }

  public Flowable<List<BlockedNumber>> getAllBlockedContacts() {
    return database.blockedNumberDao().getAll();
  }

  public Flowable<List<BlockedCalls>> getAllBlockedCalls() {
    return database.blockedCallsDao().getAll();
  }

  @SuppressLint("CheckResult")
  public void insertBlockNumber(String phoneNumber) {
    BlockedNumber blockedNumber = new BlockedNumber(phoneNumber, "");
    Observable.just(database).observeOn(Schedulers.io()).subscribe(database -> database.blockedNumberDao().insert(blockedNumber).subscribe(new CompletableObserver() {
      @Override
      public void onSubscribe(Disposable d) {

      }

      @Override
      public void onComplete() {
        Log.d(TAG, "onComplete ");
        listener.onInsertedSuccessfully();
      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        listener.onErrorInInsertOrDelete(e.getMessage());
      }
    }));
  }

  @SuppressLint("CheckResult")
  public void insertBlockNumber(ContactsModel contactsModel) {
    for (String phoneNumber : contactsModel.getPhoneNumber()) {
      final BlockedNumber blockedNumber = new BlockedNumber(phoneNumber, contactsModel.getDisplayName());
      Log.d(TAG, "insertBlockedNumber" + blockedNumber);
      Observable.just(database).observeOn(Schedulers.io()).subscribe(database -> database.blockedNumberDao().insert(blockedNumber).subscribe(new CompletableObserver() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onComplete() {
          Log.d(TAG, "onComplete ");
          listener.onInsertedSuccessfully();
        }

        @Override
        public void onError(Throwable e) {
          e.printStackTrace();
          listener.onErrorInInsertOrDelete(e.getMessage());
        }
      }));
    }
  }

  @SuppressLint("CheckResult")
  public void deleteBlockNumber(BlockedNumber blockedNumber) {
    Observable.just(database).observeOn(Schedulers.io()).subscribe(database -> database.blockedNumberDao().delete(blockedNumber).subscribe(new CompletableObserver() {
      @Override
      public void onSubscribe(Disposable d) {

      }

      @Override
      public void onComplete() {
        Log.d(TAG, "onComplete ");
        listener.onDeletedSuccessfully();
      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        listener.onErrorInInsertOrDelete(e.getMessage());
      }
    }));
  }

  public interface InsertAndDeleteListener {

    void onInsertedSuccessfully();

    void onDeletedSuccessfully();

    void onErrorInInsertOrDelete(String error);

  }

}
