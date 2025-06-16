package com.example.healthconnect.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.example.healthconnect.MainViewModel
import java.util.UUID

@Composable
fun HomeScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    backgroundReadPermissions: Set<String>,
    backgroundReadAvailable: Boolean,
    backgroundReadGranted: Boolean,
    historyReadPermissions: Set<String>,
    historyReadAvailable: Boolean,
    historyReadGranted: Boolean,
    onBackgroundReadClick: () -> Unit = {},
    sessionsList: List<ExerciseSessionRecord>,
    uiState: MainViewModel.UiState,
    onInsertClick: () -> Unit = {},
    onInsertDummyClick: () -> Unit = {},
    onDetailsClick: (String) -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
) {

    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    LaunchedEffect(uiState) {
        Log.d("ExerciseSessionScreen", "LaunchedEffect uiState=$uiState")
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is MainViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [ExerciseSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (uiState is MainViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    if (uiState != MainViewModel.UiState.Uninitialized) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!permissionsGranted) {
                item {
                    Button(
                        onClick = {
                            onPermissionsLaunch(permissions)
                        }
                    ) {
                        Text(text = "Request Permission")
                    }
                }
            } else {
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(4.dp),
                        onClick = {
                            onInsertClick()
                        }
                    ) {
                        Text(text = "Insert Data")
                    }
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(4.dp),
                        onClick = {
                            onInsertDummyClick()
                        }) { Text(text = "Insert Dummy Step") }
                }
                if (!backgroundReadGranted) {
                    item {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(4.dp),
                            onClick = {
                                onPermissionsLaunch(backgroundReadPermissions)
                            },
                            enabled = backgroundReadAvailable,
                        ) {
                            if (backgroundReadAvailable){
                                Text(text = "Request Background Read")
                            } else {
                                Text(text = "Background Read is not available")
                            }
                        }
                    }
                } else {
                    item {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(4.dp),
                            onClick = {
                                onBackgroundReadClick()
                            },
                        ) {
                            Text(text = "Read Steps in Background")
                        }
                    }
                }

                if (!historyReadGranted) {
                    item {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(4.dp),
                            onClick = {
                                onPermissionsLaunch(historyReadPermissions)
                            },
                            enabled = historyReadAvailable,
                        ) {
                            if (historyReadAvailable){
                                Text(text = "Request History Read")
                            } else {
                                Text(text = "History Read is not available")
                            }
                        }
                    }
                }

                items(sessionsList) { session ->
                    Log.d("HomeScreen", "Session: ${session.metadata.id}, Title: ${session.title}")
                }
            }
        }
    }
}