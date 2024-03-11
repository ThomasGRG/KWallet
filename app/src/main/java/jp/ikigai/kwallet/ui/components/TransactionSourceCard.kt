package jp.ikigai.kwallet.ui.components

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
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
import compose.icons.tablericons.BuildingBank
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSourceCard(
    title: String,
    currency: String,
    balance: Float,
    icon: String,
    frequency: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
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
                    text = "Frequency of use : $frequency",
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = NumberFormatter.withLocale(Locale.getDefault())
                        .notation(Notation.simple())
                        .precision(
                            Precision.minMaxFraction(2, 2)
                        )
                        .format(balance)
                        .toString(),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = currency,
                    modifier = Modifier.padding(start = 6.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun TransactionSourceCardPreview() {
    TransactionSourceCard(
        title = "Title",
        currency = "INR",
        balance = 73451.0f,
        icon = TablerIcons.BuildingBank.name,
        frequency = 0,
        onClick = {}
    )
}