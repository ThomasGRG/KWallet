package jp.ikigai.kwallet.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.extensions.getIconByType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenTopAppBar(
    title: String,
    iconName: String,
    type: Type
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            Icon(imageVector = iconName.getIconByType(type), contentDescription = iconName)
        },
        modifier = Modifier.padding(start = 6.dp)
    )
}

@Preview
@Composable
fun DetailsScreenTopAppBarPreview() {
    DetailsScreenTopAppBar(title = "Category", iconName = "Archive", type = Type.CATEGORY)
}