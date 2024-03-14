package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.AllIcons
import compose.icons.TablerIcons
import compose.icons.tablericons.AlertOctagon
import jp.ikigai.kwallet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoCard(
    title: String,
    icon: String,
    frequency: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = TablerIcons.AllIcons.find { it.name == icon }
                    ?: TablerIcons.AlertOctagon,
                contentDescription = icon,
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.frequency_of_use_label, frequency),
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Divider(
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = true,
                        onClick = onEditClick
                    )
                    .padding(top = 15.dp, bottom = 15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "edit",
                )
                Text(
                    text = stringResource(id = R.string.card_edit),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun InfoCardPreview() {
    InfoCard(
        title = "Food & Drinks",
        icon = "Archive",
        frequency = 0,
        onClick = {},
        onEditClick = {}
    )
}