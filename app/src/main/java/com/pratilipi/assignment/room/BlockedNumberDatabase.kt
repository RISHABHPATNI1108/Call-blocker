package com.pratilipi.assignment.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pratilipi.assignment.models.BlockedCalls
import com.pratilipi.assignment.models.BlockedNumber

@Database(entities = [BlockedNumber::class, BlockedCalls::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class BlockedNumberDatabase : RoomDatabase() {
    abstract fun blockedNumberDao(): BlockedNumberDao
    abstract fun blockedCallsDao(): BlockedCallsDao

    companion object {
        private const val DB_NAME = "blockednumbers.db"

        @Volatile
        private var INSTANCE: BlockedNumberDatabase? = null
        fun getInstance(context: Context?): BlockedNumberDatabase? {
            if (INSTANCE == null) {
                synchronized(BlockedNumberDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context!!,
                                BlockedNumberDatabase::class.java, DB_NAME)
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}