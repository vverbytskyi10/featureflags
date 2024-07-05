package com.vverbytskyi.featureflags.common.extensions

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val PREFERENCES_NAME = "app_prefs"

val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)
