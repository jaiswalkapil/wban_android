package com.example.healthconnect.data.repository


import com.example.healthconnect.data.local.db.StepRecordDao
import com.example.healthconnect.data.local.entity.StepRecordEntity
import com.example.healthconnect.data.model.StepData
import com.example.healthconnect.data.model.toStepData


class LocalRepository(private val dao: StepRecordDao) {

    suspend fun insertStep(stepData: StepData) {
        val entity = StepRecordEntity(
            count = stepData.count,
            startTime = stepData.startTime,
            endTime = stepData.endTime
        )
        dao.insertStepRecord(entity)
    }

    suspend fun getAllSteps(): List<StepData> {
        return dao.getAllStepRecords().map { it.toStepData() }
    }

    suspend fun deleteAllSteps() {
        dao.deleteAllSteps()
    }
}