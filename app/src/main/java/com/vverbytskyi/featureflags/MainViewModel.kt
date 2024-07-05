package com.vverbytskyi.featureflags

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.vverbytskyi.featureflags.common.extensions.dataStore
import com.vverbytskyi.featureflags.common.extensions.getBooleanAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val KEY_USE_OVERRIDE_VALUE = booleanPreferencesKey("use_override_value")
private val KEY_USED_OVERRIDDEN_VALUE = booleanPreferencesKey("used_overridden_value")

class MainViewModel(
    app: Application,
    private val dataStore: DataStore<Preferences>,
    private val remoteConfig: FirebaseRemoteConfig
) : AndroidViewModel(app) {

    private val useOverrideValue: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_USE_OVERRIDE_VALUE] ?: false }

    private val usedOverriddenValue: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_USED_OVERRIDDEN_VALUE] ?: false }

    val screenState = combine(
        useOverrideValue,
        usedOverriddenValue,
        remoteConfig.getBooleanAsFlow("enableFeature")
    ) { useOverrideValue, usedOverriddenValue, remoteConfigValue ->
        MainScreenState(
            enable = usedOverriddenValue.takeIf { useOverrideValue } ?: remoteConfigValue,
            useOverride = useOverrideValue,
            usedOverriddenValue = usedOverriddenValue
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MainScreenState())

    fun onUseOverrideCheckChange(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences -> preferences[KEY_USE_OVERRIDE_VALUE] = value }
        }
    }

    fun onUsedOverriddenCheckChange(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences -> preferences[KEY_USED_OVERRIDDEN_VALUE] = value }
        }
    }

    companion object {

        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    val app: Application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                    MainViewModel(
                        app = app,
                        dataStore = app.dataStore,
                        remoteConfig = Firebase.remoteConfig
                    ) as T
                } else {
                    error("")
                }
            }
        }
    }
}