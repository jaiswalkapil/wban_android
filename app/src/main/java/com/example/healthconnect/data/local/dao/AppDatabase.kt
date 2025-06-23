package com.example.healthconnect.data.local.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healthconnect.data.local.db.StepRecordDao
import com.example.healthconnect.data.local.entity.StepRecordEntity

@Database(entities = [StepRecordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepRecordDao(): StepRecordDao
}