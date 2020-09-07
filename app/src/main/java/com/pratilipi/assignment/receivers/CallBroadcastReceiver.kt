package com.pratilipi.assignment.receivers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.internal.telephony.ITelephony
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.pratilipi.assignment.R
import com.pratilipi.assignment.models.BlockedCalls
import com.pratilipi.assignment.models.BlockedNumber
import com.pratilipi.assignment.room.BlockedNumberDatabase
import com.pratilipi.assignment.utils.Constants
import com.pratilipi.assignment.views.activities.BlockedCallsActivity
import io.reactivex.CompletableObserver
import io.reactivex.MaybeObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * BroadcastReceiver for catching calls
 */
class CallBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        // get telephony service
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephony.callState != TelephonyManager.CALL_STATE_RINGING) {
            return
        }

        // From https://developer.android.com/reference/android/telephony/TelephonyManager:
        // If the receiving app has Manifest.permission.READ_CALL_LOG and
        // Manifest.permission.READ_PHONE_STATE permission, it will receive the broadcast twice;
        // one with the EXTRA_INCOMING_NUMBER populated with the phone number, and another
        // with it blank. Due to the nature of broadcasts, you cannot assume the order in which
        // these broadcasts will arrive, however you are guaranteed to receive two in this case.
        // Apps which are interested in the EXTRA_INCOMING_NUMBER can ignore the broadcasts where
        // EXTRA_INCOMING_NUMBER is not present in the extras (e.g. where Intent#hasExtra(String)
        // returns false).
        if (!intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
            Log.d(TAG, "Event had no incoming_number metadata. Letting it keep ringing...")
            return
        }

        // get incoming call number.
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        Log.d(TAG, "Incoming number: $number")
        if (number != null) {
            Log.d(TAG, "Incoming Call with deleted code: " + deleteCountry(number))
            getContacts(context, deleteCountry(number))
        }
    }

    /*
      We only check for the number who is calling, and remove country code from it,
      The reason is some contacts are saved without country code in contacts list.
   */
    fun deleteCountry(phone: String): String {
        val phoneInstance = PhoneNumberUtil.getInstance()
        try {
            val phoneNumber = phoneInstance.parse(phone, "IN")
            return phoneInstance.getNationalSignificantNumber(phoneNumber)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return phone
    }

    /*
      We create an aidl interface for inter process communication to call end call method in telephony
   */
    private fun breakCallNougatAndLower(context: Context) {
        Log.d(TAG, "Trying to break call for Nougat and lower with TelephonyManager.")
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val c = Class.forName(telephony.javaClass.name)
            val m = c.getDeclaredMethod("getITelephony")
            m.isAccessible = true
            val telephonyService = m.invoke(telephony) as ITelephony?
            telephonyService?.endCall()
            Log.d(TAG, "Invoked 'endCall' on TelephonyService.")
        } catch (e: Exception) {
            Log.e(TAG, "Could not end call. Check stdout for more info.")
            e.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    /*
      In Pie and above we do not need to create aidl interface to disconnect the call.
   */ private fun breakCallPieAndHigher(context: Context) {
        Log.d(TAG, "Trying to break call for Pie and higher with TelecomManager.")
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        try {
            telecomManager.javaClass.getMethod("endCall").invoke(telecomManager)
            Log.d(TAG, "Invoked 'endCall' on TelecomManager.")
        } catch (e: Exception) {
            Log.e(TAG, "Could not end call. Check stdout for more info.")
            e.printStackTrace()
        }
    }

    // Ends phone call
    private fun breakCall(context: Context) {
        if (Build.VERSION.SDK_INT >= Constants.PIE_API_VERSION) {
            breakCallPieAndHigher(context)
        } else {
            breakCallNougatAndLower(context)
        }
    }

    // Finds contacts by number, and if number exists, disconnects the call
    @SuppressLint("CheckResult")
    private fun getContacts(context: Context, number: String) {
        val db: BlockedNumberDatabase? = BlockedNumberDatabase.getInstance(context)
        if (db != null) {
            Observable.just(db).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe { database: BlockedNumberDatabase ->
                database.blockedNumberDao().getBlockNumberFromNumber(number).subscribe(object : MaybeObserver<BlockedNumber?> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(blockedNumber: BlockedNumber) {
                        Log.d(TAG, """blockNumber $blockedNumber${blockedNumber.phoneNumber.contains(number)}""".trimIndent())
                        if (blockedNumber.phoneNumber.contains(number)) {
                            breakCallAndNotify(context, number)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, " error " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, " completed ")
                    }

                })
            }
        }
    }

    // Breaks the call and notifies the user
    private fun breakCallAndNotify(context: Context, number: String) {
        // end phone call
        breakCall(context)
        // Notification
        showNotification(context, number)
        addBlockedCallToTable(context, number)
    }

    @SuppressLint("CheckResult")
    /*
   *  We add the blocked call to blocked calls table, for any future reference.
   */ private fun addBlockedCallToTable(context: Context, number: String) {
        val db: BlockedNumberDatabase? = BlockedNumberDatabase.getInstance(context)
        if (db != null) {
            val blockedCalls = BlockedCalls()
            blockedCalls.date = Date(System.currentTimeMillis())
            blockedCalls.phoneNumber = number
            Observable.just(db).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe { database: BlockedNumberDatabase ->
                database.blockedCallsDao().insert(blockedCalls).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        Log.d(TAG, " error in saving blocked call " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, " completed adding to blocked call ")
                    }
                })
            }
        }
    }

    /**
     * Shows a notification, that we have blocked a call with this number
     * @param context context is needed to create instance of notification manager
     * @param number the number which we blocked
     */
    private fun showNotification(context: Context, number: String) {
        val contentIntent = PendingIntent.getActivity(context, 0,
                Intent(context, BlockedCallsActivity::class.java), 0)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("block", "block", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }
        val mBuilder = NotificationCompat.Builder(context, "block")
                .setSmallIcon(R.drawable.ic_person)
                .setContentTitle("A call was blocked!")
                .setContentText("We have blocked a call from number $number")
        mBuilder.setContentIntent(contentIntent)
        mBuilder.setDefaults(Notification.DEFAULT_SOUND)
        mBuilder.setAutoCancel(true)
        mNotificationManager?.notify(1, mBuilder.build())
    }

    companion object {
        private val TAG = CallBroadcastReceiver::class.java.name
    }
}