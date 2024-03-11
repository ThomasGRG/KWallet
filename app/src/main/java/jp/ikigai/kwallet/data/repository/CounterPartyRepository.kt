package jp.ikigai.kwallet.data.repository

import jp.ikigai.kwallet.data.dao.CounterPartyDao
import jp.ikigai.kwallet.data.entity.CounterParty

class CounterPartyRepository(private val counterPartyDao: CounterPartyDao) {

    suspend fun saveCounterParty(counterParty: CounterParty) =
        counterPartyDao.upsertCounterParty(counterParty)

    suspend fun deleteCounterParty(counterParty: CounterParty) =
        counterPartyDao.deleteCounterParty(counterParty)

    fun getCounterParty(id: Long) = counterPartyDao.getCounterParty(id)

    suspend fun getCounterPartyById(id: Long) = counterPartyDao.getCounterPartyById(id)

    fun getCounterPartyCount() = counterPartyDao.getCounterPartyCount()

    fun getAllCounterParties() = counterPartyDao.getCounterParties()

    fun getAllTransactionsForCounterParty(
        counterPartyId: Long,
        currency: String,
        startDate: Long,
        endDate: Long
    ) = counterPartyDao.getAllTransactionsForCounterParty(
        counterPartyId,
        currency,
        startDate,
        endDate
    )
}