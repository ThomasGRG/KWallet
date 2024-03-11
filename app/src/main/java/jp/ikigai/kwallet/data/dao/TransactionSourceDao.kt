package jp.ikigai.kwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.dto.TransactionDetails
import jp.ikigai.kwallet.data.entity.TransactionSource
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionSourceDao {

    @Upsert
    suspend fun upsertTransactionSource(transactionSource: TransactionSource)

    @Delete
    suspend fun deleteTransactionSource(transactionSource: TransactionSource)

    @Query("SELECT * FROM transaction_sources WHERE id = :id")
    fun getTransactionSource(id: Long): Flow<TransactionSource>

    @Query("SELECT * FROM transaction_sources WHERE id = :id")
    suspend fun getTransactionSourceById(id: Long): TransactionSource

    @Query("SELECT COUNT(*) FROM transaction_sources")
    fun getTransactionSourceCount(): Flow<Int>

    @Query("SELECT * FROM transaction_sources ORDER BY (frequency * (1 - (strftime('%s', 'now') * 1000) - lastUsed) / :decayConstant) ASC")
    fun getTransactionSources(decayConstant: Float = Constants.decayConstant): Flow<List<TransactionSource>>

    @Query("SELECT SUM(balance) FROM transaction_sources WHERE currency = :currency")
    fun getTransactionSourceBalanceByCurrency(currency: String): Flow<Float>

    @Transaction
    @Query(
        "SELECT" +
                " id AS id," +
                " title AS title," +
                " description AS description," +
                " amount AS amount," +
                " time AS time," +
                " currency AS currency," +
                " (SELECT c.name FROM category c WHERE c.id = categoryId) AS categoryName," +
                " (SELECT c.iconName FROM category c WHERE c.id = categoryId) AS categoryIcon," +
                " (SELECT cp.name FROM counter_party cp WHERE cp.id = counterPartyId) AS counterPartyName," +
                " (SELECT cp.iconName FROM counter_party cp WHERE cp.id = counterPartyId) AS counterPartyIcon," +
                " (SELECT tm.method FROM transaction_method tm WHERE tm.id = transactionMethodId) AS transactionMethodName," +
                " (SELECT tm.iconName FROM transaction_method tm WHERE tm.id = transactionMethodId) AS transactionMethodIcon," +
                " (SELECT tn.nature FROM transaction_nature tn WHERE tn.id = transactionNatureId) AS transactionNatureName," +
                " (SELECT tn.iconName FROM transaction_nature tn WHERE tn.id = transactionNatureId) AS transactionNatureIcon," +
                " (SELECT ts.name FROM transaction_sources ts WHERE ts.id = transactionSourceId) AS transactionSourceName," +
                " (SELECT ts.iconName FROM transaction_sources ts WHERE ts.id = transactionSourceId) AS transactionSourceIcon," +
                " (SELECT tt.type FROM transaction_type tt WHERE tt.id = transactionTypeId) AS transactionTypeName," +
                " (SELECT tt.baseType FROM transaction_type tt WHERE tt.id = transactionTypeId) AS transactionBaseType," +
                " (SELECT tt.iconName FROM transaction_type tt WHERE tt.id = transactionTypeId) AS transactionTypeIcon" +
                " FROM transactions WHERE transactionSourceId = :transactionSourceId AND currency = :currency AND time BETWEEN :startDate AND :endDate ORDER BY time DESC"
    )
    fun getAllTransactionsForTransactionSource(
        transactionSourceId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionDetails>>
}