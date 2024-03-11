package jp.ikigai.kwallet.ui.components

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale

@Composable
fun TransactionHeader(
    date: LocalDate,
    amount: Float,
    currency: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(top = 10.dp, bottom = 10.dp, start = 5.dp, end = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${date.dayOfMonth} ${date.month.name} ${date.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = date.dayOfWeek.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.alpha(0.8f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = NumberFormatter.withLocale(Locale.getDefault())
                .notation(Notation.simple())
                .precision(
                    Precision.minMaxFraction(2, 2)
                )
                .format(amount)
                .toString() + " " + currency,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun TransactionHeaderPreview() {
    TransactionHeader(date = LocalDate.now(), amount = -9377.0f, currency = "INR")
}