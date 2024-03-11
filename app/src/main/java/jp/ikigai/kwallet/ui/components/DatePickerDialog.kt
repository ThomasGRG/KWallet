package jp.ikigai.kwallet.ui.components

import android.content.res.Configuration
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.ikigai.kwallet.R
import jp.ikigai.kwallet.extensions.toZonedDateTime
import kotlinx.coroutines.flow.collectLatest
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    date: ZonedDateTime,
    updateDate: (ZonedDateTime) -> Unit,
    dismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.toInstant().toEpochMilli() + (date.offset.totalSeconds * 1000)
    )

    var orientation by remember {
        mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT)
    }

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }
            .collectLatest {
                if (it != Configuration.ORIENTATION_PORTRAIT) {
                    datePickerState.displayMode = DisplayMode.Input
                } else {
                    datePickerState.displayMode = DisplayMode.Picker
                }
                orientation = it
            }
    }

    DatePickerDialog(
        onDismissRequest = {
            dismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    updateDate(
                        datePickerState.selectedDateMillis!!.toZonedDateTime()
                    )
                    dismiss()
                },
            ) {
                Text(text = stringResource(id = R.string.dialog_select))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dismiss()
                },
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview
@Composable
fun DatePickerDialogPreview() {
    DatePickerDialog(
        date = ZonedDateTime.now(),
        dismiss = {},
        updateDate = {}
    )
}