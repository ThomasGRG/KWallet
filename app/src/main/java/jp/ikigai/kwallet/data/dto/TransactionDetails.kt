package jp.ikigai.kwallet.data.dto

data class TransactionDetails(
    val id: Long,
    val amount: Float,
    val title: String,
    val description: String,
    val time: Long,
    val currency: String,
    val categoryName: String,
    val categoryIcon: String,
    val counterPartyName: String,
    val counterPartyIcon: String,
    val transactionMethodName: String,
    val transactionMethodIcon: String,
    val transactionNatureName: String,
    val transactionNatureIcon: String,
    val transactionSourceName: String,
    val transactionSourceIcon: String,
    val transactionTypeName: String,
    val transactionBaseType: String,
    val transactionTypeIcon: String,
)
