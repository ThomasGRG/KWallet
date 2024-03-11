package jp.ikigai.kwallet.data.dto

data class TransactionFieldCount(
    val categoryCount: Int,
    val counterPartyCount: Int,
    val transactionMethodCount: Int,
    val transactionNatureCount: Int,
    val transactionSourceCount: Int,
    val transactionTypeCount: Int,
)
