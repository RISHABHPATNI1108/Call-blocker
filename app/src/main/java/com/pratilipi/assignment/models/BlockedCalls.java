package com.pratilipi.assignment.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.pratilipi.assignment.room.Converter;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "blocked_calls")
public class BlockedCalls {

  @ColumnInfo(name = "time_stamp")
  @PrimaryKey
  @NonNull
  @TypeConverters(Converter.class)
  private Date date = new Date(System.currentTimeMillis());

  @ColumnInfo(name = "phone_number")
  private String phoneNumber;

  @NonNull
  public Date getDate() {
    return date;
  }

  public void setDate(@NonNull Date date) {
    this.date = date;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @androidx.annotation.NonNull
  @Override
  public String toString() {
    return "BlockedCalls{" +
        "date=" + date +
        ", phoneNumber='" + phoneNumber + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockedCalls)) return false;
    BlockedCalls that = (BlockedCalls) o;
    return date.equals(that.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date);
  }
}
