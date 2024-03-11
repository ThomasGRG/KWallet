package jp.ikigai.kwallet.data.dto

import jp.ikigai.kwallet.data.entity.Category
import jp.ikigai.kwallet.data.entity.CounterParty
import jp.ikigai.kwallet.data.entity.Transaction
import jp.ikigai.kwallet.data.entity.TransactionMethod
import jp.ikigai.kwallet.data.entity.TransactionNature
import jp.ikigai.kwallet.data.entity.TransactionSource
import jp.ikigai.kwallet.data.entity.TransactionType

data class UpsertTransactionFlows(
    val categoryFlow: List<Category>,
    val counterPartyFlow: List<CounterParty>,
    val transactionMethodFlow: List<TransactionMethod>,
    val transactionNatureFlow: List<TransactionNature>,
    val transactionSourceFlow: List<TransactionSource>,
    val transactionTypeFlow: List<TransactionType>,
    val transaction: Transaction?
)
