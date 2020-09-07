package com.pratilipi.assignment.models;

import java.util.ArrayList;

public class ContactsModel {

  private String displayName;

  private ArrayList<String> phoneNumber;

  private String photoUri;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ArrayList<String> getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(ArrayList<String> phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhotoUri() {
    return photoUri;
  }

  public void setPhotoUri(String photoUri) {
    this.photoUri = photoUri;
  }

  @androidx.annotation.NonNull
  @Override
  public String toString() {
    return "model{" +
        "displayName='" + displayName + '\'' +
        ", phoneNumber='" + phoneNumber + '\'' +
        ", photoUri='" + photoUri + '\'' +
        '}';
  }
}
