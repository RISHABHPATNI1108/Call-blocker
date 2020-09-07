package com.pratilipi.assignment.room

import androidx.room.*
import com.pratilipi.assignment.models.BlockedNumber
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface BlockedNumberDao {
    @get:Query("SELECT * FROM blockednumbers")
    val all: Flowable<List<BlockedNumber?>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(number: BlockedNumber?): Completable

    @Delete
    fun delete(number: BlockedNumber?): Completable

    @Query("SELECT * FROM blockednumbers WHERE phone_number LIKE :phoneNumber")
    fun getBlockNumberFromNumber(phoneNumber: String?): Maybe<BlockedNumber?>
}