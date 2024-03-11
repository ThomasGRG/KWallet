package jp.ikigai.kwallet.extensions

import jp.ikigai.kwallet.data.enums.UpsertTransactionFormStatus
import jp.ikigai.kwallet.data.dto.UpsertTransactionValidationStatus

fun UpsertTransactionValidationStatus.isValid() : Boolean {
    return this.amountStatus == UpsertTransactionFormStatus.VALID &&
            this.categoryStatus == UpsertTransactionFormStatus.VALID &&
            this.counterPartyStatus == UpsertTransactionFormStatus.VALID &&
            this.transactionMethodStatus == UpsertTransactionFormStatus.VALID &&
            this.transactionNatureStatus == UpsertTransactionFormStatus.VALID &&
            this.transactionSourceStatus == UpsertTransactionFormStatus.VALID &&
            this.transactionTypeStatus == UpsertTransactionFormStatus.VALID
}