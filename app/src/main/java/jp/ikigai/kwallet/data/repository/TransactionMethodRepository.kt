package jp.ikigai.kwallet.data.repository

import jp.ikigai.kwallet.data.dao.TransactionMethodDao
import jp.ikigai.kwallet.data.entity.TransactionMethod

class TransactionMethodRepository(private val transactionMethodDao: TransactionMethodDao) {

    suspend fun saveTransactionMethod(transactionMethod: TransactionMethod) =
        transactionMethodDao.upsertTransactionMethod(transactionMethod)

    suspend fun deleteTransactionMethod(transactionMethod: TransactionMethod) =
        transactionMethodDao.deleteTransactionMethod(transactionMethod)

    fun getTransactionMethod(id: Long) = transactionMethodDao.getTransactionMethod(id)

    suspend fun getTransactionMethodById(id: Long) = transactionMethodDao.getTransactionMethodById(id)

    fun getTransactionMethodCount() = transactionMethodDao.getTransactionMethodCount()

    fun getAllTransactionMethods() = transactionMethodDao.getTransactionMethods()

    fun getAllTransactionsForTransactionMethod(
        transactionMethodId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ) = transactionMethodDao.getAllTransactionsForTransactionMethod(
        transactionMethodId,
        currency,
        startDate,
        endDate
    )
}