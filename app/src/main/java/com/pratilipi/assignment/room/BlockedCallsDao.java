package com.pratilipi.assignment.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratilipi.assignment.models.BlockedCalls;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface BlockedCallsDao {

  @Query("SELECT * FROM blocked_calls ORDER BY time_stamp DESC")
  Flowable<List<BlockedCalls>> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  Completable insert(BlockedCalls number);

  @Delete
  Completable delete(BlockedCalls number);

}
