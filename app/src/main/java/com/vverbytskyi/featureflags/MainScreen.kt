package com.vverbytskyi.featureflags

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vverbytskyi.featureflags.ui.common.LabeledSwitch
import com.vverbytskyi.featureflags.ui.theme.FeatureFlagsTheme

@Composable
fun MainScreen(viewModel: MainViewModel) {

    val state = viewModel.screenState.collectAsState()

    MainScreenImpl(
        state.value.enable,
        state.value.useOverride,
        state.value.usedOverriddenValue,
        onOverrideValueChange = viewModel::onUseOverrideCheckChange,
        onOverriddenValueChange = viewModel::onUsedOverriddenCheckChange
    )
}

@Composable
private fun MainScreenImpl(
    enabled: Boolean,
    override: Boolean,
    overriddenValue: Boolean,
    onOverrideValueChange: (Boolean) -> Unit,
    onOverriddenValueChange: (Boolean) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ColorSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(if (enabled) Color.Green else Color.Red)
            )
            SettingsSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                isOverrideChecked = override,
                isEnableFeatureChecked = overriddenValue,
                onOverrideSwitchChanged = onOverrideValueChange,
                onEnableFeatureSwitchChanged = onOverriddenValueChange
            )
        }
    }
}

@Composable
private fun ColorSection(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}

@Composable
private fun SettingsSection(
    modifier: Modifier = Modifier,
    isOverrideChecked: Boolean,
    isEnableFeatureChecked: Boolean,
    onOverrideSwitchChanged: (Boolean) -> Unit,
    onEnableFeatureSwitchChanged: (Boolean) -> Unit
) {
    Column(modifier = modifier.padding(8.dp)) {
        LabeledSwitch(
            modifier = Modifier.fillMaxWidth(),
            label = "Override FCM value",
            isChecked = isOverrideChecked,
            onCheckChange = onOverrideSwitchChanged
        )
        LabeledSwitch(
            modifier = Modifier.fillMaxWidth(),
            label = "Enable feature",
            isChecked = isEnableFeatureChecked,
            onCheckChange = onEnableFeatureSwitchChanged
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    FeatureFlagsTheme {
        MainScreenImpl(
            enabled = false,
            override = false,
            overriddenValue = true,
            onOverrideValueChange = { },
            onOverriddenValueChange = { }
        )
    }
}
