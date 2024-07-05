package com.vverbytskyi.featureflags

data class MainScreenState(
    val enable: Boolean = false,
    val useOverride: Boolean = false,
    val usedOverriddenValue: Boolean = false
)
