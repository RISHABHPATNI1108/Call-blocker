package com.pratilipi.assignment.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratilipi.assignment.models.BlockedNumber;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface BlockedNumberDao {

  @Query("SELECT * FROM blockednumbers")
  Flowable<List<BlockedNumber>> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  Completable insert(BlockedNumber number);

  @Delete
  Completable delete(BlockedNumber number);

  @Query("SELECT * FROM blockednumbers WHERE phone_number LIKE :phoneNumber")
  Maybe<BlockedNumber> getBlockNumberFromNumber(String phoneNumber);

}