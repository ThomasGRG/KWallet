package jp.ikigai.kwallet.extensions

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.AllIcons
import compose.icons.TablerIcons
import compose.icons.tablericons.QuestionMark
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.enums.Type

fun String.getIconByType(type: Type): ImageVector {
    return when (type) {
        Type.CATEGORY -> {
            return TablerIcons.AllIcons.find { it.name == this } ?: Constants.DEFAULT_CATEGORY_ICON
        }

        Type.COUNTERPARTY -> {
            return TablerIcons.AllIcons.find { it.name == this } ?: Constants.DEFAULT_COUNTERPARTY_ICON
        }

        Type.METHOD -> {
            return TablerIcons.AllIcons.find { it.name == this } ?: Constants.DEFAULT_TRANSACTION_METHOD_ICON
        }

        Type.NATURE -> {
            return TablerIcons.AllIcons.find { it.name == this } ?: Constants.DEFAULT_TRANSACTION_NATURE_ICON
        }

        Type.SOURCE -> {
            return TablerIcons.AllIcons.find { it.name == this } ?: Constants.DEFAULT_TRANSACTION_SOURCE_ICON
        }

        Type.TYPE -> {
            return TablerIcons.AllIcons.find { it.name == this } ?: Constants.DEFAULT_TRANSACTION_TYPE_ICON
        }

        else -> TablerIcons.QuestionMark
    }
}