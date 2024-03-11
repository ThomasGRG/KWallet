package jp.ikigai.kwallet.data

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.ikigai.kwallet.data.dao.CategoryDao
import jp.ikigai.kwallet.data.dao.CounterPartyDao
import jp.ikigai.kwallet.data.dao.TransactionDao
import jp.ikigai.kwallet.data.dao.TransactionMethodDao
import jp.ikigai.kwallet.data.dao.TransactionNatureDao
import jp.ikigai.kwallet.data.dao.TransactionSourceDao
import jp.ikigai.kwallet.data.dao.TransactionTypeDao
import jp.ikigai.kwallet.data.entity.Category
import jp.ikigai.kwallet.data.entity.CounterParty
import jp.ikigai.kwallet.data.entity.Transaction
import jp.ikigai.kwallet.data.entity.TransactionMethod
import jp.ikigai.kwallet.data.entity.TransactionNature
import jp.ikigai.kwallet.data.entity.TransactionSource
import jp.ikigai.kwallet.data.entity.TransactionType

@Database(
    version = 1,
    exportSchema = false,
    entities = [TransactionSource::class, Category::class, CounterParty::class, Transaction::class, TransactionMethod::class, TransactionNature::class, TransactionType::class]
)
abstract class Database : RoomDatabase() {

    abstract fun bankDao() : TransactionSourceDao

    abstract fun categoryDao() : CategoryDao

    abstract fun counterPartyDao() : CounterPartyDao

    abstract fun transactionDao() : TransactionDao

    abstract fun transactionMethodDao() : TransactionMethodDao

    abstract fun transactionNatureDao() : TransactionNatureDao

    abstract fun transactionTypeDao() : TransactionTypeDao
}