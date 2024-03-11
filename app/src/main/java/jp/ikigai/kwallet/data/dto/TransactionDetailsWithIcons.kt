package jp.ikigai.kwallet.data.dto

import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionDetailsWithIcons(
    val id: Long,
    val amount: Float,
    val title: String,
    val description: String,
    val time: Long,
    val currency: String,
    val transactionBaseType: String,
    val chips: List<Pair<String, ImageVector>>
)
