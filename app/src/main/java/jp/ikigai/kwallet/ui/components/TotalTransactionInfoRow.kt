package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowDownCircle
import compose.icons.tablericons.ArrowUpCircle
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TotalTransactionInfoRow(
    currency: String,
    expenses: Float,
    expensesCount: Int,
    income: Float,
    incomeCount: Int,
) {
    val configuration = LocalConfiguration.current

    var screenWidth by remember {
        mutableIntStateOf(configuration.screenWidthDp)
    }

    val maxWidth = remember(key1 = screenWidth) {
        if (screenWidth <= 280) {
            screenWidth * 1f
        } else {
            screenWidth * 0.40f
        }
    }

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.screenWidthDp }
            .collectLatest { screenWidth = it }
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TotalTransactionInfoCard(
            currency = currency,
            total = expenses,
            count = expensesCount,
            color = Color(0xFFF44336),
            maxWidth = maxWidth,
            icon = TablerIcons.ArrowUpCircle
        )
        TotalTransactionInfoCard(
            currency = currency,
            total = income,
            count = incomeCount,
            color = Color(0xFF4CAF50),
            maxWidth = maxWidth,
            icon = TablerIcons.ArrowDownCircle
        )
    }
}

@Preview
@Composable
fun TotalTransactionInfoRowPreview() {
    TotalTransactionInfoRow(
        currency = "INR",
        expenses = 230f,
        expensesCount = 4,
        income = 394f,
        incomeCount = 6
    )
}