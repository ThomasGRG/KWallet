package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import jp.ikigai.kwallet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCardActionBottomSheet(
    sheetState: SheetState,
    dismiss: () -> Unit,
    clone: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current

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
                .clickable(
                    enabled = true,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        clone()
                    }
                )
                .padding(top = 10.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = TablerIcons.Copy, contentDescription = TablerIcons.Copy.name)
            Text(text = stringResource(id = R.string.button_clone))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TransactionCardActionBottomSheetPreview() {
    TransactionCardActionBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        dismiss = {},
        clone = {}
    )
}