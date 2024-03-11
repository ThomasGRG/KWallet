package jp.ikigai.kwallet.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Filter
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.data.Constants.currencyList
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyFilterBottomSheet(
    sheetState: SheetState,
    dismiss: () -> Unit,
    filter: (String) -> Unit,
    selectedCurrency: String,
    currencies: List<String>
) {
    val configuration = LocalConfiguration.current
    var orientation by remember {
        mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT)
    }
    var screenHeight by remember {
        mutableIntStateOf(configuration.screenHeightDp)
    }
    val maxHeight = remember(key1 = screenHeight, key2 = orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenHeight * 0.4
        } else {
            screenHeight * 0.8
        }
    }

    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        val index = currencies.indexOf(selectedCurrency)
        gridState.animateScrollToItem(index)
    }

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.screenHeightDp }
            .collectLatest { screenHeight = it }
        snapshotFlow { configuration.orientation }
            .collectLatest { orientation = it }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            dismiss()
        },
        shape = RoundedCornerShape(10),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = TablerIcons.Filter, contentDescription = TablerIcons.Filter.name)
            Text(text = stringResource(id = R.string.dialog_filter))
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, maxHeight.dp)
            ) {
                itemsIndexed(
                    items = currencies,
                    key = { _, currency -> "currency-${currency}" }
                ) { _, currency ->
                    ToggleButton(
                        label = currency,
                        selected = currency == selectedCurrency,
                        toggle = {
                            filter(currency)
                            dismiss()
                        }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    dismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CurrencyFilterBottomSheetPreview() {
    CurrencyFilterBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        dismiss = {},
        filter = {},
        selectedCurrency = "INR",
        currencies = currencyList.map { it.code }
    )
}