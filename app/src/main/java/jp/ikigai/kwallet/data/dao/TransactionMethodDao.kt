package jp.ikigai.kwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.dto.TransactionDetails
import jp.ikigai.kwallet.data.entity.TransactionMethod
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionMethodDao {

    @Upsert
    suspend fun upsertTransactionMethod(transactionMethod: TransactionMethod)

    @Delete
    suspend fun deleteTransactionMethod(transactionMethod: TransactionMethod)

    @Query("SELECT * FROM transaction_method WHERE id = :id")
    fun getTransactionMethod(id: Long): Flow<TransactionMethod>

    @Query("SELECT * FROM transaction_method WHERE id = :id")
    suspend fun getTransactionMethodById(id: Long): TransactionMethod

    @Query("SELECT COUNT(*) FROM transaction_method")
    fun getTransactionMethodCount(): Flow<Int>

    @Query("SELECT * FROM transaction_method ORDER BY (frequency * (1 - (strftime('%s', 'now') * 1000) - lastUsed) / :decayConstant) ASC")
    fun getTransactionMethods(decayConstant: Float = Constants.decayConstant): Flow<List<TransactionMethod>>

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
                " FROM transactions WHERE transactionMethodId = :transactionMethodId AND currency = :currency AND time BETWEEN :startDate AND :endDate ORDER BY time DESC"
    )
    fun getAllTransactionsForTransactionMethod(
        transactionMethodId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionDetails>>
}