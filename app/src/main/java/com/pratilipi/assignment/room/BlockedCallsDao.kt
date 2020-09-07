package com.pratilipi.assignment.room

import androidx.room.*
import com.pratilipi.assignment.models.BlockedCalls
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface BlockedCallsDao {
    @get:Query("SELECT * FROM blocked_calls ORDER BY time_stamp DESC")
    val all: Flowable<List<BlockedCalls?>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(number: BlockedCalls?): Completable

    @Delete
    fun delete(number: BlockedCalls?): Completable?
}