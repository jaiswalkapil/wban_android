package com.example.healthconnect.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthconnect.data.local.entity.StepRecordEntity

@Dao
interface StepRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepRecord(step: StepRecordEntity)

    @Query("SELECT * FROM step_records")
    suspend fun getAllStepRecords(): List<StepRecordEntity>

    @Query("DELETE FROM step_records")
    suspend fun deleteAllSteps()
}