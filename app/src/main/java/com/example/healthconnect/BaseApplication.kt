package com.example.healthconnect

import android.app.Application
import com.example.healthconnect.helper.HealthConnectManager

class BaseApplication : Application() {
    val healthConnectManager by lazy {
        HealthConnectManager(this)
    }
}