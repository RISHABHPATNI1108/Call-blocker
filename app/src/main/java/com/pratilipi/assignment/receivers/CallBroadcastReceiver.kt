package com.pratilipi.assignment.receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.internal.telephony.ITelephony;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.pratilipi.assignment.R;
import com.pratilipi.assignment.models.BlockedCalls;
import com.pratilipi.assignment.models.BlockedNumber;
import com.pratilipi.assignment.room.BlockedNumberDatabase;
import com.pratilipi.assignment.utils.Constants;
import com.pratilipi.assignment.views.activities.BlockedCallsActivity;

import java.lang.reflect.Method;
import java.util.Date;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * BroadcastReceiver for catching calls
 */
@SuppressWarnings({"rawtypes", "ResultOfMethodCallIgnored"})
public class CallBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = CallBroadcastReceiver.class.getName();

  @SuppressLint("UnsafeProtectedBroadcastReceiver")
  @Override
  public void onReceive(final Context context, Intent intent) {

    // get telephony service
    TelephonyManager telephony = (TelephonyManager)
        context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephony == null) {
      return;
    }
    if (telephony.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
      return;
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
      Log.d(TAG, "Event had no incoming_number metadata. Letting it keep ringing...");
      return;
    }

    // get incoming call number.
    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
    Log.d(TAG, "Incoming number: " + number);
    Log.d(TAG, "Incoming Call with deleted code: " + deleteCountry(number));

    getContacts(context, deleteCountry(number));

  }

  /*
      We only check for the number who is calling, and remove country code from it,
      The reason is some contacts are saved without country code in contacts list.
   */
  String deleteCountry(String phone) {
    PhoneNumberUtil phoneInstance = PhoneNumberUtil.getInstance();
    try {
      Phonenumber.PhoneNumber phoneNumber = phoneInstance.parse(phone, "IN");
      return phoneInstance.getNationalSignificantNumber(phoneNumber);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return phone;
  }

  /*
      We create an aidl interface for inter process communication to call end call method in telephony
   */
  @SuppressWarnings("unchecked")
  private void breakCallNougatAndLower(Context context) {
    Log.d(TAG, "Trying to break call for Nougat and lower with TelephonyManager.");
    TelephonyManager telephony = (TelephonyManager)
        context.getSystemService(Context.TELEPHONY_SERVICE);
    try {
      if (telephony != null) {
        Class c = Class.forName(telephony.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        ITelephony telephonyService = (ITelephony) m.invoke(telephony);
        if (telephonyService != null) {
          telephonyService.endCall();
        }
        Log.d(TAG, "Invoked 'endCall' on TelephonyService.");
      }
    } catch (Exception e) {
      Log.e(TAG, "Could not end call. Check stdout for more info.");
      e.printStackTrace();
    }
  }

  @SuppressLint("NewApi")
  /*
      In Pie and above we do not need to create aidl interface to disconnect the call.
   */
  private void breakCallPieAndHigher(Context context) {
    Log.d(TAG, "Trying to break call for Pie and higher with TelecomManager.");
    TelecomManager telecomManager = (TelecomManager)
        context.getSystemService(Context.TELECOM_SERVICE);
    try {
      if (telecomManager != null) {
        telecomManager.getClass().getMethod("endCall").invoke(telecomManager);
        Log.d(TAG, "Invoked 'endCall' on TelecomManager.");
      }
    } catch (Exception e) {
      Log.e(TAG, "Could not end call. Check stdout for more info.");
      e.printStackTrace();
    }
  }

  // Ends phone call
  private void breakCall(Context context) {
    if (Build.VERSION.SDK_INT >= Constants.PIE_API_VERSION) {
      breakCallPieAndHigher(context);
    } else {
      breakCallNougatAndLower(context);
    }
  }

  // Finds contacts by number, and if number exists, disconnects the call
  @SuppressLint("CheckResult")
  private void getContacts(Context context, String number) {
    BlockedNumberDatabase db = BlockedNumberDatabase.getInstance(context);
    if (db != null) {
      Observable.just(db).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(database -> database.blockedNumberDao().getBlockNumberFromNumber(number).subscribe(new MaybeObserver<BlockedNumber>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(BlockedNumber blockedNumber) {
          if (blockedNumber != null) {
            Log.d(TAG, "blockNumber " + blockedNumber + "\n" + blockedNumber.getPhoneNumber().contains(number));
            if (blockedNumber.getPhoneNumber().contains(number)) {
              breakCallAndNotify(context, number);
            }
          }
        }

        @Override
        public void onError(Throwable e) {
          Log.d(TAG, " error " + e.getMessage());
        }

        @Override
        public void onComplete() {
          Log.d(TAG, " completed ");
        }
      }));
    }
  }

  // Breaks the call and notifies the user
  private void breakCallAndNotify(Context context, String number) {
    // end phone call
    breakCall(context);
    // Notification
    showNotification(context, number);
    addBlockedCallToTable(context, number);

  }

  @SuppressLint("CheckResult")
  /*
   *  We add the blocked call to blocked calls table, for any future reference.
   */
  private void addBlockedCallToTable(Context context, String number) {
    BlockedNumberDatabase db = BlockedNumberDatabase.getInstance(context);
    if (db != null) {
      BlockedCalls blockedCalls = new BlockedCalls();
      blockedCalls.setDate(new Date(System.currentTimeMillis()));
      blockedCalls.setPhoneNumber(number);
      Observable.just(db).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(database -> database.blockedCallsDao().insert(blockedCalls).subscribe(new CompletableObserver() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onError(Throwable e) {
          Log.d(TAG, " error in saving blocked call " + e.getMessage());
        }

        @Override
        public void onComplete() {
          Log.d(TAG, " completed adding to blocked call ");
        }
      }));
    }
  }

  /**
   *  Shows a notification, that we have blocked a call with this number
   * @param context context is needed to create instance of notification manager
   * @param number the number which we blocked
   */
  private void showNotification(Context context, String number) {
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
        new Intent(context, BlockedCallsActivity.class), 0);

    NotificationManager mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel("block", "block", NotificationManager.IMPORTANCE_DEFAULT);
      notificationChannel.enableLights(true);
      if (mNotificationManager != null) {
        mNotificationManager.createNotificationChannel(notificationChannel);
      }
    }

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(context, "block")
            .setSmallIcon(R.drawable.ic_person)
            .setContentTitle("A call was blocked!")
            .setContentText("We have blocked a call from number " + number);
    mBuilder.setContentIntent(contentIntent);
    mBuilder.setDefaults(Notification.DEFAULT_SOUND);
    mBuilder.setAutoCancel(true);
    if (mNotificationManager != null) {
      mNotificationManager.notify(1, mBuilder.build());
    }
  }

}