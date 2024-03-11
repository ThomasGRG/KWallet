package jp.ikigai.kwallet.data.repository

import jp.ikigai.kwallet.data.dao.TransactionTypeDao
import jp.ikigai.kwallet.data.entity.TransactionType

class TransactionTypeRepository(private val transactionTypeDao: TransactionTypeDao) {

    suspend fun saveTransactionType(transactionType: TransactionType) =
        transactionTypeDao.upsertTransactionType(transactionType)

    suspend fun deleteTransactionType(transactionType: TransactionType) =
        transactionTypeDao.deleteTransactionType(transactionType)

    fun getTransactionType(id: Long) = transactionTypeDao.getTransactionType(id)

    suspend fun getTransactionTypeById(id: Long) = transactionTypeDao.getTransactionTypeById(id)

    fun getTransactionTypeCount() = transactionTypeDao.getTransactionTypeCount()

    fun getAllTransactionTypes() = transactionTypeDao.getTransactionTypes()

    fun getAllTransactionsForTransactionType(
        transactionTypeId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ) = transactionTypeDao.getAllTransactionsForTransactionType(
        transactionTypeId,
        currency,
        startDate,
        endDate
    )
}