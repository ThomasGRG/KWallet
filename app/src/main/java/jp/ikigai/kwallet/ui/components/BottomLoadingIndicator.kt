package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ColumnScope.BottomLoadingIndicator() {
    Spacer(modifier = Modifier.weight(1f))
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth()
    )
}