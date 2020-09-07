package com.pratilipi.assignment.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "blockednumbers")
public class BlockedNumber {

  @ColumnInfo(name = "phone_number")
  @PrimaryKey
  @NonNull
  private String phoneNumber = "";

  @ColumnInfo(name = "Name")
  private String name;

  @NonNull
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(@NonNull String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BlockedNumber(@NonNull String phoneNumber, String name) {
    this.phoneNumber = phoneNumber;
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockedNumber)) return false;
    BlockedNumber that = (BlockedNumber) o;
    return phoneNumber.equals(that.phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(phoneNumber);
  }

  @NonNull
  @Override
  public String toString() {
    return "BlockedNumber{" +
        "phoneNumber='" + phoneNumber + '\'' +
        ", name='" + name + '\'' +
        '}';
  }

}