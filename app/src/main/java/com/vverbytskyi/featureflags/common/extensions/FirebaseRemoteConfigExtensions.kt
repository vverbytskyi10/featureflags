package com.vverbytskyi.featureflags.common.extensions

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.configUpdates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun FirebaseRemoteConfig.getBooleanAsFlow(key: String): Flow<Boolean> {
    return flow {
        var currentValue = this@getBooleanAsFlow.getBoolean(key)
        emit(currentValue)

        configUpdates.collect { updates ->
            if (key in updates.updatedKeys) {
                val updatedValue = this@getBooleanAsFlow.getBoolean(key)
                if (updatedValue != currentValue) {
                    currentValue = updatedValue
                    emit(currentValue)
                }
            }
        }
    }
}