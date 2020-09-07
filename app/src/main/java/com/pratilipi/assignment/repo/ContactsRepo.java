package com.pratilipi.assignment.repo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;

import com.pratilipi.assignment.models.ContactsModel;

import java.util.ArrayList;

public class ContactsRepo {

  private Context context;

  public ContactsRepo(Context context) {
    this.context = context;
  }

  /**
   * Method to get List Of Contacts from Contacts content provider.
   *
   * @return Array list of contacts, size can be 0 or more.
   **/
  public ArrayList<ContactsModel> getContactList() {
    ArrayList<ContactsModel> contacts = new ArrayList<>();
    ContentResolver cr = context.getContentResolver();
    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
        null, null, null, null);

    // Check if cursor has some values and parse from the cursor till it has next value
    if ((cur != null ? cur.getCount() : 0) > 0) {
      while (cur.moveToNext()) {
        ContactsModel contact = new ContactsModel();
        String id = cur.getString(
            cur.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cur.getString(cur.getColumnIndex(
            ContactsContract.Contacts.DISPLAY_NAME));
        String photoUri = cur.getString(cur.getColumnIndex(
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));

        contact.setDisplayName(name);
        contact.setPhotoUri(photoUri);
        if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
          Cursor pCur = cr.query(
              ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
              null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
              new String[]{id}, null);
          if (pCur != null) {
            ArrayList<String> phoneNumber = new ArrayList<>();
            while (pCur.moveToNext()) {
              String phoneNo = pCur.getString(pCur.getColumnIndex(
                  ContactsContract.CommonDataKinds.Phone.NUMBER));
              if (!phoneNumber.contains(phoneNo)) {
                phoneNumber.add(phoneNo);
              }
            }
            contact.setPhoneNumber(phoneNumber);
            contacts.add(contact);
            pCur.close();
          }
        }
      }
    }
    if (cur != null) {
      cur.close();
    }
    return contacts;
  }

  /**
   * Check if phone number is valid, if empty
   *
   * @return Boolean true iv phone number is valid,
   * if phone is empty or is not a phone number returns false.
   **/
  public boolean isValidPhoneNumber(String phoneNumber) {
    if (TextUtils.isEmpty(phoneNumber)) {
      return false;
    } else return Patterns.PHONE.matcher(phoneNumber).matches();
  }

}
