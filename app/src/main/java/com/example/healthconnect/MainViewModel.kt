package com.example.healthconnect

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.permission.HealthPermission.Companion.PERMISSION_READ_HEALTH_DATA_HISTORY
import androidx.health.connect.client.permission.HealthPermission.Companion.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.healthconnect.data.local.dao.AppDatabase
import com.example.healthconnect.data.model.StepData
import com.example.healthconnect.data.repository.LocalRepository
import com.example.healthconnect.data.repository.StepRepository
import com.example.healthconnect.helper.HealthConnectManager
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.random.Random

class MainViewModel(
    private val healthConnectManager: HealthConnectManager,
    private val localRepository: LocalRepository,
    private val remoteRepository: StepRepository
) : ViewModel() {

    val permissions = setOf(
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
    )

    val backgroundReadPermissions = setOf(PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND)
    val historyReadPermissions = setOf(PERMISSION_READ_HEALTH_DATA_HISTORY)

    var permissionsGranted = mutableStateOf(false)
        private set

    var backgroundReadAvailable = mutableStateOf(false)
        private set

    var backgroundReadGranted = mutableStateOf(false)
        private set

    var historyReadAvailable = mutableStateOf(false)
        private set

    var historyReadGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<ExerciseSessionRecord>> = mutableStateOf(listOf())
        private set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    // INITIAL LOAD
    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                readExerciseSessions()
            }
        }
    }

    // INSERT DUMMY DATA KE HealthConnect + Local SQLite
    fun insertDummyExerciseSession() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val latestStart = ZonedDateTime.now().minusMinutes(30)
                val offset = Random.nextDouble()

                val startTime = startOfDay.plusSeconds(
                    (Duration.between(startOfDay, latestStart).seconds * offset).toLong()
                )
                val endTime = startTime.plusMinutes(30)

                val count = (50..150).random()

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                val stepData = StepData(
                    count = count,
                    startTime = startTime.format(formatter),
                    endTime = endTime.format(formatter)
                )

                localRepository.insertStep(stepData)
            }
        }
    }

    private suspend fun readExerciseSessions() {
        val start = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(90)
        val now = Instant.now()

        sessionsList.value = healthConnectManager.readExerciseSessions(start.toInstant(), now)
    }


    fun uploadAllSteps() {
        viewModelScope.launch {
            val steps = localRepository.getAllSteps()
            steps.forEach { remoteRepository.saveStep(it) }

            localRepository.deleteAllSteps()
        }
    }

    fun logGrantedPermissions() {
        viewModelScope.launch {
            val grantedList = healthConnectManager.getGrantedPermissions()
            Log.d("MainViewModel", "Granted permissions list: $grantedList")
        }
    }

    private suspend fun tryWithPermissionsCheck(method: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        backgroundReadAvailable.value = healthConnectManager.isFeatureAvailable(
            HealthConnectFeatures.FEATURE_READ_HEALTH_DATA_IN_BACKGROUND
        )
        historyReadAvailable.value = healthConnectManager.isFeatureAvailable(
            HealthConnectFeatures.FEATURE_READ_HEALTH_DATA_HISTORY
        )
        backgroundReadGranted.value = healthConnectManager.hasAllPermissions(backgroundReadPermissions)
        historyReadGranted.value = healthConnectManager.hasAllPermissions(historyReadPermissions)

        uiState = try {
            if (permissionsGranted.value) {
                method()
            }
            UiState.Done
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }

    fun insertExerciseSession() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val latestStartOfSession = ZonedDateTime.now().minusMinutes(30)
                val offset = Random.nextDouble()

                // Generate random start time between the start of the day and (now - 30mins).
                val startOfSession = startOfDay.plusSeconds(
                    (Duration.between(startOfDay, latestStartOfSession).seconds * offset).toLong()
                )
                val endOfSession = startOfSession.plusMinutes(30)

                healthConnectManager.writeExerciseSession(startOfSession, endOfSession)
                readExerciseSessions()
            }
        }
    }

    fun enqueueReadStepWorker() {
        healthConnectManager.enqueueReadStepWorker()
    }


    sealed class UiState {
        object Uninitialized : UiState()
        object Done : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

class MainViewModelFactory(
    private val healthConnectManager: HealthConnectManager,
    private val localRepository: LocalRepository,
    private val remoteRepository: StepRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                healthConnectManager = healthConnectManager,
                localRepository = localRepository,
                remoteRepository = remoteRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}