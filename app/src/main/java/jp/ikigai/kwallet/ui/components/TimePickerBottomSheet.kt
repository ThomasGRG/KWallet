package jp.ikigai.kwallet.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
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
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerBottomSheet(
    date: ZonedDateTime,
    updateTime: (Int, Int) -> Unit,
    dismiss: () -> Unit,
    sheetState: SheetState,
) {
    val configuration = LocalConfiguration.current

    var orientation by remember {
        mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT)
    }

    val timePickerState = rememberTimePickerState(
        is24Hour = false,
        initialHour = date.hour,
        initialMinute = date.minute
    )

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }
            .collectLatest {
                orientation = it
            }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            dismiss()
        },
        shape = RoundedCornerShape(10),
    ) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TimePicker(
                        state = timePickerState
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TimeInput(state = timePickerState)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
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
                    updateTime(timePickerState.hour, timePickerState.minute)
                    dismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.dialog_select))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TimePickerBottomSheetPreview() {
    TimePickerBottomSheet(
        date = ZonedDateTime.now(),
        dismiss = {},
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        updateTime = { _, _ -> }
    )
}