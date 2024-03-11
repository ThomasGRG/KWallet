package jp.ikigai.kwallet.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import jp.ikigai.kwallet.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UpsertTransactionSelectionBottomSheet(
    data: List<Triple<Long, String, String>>,
    selectedId: Long,
    dismiss: () -> Unit,
    select: (Long) -> Unit,
    sheetState: SheetState,
) {
    val configuration = LocalConfiguration.current

    val scrollState = rememberScrollState()

    var orientation by remember {
        mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT)
    }
    var screenHeight by remember {
        mutableIntStateOf(configuration.screenHeightDp)
    }
    val maxHeight = remember(key1 = screenHeight, key2 = orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenHeight * 0.45
        } else {
            screenHeight * 0.9
        }
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
        shape = RoundedCornerShape(10)
    ) {
        FlowRow(
            modifier = Modifier
                .heightIn(0.dp, maxHeight.dp)
                .padding(start = 8.dp, end = 8.dp)
                .verticalScroll(
                    state = scrollState
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(data.size) {
                IconToggleButton(
                    label = data[it].second,
                    iconName = data[it].third,
                    selected = data[it].first == selectedId,
                    toggle = {
                        select(data[it].first)
                        dismiss()
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
@Preview(showSystemUi = true)
@Composable
fun UpsertTransactionSelectionBottomSheetPreview() {
    UpsertTransactionSelectionBottomSheet(
        data = listOf(
            Triple(1L, "Food", "Archive"),
            Triple(2L, "Food", "Archive"),
            Triple(3L, "Food", "Archive"),
            Triple(4L, "Food", "Archive"),
            Triple(5L, "Food", "Archive"),
            Triple(6L, "Food", "Archive"),
            Triple(7L, "Food", "Archive"),
            Triple(8L, "Food", "Archive"),
            Triple(9L, "Food", "Archive"),
            Triple(10L, "Food", "Archive"),
            Triple(11L, "Food", "Archive"),
            Triple(12L, "Food", "Archive"),
        ),
        selectedId = 0L,
        dismiss = {},
        select = {},
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
    )
}