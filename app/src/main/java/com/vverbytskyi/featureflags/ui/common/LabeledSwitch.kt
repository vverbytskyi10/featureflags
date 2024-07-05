package com.vverbytskyi.featureflags.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vverbytskyi.featureflags.ui.theme.FeatureFlagsTheme

@Composable
fun LabeledSwitch(
    modifier: Modifier = Modifier,
    label: String,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .clickable(
                role = Role.Switch,
                onClick = { onCheckChange(!isChecked) }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Switch(checked = isChecked, onCheckedChange = onCheckChange)
        Text(text = label)
    }
}

@Preview
@Composable
private fun LabeledSwitchPreview() {
    FeatureFlagsTheme {
        LabeledSwitch(label = "Enable", isChecked = false) {}
    }
}