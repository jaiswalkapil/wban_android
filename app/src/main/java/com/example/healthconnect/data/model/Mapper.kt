package com.example.healthconnect.data.model

import com.example.healthconnect.data.local.entity.StepRecordEntity

fun StepRecordEntity.toStepData(): StepData {
    return StepData(
        count = this.count,
        startTime = this.startTime,
        endTime = this.endTime
    )
}