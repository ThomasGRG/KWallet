package jp.ikigai.kwallet.data.dto

data class TransactionDetailsByDayForType(
    val transactionDetails: List<TransactionDetailsWithIcons>,
    val cashFlow: Float,
)
