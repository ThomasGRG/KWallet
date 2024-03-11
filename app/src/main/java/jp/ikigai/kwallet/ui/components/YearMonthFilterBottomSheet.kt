package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Filter
import jp.ikigai.kwallet.R
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearMonthFilterBottomSheet(
    sheetState: SheetState,
    dismiss: () -> Unit,
    filter: (YearMonth) -> Unit,
    selectedYearMonth: YearMonth,
) {
    val (yearMonth, setYearMonth) = remember(key1 = selectedYearMonth) {
        mutableStateOf(
            selectedYearMonth
        )
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = TablerIcons.Filter, contentDescription = TablerIcons.Filter.name)
            Text(text = stringResource(id = R.string.dialog_filter))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp, top = 25.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = yearMonth.month.name)
            Slider(
                value = yearMonth.month.value.toFloat(),
                onValueChange = {
                    setYearMonth(YearMonth.of(yearMonth.year, it.toInt()))
                },
                enabled = true,
                steps = 10,
                valueRange = 1f..12f
            )
            Text(text = yearMonth.year.toString())
            Slider(
                value = yearMonth.year.toFloat(),
                onValueChange = {
                    setYearMonth(YearMonth.of(it.toInt(), yearMonth.month))
                },
                enabled = true,
                steps = YearMonth.now().year - 2000 - 1,
                valueRange = 2000f..YearMonth.now().year.toFloat()
            )
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
            TextButton(
                onClick = {
                    filter(yearMonth)
                    dismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.dialog_filter))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun YearMonthFilterBottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    YearMonthFilterBottomSheet(
        sheetState = sheetState,
        dismiss = {},
        filter = {},
        selectedYearMonth = YearMonth.now(),
    )
}