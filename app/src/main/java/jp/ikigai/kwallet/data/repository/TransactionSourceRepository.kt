package jp.ikigai.kwallet.data.repository

import jp.ikigai.kwallet.data.dao.TransactionSourceDao
import jp.ikigai.kwallet.data.entity.TransactionSource

class TransactionSourceRepository(private val transactionSourceDao: TransactionSourceDao) {

    suspend fun saveTransactionSource(transactionSource: TransactionSource) =
        transactionSourceDao.upsertTransactionSource(transactionSource)

    suspend fun deleteTransactionSource(transactionSource: TransactionSource) =
        transactionSourceDao.deleteTransactionSource(transactionSource)

    fun getTransactionSource(id: Long) = transactionSourceDao.getTransactionSource(id)

    suspend fun getTransactionSourceById(id: Long) = transactionSourceDao.getTransactionSourceById(id)

    fun getTransactionSourceCount() = transactionSourceDao.getTransactionSourceCount()

    fun getTransactionSourceBalanceByCurrency(currency: String) = transactionSourceDao.getTransactionSourceBalanceByCurrency(currency)

    fun getTransactionSources() = transactionSourceDao.getTransactionSources()

    fun getAllTransactionsForTransactionSource(
        transactionSourceId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ) = transactionSourceDao.getAllTransactionsForTransactionSource(
        transactionSourceId,
        currency,
        startDate,
        endDate
    )
}