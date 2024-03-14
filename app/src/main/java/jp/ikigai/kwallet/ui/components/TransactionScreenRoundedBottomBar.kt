package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import compose.icons.TablerIcons
import compose.icons.tablericons.BuildingBank
import compose.icons.tablericons.Users

@Composable
fun TransactionScreenRoundedBottomBar(
    navigateToMoreScreen: () -> Unit,
    navigateToTransactionSourcesScreen: () -> Unit,
    navigateToCounterPartyScreen: () -> Unit,
    addTransaction: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current

    RoundedBottomBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navigateToTransactionSourcesScreen()
                }
            ) {
                Icon(
                    imageVector = TablerIcons.BuildingBank,
                    contentDescription = "transaction sources"
                )
            }
            IconButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navigateToCounterPartyScreen()
                }
            ) {
                Icon(
                    imageVector = TablerIcons.Users,
                    contentDescription = "counter parties"
                )
            }
            FloatingActionButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    addTransaction()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "add new transaction"
                )
            }
            IconButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "settings")
            }
            IconButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navigateToMoreScreen()
                }
            ) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "more")
            }
        }
    }
}

@Preview
@Composable
fun TransactionScreenRoundedBottomBarPreview() {
    TransactionScreenRoundedBottomBar(
        navigateToMoreScreen = {},
        navigateToTransactionSourcesScreen = {},
        navigateToCounterPartyScreen = {},
        addTransaction = {}
    )
}