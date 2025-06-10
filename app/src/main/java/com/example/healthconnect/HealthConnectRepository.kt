package com.example.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset

class HealthConnectRepository(context: Context) {

    private val healthConnectClient = HealthConnectClient.getOrCreate(context)

    suspend fun insertDummyData(): Result<Unit> {
        val now = Instant.now()
        val start = now.minusSeconds(3600)
        val end = now

        val stepRecord = StepsRecord(
            count = 300,
            startTime = start,
            endTime = now,
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC,
            metadata = Metadata.manualEntry()
        )

        val heartRateRecord = HeartRateRecord(
            startTime = start,
            endTime = now,
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC,
            samples = listOf(
                HeartRateRecord.Sample(start.plusSeconds(600), 80),
                HeartRateRecord.Sample(start.plusSeconds(1800), 85),
                HeartRateRecord.Sample(end, 78)
            ),
            metadata = Metadata.manualEntry()
        )

        return try {
            healthConnectClient.insertRecords(listOf(stepRecord, heartRateRecord))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}