package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DetailsScreenRoundedBottomBar(
    editClick: () -> Unit,
    editIconDescription: String,
    deleteClick: () -> Unit,
    deleteIconDescription: String,
    navigateBack: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current

    RoundedBottomBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navigateBack()
                },
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "go back")
            }
            FloatingActionButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    editClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = editIconDescription
                )
            }
            IconButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    deleteClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = deleteIconDescription
                )
            }
        }
    }
}

@Preview
@Composable
fun DetailsScreenRoundedBottomBarPreview() {
    DetailsScreenRoundedBottomBar(
        editClick = {},
        editIconDescription = "",
        deleteClick = {},
        deleteIconDescription = "",
        navigateBack = {}
    )
}