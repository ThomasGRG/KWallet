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
fun ConfirmDeleteDialog(
    message: String,
    dismiss: () -> Unit,
    delete: () -> Unit,
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
                    delete()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_delete))
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
            Text(text = message)
        },
        icon = {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = "delete")
        }
    )
}

@Preview
@Composable
fun ConfirmDeleteDialogPreview() {
    ConfirmDeleteDialog(message = "", dismiss = {}, delete = {})
}