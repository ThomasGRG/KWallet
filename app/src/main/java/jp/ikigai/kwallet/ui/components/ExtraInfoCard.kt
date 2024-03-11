package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.AllIcons
import compose.icons.TablerIcons
import compose.icons.tablericons.AlertOctagon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraInfoCard(
    title: String,
    subTitle: String,
    icon: String,
    frequency: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = TablerIcons.AllIcons.find { it.name == icon }
                    ?: TablerIcons.AlertOctagon,
                contentDescription = icon,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Frequency of use : $frequency",
                )
            }
        }
    }
}

@Preview
@Composable
fun ExtraInfoCardPreview() {
    ExtraInfoCard(
        title = "test",
        subTitle = "subTitle",
        icon = "Archive",
        frequency = 1,
        onClick = {}
    )
}