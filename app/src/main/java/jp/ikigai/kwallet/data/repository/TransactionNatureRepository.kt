package jp.ikigai.kwallet.data.repository

import jp.ikigai.kwallet.data.dao.TransactionNatureDao
import jp.ikigai.kwallet.data.entity.TransactionNature

class TransactionNatureRepository(private val transactionNatureDao: TransactionNatureDao) {

    suspend fun saveTransactionNature(transactionNature: TransactionNature) =
        transactionNatureDao.upsertTransactionNature(transactionNature)

    suspend fun deleteTransactionNature(transactionNature: TransactionNature) =
        transactionNatureDao.deleteTransactionNature(transactionNature)

    fun getTransactionNature(id: Long) = transactionNatureDao.getTransactionNature(id)

    suspend fun getTransactionNatureById(id: Long) = transactionNatureDao.getTransactionNatureById(id)

    fun getTransactionNatureCount() = transactionNatureDao.getTransactionNatureCount()

    fun getAllTransactionNatures() = transactionNatureDao.getTransactionNatures()

    fun getAllTransactionsForTransactionNature(
        transactionNatureId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ) = transactionNatureDao.getAllTransactionsForTransactionNature(
        transactionNatureId,
        currency,
        startDate,
        endDate
    )
}