package jp.ikigai.kwallet.ui.viewmodels.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.Constants.DEBIT
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.data.dto.TransactionDetailsByDayForType
import jp.ikigai.kwallet.data.dto.TransactionDetailsWithIcons
import jp.ikigai.kwallet.data.entity.TransactionType
import jp.ikigai.kwallet.data.repository.TransactionTypeRepository
import jp.ikigai.kwallet.extensions.getFirstDayOfMonth
import jp.ikigai.kwallet.extensions.getIconByType
import jp.ikigai.kwallet.extensions.getLastDayOfMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class TransactionTypeDetailsViewModel(
    private val transactionTypeRepository: TransactionTypeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val transactionTypeId: Long = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(TransactionTypeDetailsScreenState())
    val state: StateFlow<TransactionTypeDetailsScreenState> = _state.asStateFlow()

    init {
        getTransactionType()
        getTransactionDetails()
    }

    private fun getTransactionType() = viewModelScope.launch {
        transactionTypeRepository.getTransactionType(transactionTypeId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    transactionType = it
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getTransactionDetails() = viewModelScope.launch {
        state.flatMapLatest {
            transactionTypeRepository.getAllTransactionsForTransactionType(
                transactionTypeId = transactionTypeId,
                currency = it.selectedCurrency,
                startDate = it.selectedYearMonth.getFirstDayOfMonth(),
                endDate = it.selectedYearMonth.getLastDayOfMonth()
            )
        }.collectLatest { transactionDetails ->
            val transactionDetailsWithIcons = transactionDetails.map {
                TransactionDetailsWithIcons(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    amount = it.amount,
                    currency = it.currency,
                    transactionBaseType = it.transactionBaseType,
                    time = it.time,
                    chips = listOf(
                        Pair(
                            it.categoryName,
                            it.categoryIcon.getIconByType(Type.CATEGORY)
                        ),
                        Pair(
                            it.counterPartyName,
                            it.counterPartyIcon.getIconByType(Type.COUNTERPARTY)
                        ),
                        Pair(
                            it.transactionMethodName,
                            it.transactionMethodIcon.getIconByType(Type.METHOD)
                        ),
                        Pair(
                            it.transactionNatureName,
                            it.transactionNatureIcon.getIconByType(Type.NATURE)
                        ),
                        Pair(
                            it.transactionSourceName,
                            it.transactionSourceIcon.getIconByType(Type.SOURCE)
                        ),
                        Pair(
                            it.transactionTypeName,
                            it.transactionTypeIcon.getIconByType(Type.TYPE)
                        ),
                    )
                )
            }
            val transactionMap =
                transactionDetailsWithIcons
                    .groupBy {
                        Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
            val transactionDetailsMap = mutableMapOf<LocalDate, TransactionDetailsByDayForType>()
            transactionMap.forEach {
                val cashFlow = it.value.map { transactionDetail -> transactionDetail.amount }.sum()
                transactionDetailsMap[it.key] = TransactionDetailsByDayForType(
                    transactionDetails = it.value,
                    cashFlow = cashFlow,
                )
            }
            _state.update { currentState ->
                currentState.copy(
                    transactions = transactionDetailsMap,
                    cashFlow = transactionDetails.map { it.amount }.sum(),
                    transactionCount = transactionDetails.size,
                    loading = false
                )
            }
        }
    }

    fun setCurrency(currency: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedCurrency = currency,
                loading = true
            )
        }
    }

    fun setYearMonth(yearMonth: YearMonth) {
        _state.update { currentState ->
            currentState.copy(
                selectedYearMonth = yearMonth,
                loading = true
            )
        }
    }
}

data class TransactionTypeDetailsScreenState(
    val transactionType: TransactionType = TransactionType(
        id = 0L,
        type = "",
        baseType = DEBIT,
        iconName = "CashBanknote"
    ),
    val transactions: Map<LocalDate, TransactionDetailsByDayForType> = mapOf(),
    val currencies: List<String> = Constants.currencyList.map { it.code },
    val selectedCurrency: String = Constants.currencyList[0].code,
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val cashFlow: Float = 0f,
    val transactionCount: Int = 0,
    val loading: Boolean = true
)