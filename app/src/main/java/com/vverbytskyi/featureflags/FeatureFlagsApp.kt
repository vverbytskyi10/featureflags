package com.vverbytskyi.featureflags

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.configUpdates
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class FeatureFlagsApp : Application() {

    private var updateListenerJob: Job? = null

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()

        initFirebaseRemoteConfig()
        tryFetchAndActivate()
        listenForUpdates()
    }

    private fun initFirebaseRemoteConfig() {
        remoteConfig = Firebase.remoteConfig
    }

    private fun tryFetchAndActivate() {
        GlobalScope.launch {
            runCatching { remoteConfig.fetchAndActivate().await() }
                // .onSuccess { logger.info { "Successfully pulled remote configuration" } }
                // .onFailure { throwable -> logger.warn(throwable) { "Could not pull remote configuration" } }
        }
    }

    private fun listenForUpdates() {
        if (updateListenerJob != null) {
            // logger.debug { "Tried to update active listener while its still active, aborting" }
            return
        }

        updateListenerJob = GlobalScope.launch {
            remoteConfig.configUpdates
                .catch {
                    // if firebase update flow was cancelled it wraps cause with Cancellation exception
                    val throwable = if (it is CancellationException) {
                        it.cause
                    } else {
                        it
                    }
                    updateListenerJob = null
                    // logger.warn(throwable) { "Config update error, aborting current job" }
                }
                .collect {
                    runCatching { remoteConfig.activate().await() }
                        // .onSuccess { logger.info { "Activated newly fetched configuration" } }
                        // .onFailure { throwable -> logger.warn(throwable) { "Failed to activate newly fetched configuration" } }
                }
        }
    }
}