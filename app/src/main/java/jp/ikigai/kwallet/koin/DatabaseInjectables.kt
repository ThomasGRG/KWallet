package jp.ikigai.kwallet.koin

import android.content.Context
import androidx.room.Room
import jp.ikigai.kwallet.data.Database

fun getDatabase(context: Context) =
    Room.databaseBuilder(context = context, Database::class.java, "database")
        .allowMainThreadQueries()
        .build()

fun getBankDao(database: Database) = database.bankDao()

fun getCategoryDao(database: Database) = database.categoryDao()

fun getCounterPartyDao(database: Database) = database.counterPartyDao()

fun getTransactionDao(database: Database) = database.transactionDao()

fun getTransactionMethodDao(database: Database) = database.transactionMethodDao()

fun getTransactionNatureDao(database: Database) = database.transactionNatureDao()

fun getTransactionTypeDao(database: Database) = database.transactionTypeDao()