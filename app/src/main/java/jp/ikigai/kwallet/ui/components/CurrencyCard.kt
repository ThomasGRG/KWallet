package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.murgupluoglu.flagkit.FlagKit
import jp.ikigai.kwallet.data.Constants.currencyList
import jp.ikigai.kwallet.data.entity.Currency

@Composable
fun CurrencyCard(
    currency: Currency
) {
    val context = LocalContext.current

    ElevatedCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = FlagKit.getResId(context, currency.countryCode)),
                contentDescription = currency.countryCode,
                modifier = Modifier
                    .size(60.dp, 40.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = "${currency.name} - ${currency.code}",
            )
        }
    }
}

@Preview
@Composable
fun CurrencyCardPreview() {
    CurrencyCard(
        currencyList[0]
    )
}