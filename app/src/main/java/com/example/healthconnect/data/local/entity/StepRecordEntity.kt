package com.example.healthconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_records")
data class StepRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val count: Int,
    val startTime: String,
    val endTime: String
)