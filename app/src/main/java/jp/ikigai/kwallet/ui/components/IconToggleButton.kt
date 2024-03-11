package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.AllIcons
import compose.icons.TablerIcons
import compose.icons.tablericons.Archive
import compose.icons.tablericons.QuestionMark

@Composable
fun IconToggleButton(
    label: String,
    iconName: String,
    selected: Boolean,
    toggle: () -> Unit
) {
    val icon by remember(key1 = iconName) {
        mutableStateOf(TablerIcons.AllIcons.find { it.name == iconName } ?: TablerIcons.QuestionMark)
    }

    val colors = if (selected) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.outlinedButtonColors()
    val border = if (!selected) ButtonDefaults.outlinedButtonBorder else null

    FilledTonalButton(
        onClick = toggle,
        colors = colors,
        border = border
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "icon"
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
fun IconToggleButtonPreview() {
    IconToggleButton(
        label = "Transportation",
        iconName = TablerIcons.Archive.name,
        selected = true,
        toggle = {}
    )
}