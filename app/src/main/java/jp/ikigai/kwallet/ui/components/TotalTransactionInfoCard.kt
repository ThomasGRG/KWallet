package jp.ikigai.kwallet.ui.components

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowUpCircle
import jp.ikigai.kwallet.R
import java.util.Locale

@Composable
fun TotalTransactionInfoCard(
    currency: String,
    total: Float,
    count: Int,
    color: Color,
    maxWidth: Float,
    icon: ImageVector
) {
    ElevatedCard(
        modifier = Modifier
            .widthIn(min = 150.dp, max = maxWidth.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = icon,
                    contentDescription = icon.name,
                    tint = color,
                )
                Text(
                    text = stringResource(
                        id = R.string.transaction_amount_label,
                        NumberFormatter.withLocale(Locale.getDefault())
                            .notation(Notation.simple())
                            .precision(
                                Precision.minMaxFraction(2, 2)
                            )
                            .format(total)
                            .toString(),
                        currency,
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Text(
                text = pluralStringResource(
                    id = R.plurals.transaction_count_label,
                    count,
                    count,
                ),
            )
        }
    }
}

@Preview
@Composable
fun TotalTransactionInfoCardPreview() {
    TotalTransactionInfoCard(
        currency = "INR",
        total = 204f,
        count = 2,
        color = Color(0xFFF44336),
        maxWidth = 200f,
        icon = TablerIcons.ArrowUpCircle
    )
}