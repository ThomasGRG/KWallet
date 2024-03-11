package jp.ikigai.kwallet.data.repository

import jp.ikigai.kwallet.data.dao.TransactionDao
import jp.ikigai.kwallet.data.entity.Transaction

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun saveTransaction(transaction: Transaction) =
        transactionDao.upsertTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    fun getTransaction(id: Long) = transactionDao.getTransaction(id)

    suspend fun getTransactionById(id: Long) = transactionDao.getTransactionById(id)

    fun getAllTransactions(
        currency: String,
        startDate: Long,
        endDate: Long
    ) = transactionDao.getAllTransactions(currency, startDate, endDate)
}