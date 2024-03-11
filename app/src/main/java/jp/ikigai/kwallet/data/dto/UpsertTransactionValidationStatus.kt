package jp.ikigai.kwallet.data.dto

import jp.ikigai.kwallet.data.enums.UpsertTransactionFormStatus

data class UpsertTransactionValidationStatus(
    val amountStatus: UpsertTransactionFormStatus = UpsertTransactionFormStatus.VALID,
    val categoryStatus: UpsertTransactionFormStatus = UpsertTransactionFormStatus.VALID,
    val counterPartyStatus: UpsertTransactionFormStatus = UpsertTransactionFormStatus.VALID,
    val transactionMethodStatus: UpsertTransactionFormStatus = UpsertTransactionFormStatus.VALID,
    val transactionNatureStatus: UpsertTransactionFormStatus = UpsertTransactionFormStatus.VALID,
    val transactionSourceStatus: UpsertTransactionFormStatus = UpsertTransactionFormStatus.VALID,
    val transactionTypeStatus: UpsertTransactionFormStatus = UpsertTransactionFormStatus.VALID,
)
