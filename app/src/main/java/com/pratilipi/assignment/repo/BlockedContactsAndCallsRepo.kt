package com.pratilipi.assignment.repo

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.pratilipi.assignment.models.BlockedCalls
import com.pratilipi.assignment.models.BlockedNumber
import com.pratilipi.assignment.models.ContactsModel
import com.pratilipi.assignment.room.BlockedNumberDatabase
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BlockedContactsAndCallsRepo(context: Context?, private val listener: InsertAndDeleteListener) {
    private val database: BlockedNumberDatabase? = BlockedNumberDatabase.getInstance(context)
    val allBlockedContacts: Flowable<List<BlockedNumber?>?>?
        get() = database!!.blockedNumberDao().all
    val allBlockedCalls: Flowable<List<BlockedCalls?>?>?
        get() = database!!.blockedCallsDao().all

    @SuppressLint("CheckResult")
    fun insertBlockNumber(phoneNumber: String?) {
        val blockedNumber = BlockedNumber(phoneNumber!!, "")
        Observable.just(database).observeOn(Schedulers.io()).subscribe { database: BlockedNumberDatabase? ->
            database!!.blockedNumberDao().insert(blockedNumber).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.d(TAG, "onComplete ")
                    listener.onInsertedSuccessfully()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    listener.onErrorInInsertOrDelete(e.message)
                }
            })
        }
    }

    @SuppressLint("CheckResult")
    fun insertBlockNumber(contactsModel: ContactsModel) {
        for (phoneNumber in contactsModel.phoneNumber!!) {
            val blockedNumber = BlockedNumber(phoneNumber, contactsModel.displayName)
            Log.d(TAG, "insertBlockedNumber$blockedNumber")
            Observable.just(database).observeOn(Schedulers.io()).subscribe { database: BlockedNumberDatabase? ->
                database!!.blockedNumberDao().insert(blockedNumber).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        Log.d(TAG, "onComplete ")
                        listener.onInsertedSuccessfully()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        listener.onErrorInInsertOrDelete(e.message)
                    }
                })
            }
        }
    }

    @SuppressLint("CheckResult")
    fun deleteBlockNumber(blockedNumber: BlockedNumber?) {
        Observable.just(database).observeOn(Schedulers.io()).subscribe { database: BlockedNumberDatabase? ->
            database!!.blockedNumberDao().delete(blockedNumber).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.d(TAG, "onComplete ")
                    listener.onDeletedSuccessfully()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    listener.onErrorInInsertOrDelete(e.message)
                }
            })
        }
    }

    interface InsertAndDeleteListener {
        fun onInsertedSuccessfully()
        fun onDeletedSuccessfully()
        fun onErrorInInsertOrDelete(error: String?)
    }

    companion object {
        private val TAG = BlockedContactsAndCallsRepo::class.java.name
    }

}