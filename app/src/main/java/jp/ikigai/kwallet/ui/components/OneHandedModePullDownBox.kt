package jp.ikigai.kwallet.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun OneHandedModePullDownBox(
    expand: Boolean,
    setExpand: (Boolean) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val expandHeight = remember(key1 = configuration.screenHeightDp) {
        configuration.screenHeightDp / 2
    }

    val heightModifier = if (expand) {
        Modifier.height(expandHeight.dp)
    } else {
        Modifier.height(0.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(heightModifier)
            .clickable { setExpand(false) }
    ) {}
}
