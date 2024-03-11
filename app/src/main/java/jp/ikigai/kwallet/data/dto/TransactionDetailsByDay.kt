package jp.ikigai.kwallet.data.dto

data class TransactionDetailsByDay(
    val transactionDetails: List<TransactionDetailsWithIcons>,
    val credit: Float,
    val debit: Float
)
