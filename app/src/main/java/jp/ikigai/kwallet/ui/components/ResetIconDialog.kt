package jp.ikigai.kwallet.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.ikigai.kwallet.R

@Composable
fun ResetIconDialog(
    dismiss: () -> Unit,
    reset: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current

    AlertDialog(
        onDismissRequest = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            dismiss()
        },
        confirmButton = {
            Button(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    reset()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_reset))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    dismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        },
        text = {
            Text(text = stringResource(id = R.string.reset_icon_dialog_label))
        },
        icon = {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = "reset")
        }
    )
}

@Preview
@Composable
fun ResetIconDialogPreview() {
    ResetIconDialog(
        dismiss = {},
        reset = {}
    )
}