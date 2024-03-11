package jp.ikigai.kwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.dto.TransactionDetails
import jp.ikigai.kwallet.data.entity.CounterParty
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterPartyDao {

    @Upsert
    suspend fun upsertCounterParty(counterParty: CounterParty)

    @Delete
    suspend fun deleteCounterParty(counterParty: CounterParty)

    @Query("SELECT * FROM counter_party WHERE id = :id")
    fun getCounterParty(id: Long): Flow<CounterParty>

    @Query("SELECT * FROM counter_party WHERE id = :id")
    suspend fun getCounterPartyById(id: Long): CounterParty

    @Query("SELECT COUNT(*) FROM counter_party")
    fun getCounterPartyCount(): Flow<Int>

    @Query("SELECT * FROM counter_party ORDER BY (frequency * (1 - (strftime('%s', 'now') * 1000) - lastUsed) / :decayConstant) ASC")
    fun getCounterParties(decayConstant: Float = Constants.decayConstant): Flow<List<CounterParty>>

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
                " FROM transactions WHERE counterPartyId = :counterPartyId AND currency = :currency AND time BETWEEN :startDate AND :endDate ORDER BY time DESC"
    )
    fun getAllTransactionsForCounterParty(
        counterPartyId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionDetails>>
}