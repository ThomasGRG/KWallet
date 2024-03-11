package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.ikigai.kwallet.R
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterChipRow(
    currency: String,
    setCurrencyExpanded: () -> Unit,
    yearMonth: YearMonth,
    setYearMonthExpanded: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        FilterChip(
            selected = true,
            onClick = { setCurrencyExpanded() },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.currency_filter_label,
                        currency
                    )
                )
            },
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = true,
            onClick = { setYearMonthExpanded() },
            label = {
                Text(text = "${yearMonth.month.name}, ${yearMonth.year}")
            }
        )
    }
}

@Preview
@Composable
fun TransactionFilterChipRowPreview() {
    TransactionFilterChipRow(
        currency = "INR",
        setCurrencyExpanded = {},
        yearMonth = YearMonth.now(),
        setYearMonthExpanded = {}
    )
}