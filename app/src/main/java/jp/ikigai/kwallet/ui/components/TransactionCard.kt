package jp.ikigai.kwallet.ui.components

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Archive
import compose.icons.tablericons.ArrowDownCircle
import compose.icons.tablericons.ArrowUpCircle
import jp.ikigai.kwallet.data.Constants.CREDIT
import jp.ikigai.kwallet.data.Constants.DEBIT
import jp.ikigai.kwallet.data.dto.TransactionDetailsWithIcons
import java.util.Locale

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun TransactionCard(
    transactionDetailsWithIcon: TransactionDetailsWithIcons,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 0.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (transactionDetailsWithIcon.title != "") {
                Text(
                    text = transactionDetailsWithIcon.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            if (transactionDetailsWithIcon.description != "") {
                Text(
                    text = transactionDetailsWithIcon.description,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (transactionDetailsWithIcon.transactionBaseType == CREDIT) {
                Icon(
                    imageVector = TablerIcons.ArrowDownCircle,
                    contentDescription = "credit icon",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(30.dp)
                )
            } else {
                Icon(
                    imageVector = TablerIcons.ArrowUpCircle,
                    contentDescription = "debit icon",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = NumberFormatter.withLocale(Locale.getDefault())
                    .notation(Notation.simple())
                    .precision(
                        Precision.minMaxFraction(2, 2)
                    )
                    .format(transactionDetailsWithIcon.amount)
                    .toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transactionDetailsWithIcon.currency,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(transactionDetailsWithIcon.chips.size) {
                FilledTonalButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = transactionDetailsWithIcon.chips[it].second,
                        contentDescription = transactionDetailsWithIcon.chips[it].second.name
                    )
                    Text(text = transactionDetailsWithIcon.chips[it].first)
                }
            }
        }
    }
}

@Preview
@Composable
fun TransactionCardPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TransactionCard(
            transactionDetailsWithIcon = TransactionDetailsWithIcons(
                id = 0L,
                amount = 230f,
                title = "",
                description = "description",
                time = 0L,
                currency = "INR",
                transactionBaseType = DEBIT,
                chips = listOf(
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                ),
            ),
            onClick = {},
            onLongClick = {},
        )
        TransactionCard(
            transactionDetailsWithIcon = TransactionDetailsWithIcons(
                id = 0L,
                amount = 230f,
                title = "",
                description = "",
                time = 0L,
                currency = "INR",
                transactionBaseType = DEBIT,
                chips = listOf(
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                ),
            ),
            onClick = {},
            onLongClick = {},
        )
        TransactionCard(
            transactionDetailsWithIcon = TransactionDetailsWithIcons(
                id = 0L,
                amount = 230f,
                title = "Title",
                description = "description",
                time = 0L,
                currency = "INR",
                transactionBaseType = DEBIT,
                chips = listOf(
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                    Pair("Category", TablerIcons.Archive),
                ),
            ),
            onClick = {},
            onLongClick = {},
        )
    }
}