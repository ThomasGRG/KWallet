package jp.ikigai.kwallet.ui.viewmodels.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.Constants.currencyList
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.data.dto.TransactionDetailsByDay
import jp.ikigai.kwallet.data.dto.TransactionDetailsWithIcons
import jp.ikigai.kwallet.data.entity.TransactionMethod
import jp.ikigai.kwallet.data.repository.TransactionMethodRepository
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

class TransactionMethodDetailsViewModel(
    private val transactionMethodRepository: TransactionMethodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val transactionMethodId : Long = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(TransactionMethodDetailsScreenState())
    val state: StateFlow<TransactionMethodDetailsScreenState> = _state.asStateFlow()

    init {
        getTransactionMethod()
        getTransactionDetails()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getTransactionDetails() = viewModelScope.launch {
        state.flatMapLatest {
            transactionMethodRepository.getAllTransactionsForTransactionMethod(
                transactionMethodId = transactionMethodId,
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
            val transactionDetailsMap = mutableMapOf<LocalDate, TransactionDetailsByDay>()
            transactionMap.forEach {
                val credit =
                    it.value.filter { transactionDetail -> transactionDetail.transactionBaseType == Constants.CREDIT }
                        .map { transactionDetail -> transactionDetail.amount }.sum()
                val debit =
                    it.value.filter { transactionDetail -> transactionDetail.transactionBaseType == Constants.DEBIT }
                        .map { transactionDetail -> transactionDetail.amount }.sum()
                transactionDetailsMap[it.key] = TransactionDetailsByDay(
                    transactionDetails = it.value,
                    debit = debit,
                    credit = credit
                )
            }
            val incomeTransactions =
                transactionDetails.filter { it.transactionBaseType == Constants.CREDIT }
            val expensesTransactions =
                transactionDetails.filter { it.transactionBaseType == Constants.DEBIT }
            _state.update { currentState ->
                currentState.copy(
                    transactions = transactionDetailsMap,
                    expenses = expensesTransactions.map { it.amount }.sum(),
                    expensesTransactionCount = expensesTransactions.size,
                    income = incomeTransactions.map { it.amount }.sum(),
                    incomeTransactionCount = incomeTransactions.size,
                    loading = false
                )
            }
        }
    }

    private fun getTransactionMethod() = viewModelScope.launch {
        transactionMethodRepository.getTransactionMethod(transactionMethodId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    transactionMethod = it
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

data class TransactionMethodDetailsScreenState(
    val transactionMethod: TransactionMethod = TransactionMethod(
        id = 0L,
        method = "",
        iconName = "CreditCard"
    ),
    val transactions: Map<LocalDate, TransactionDetailsByDay> = mapOf(),
    val currencies: List<String> = currencyList.map { it.code },
    val selectedCurrency: String = currencyList[0].code,
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val income: Float = 0f,
    val incomeTransactionCount: Int = 0,
    val expenses: Float = 0f,
    val expensesTransactionCount: Int = 0,
    val loading: Boolean = true
)