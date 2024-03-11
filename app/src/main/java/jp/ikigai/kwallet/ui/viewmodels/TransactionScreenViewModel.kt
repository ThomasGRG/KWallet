package jp.ikigai.kwallet.ui.viewmodels

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.dto.TransactionDetailsByDay
import jp.ikigai.kwallet.data.dto.TransactionDetailsWithIcons
import jp.ikigai.kwallet.data.dto.TransactionFieldCount
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.data.repository.CategoryRepository
import jp.ikigai.kwallet.data.repository.CounterPartyRepository
import jp.ikigai.kwallet.data.repository.TransactionMethodRepository
import jp.ikigai.kwallet.data.repository.TransactionNatureRepository
import jp.ikigai.kwallet.data.repository.TransactionRepository
import jp.ikigai.kwallet.data.repository.TransactionSourceRepository
import jp.ikigai.kwallet.data.repository.TransactionTypeRepository
import jp.ikigai.kwallet.extensions.combine
import jp.ikigai.kwallet.extensions.getFirstDayOfMonth
import jp.ikigai.kwallet.extensions.getIconByType
import jp.ikigai.kwallet.extensions.getLastDayOfMonth
import jp.ikigai.kwallet.extensions.toMilli
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class TransactionScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val counterPartyRepository: CounterPartyRepository,
    private val transactionMethodRepository: TransactionMethodRepository,
    private val transactionNatureRepository: TransactionNatureRepository,
    private val transactionSourceRepository: TransactionSourceRepository,
    private val transactionTypeRepository: TransactionTypeRepository,
) : ViewModel() {

    private val strings: MutableSet<String> = mutableSetOf()

    private val numberFormatter = NumberFormatter
        .withLocale(Locale.getDefault())
        .notation(Notation.simple())
        .precision(
            Precision.minMaxFraction(2, 2)
        )

    private val _state = MutableStateFlow(TransactionScreenState())
    val state: StateFlow<TransactionScreenState> = _state.asStateFlow()

    init {
        getBalance()
        getTransaction()
        canAddTransaction()
    }

    fun setStrings(stringList: List<String>) {
        strings.addAll(stringList)
    }

    private fun getSnackBarString(transactionFieldCount: TransactionFieldCount): String {
        val statusStrings: MutableList<String> = mutableListOf()
        if (transactionFieldCount.categoryCount == 0) {
            statusStrings.add(strings.elementAt(2).lowercase())
        }
        if (transactionFieldCount.counterPartyCount == 0) {
            statusStrings.add(strings.elementAt(3).lowercase())
        }
        if (transactionFieldCount.transactionMethodCount == 0) {
            statusStrings.add(strings.elementAt(4).lowercase())
        }
        if (transactionFieldCount.transactionNatureCount == 0) {
            statusStrings.add(strings.elementAt(5).lowercase())
        }
        if (transactionFieldCount.transactionSourceCount == 0) {
            statusStrings.add(strings.elementAt(6).lowercase())
        }
        if (transactionFieldCount.transactionTypeCount == 0) {
            statusStrings.add(strings.elementAt(7).lowercase())
        }
        if (statusStrings.isNotEmpty()) {
            val joinedString =
                statusStrings.joinToString(separator = ", ").replaceFirstChar { firstChar -> firstChar.uppercase() }
            val endString = if (statusStrings.size > 1) strings.elementAt(0) else strings.elementAt(1)
            val lastItem = joinedString.substringAfterLast(",", "")
            return if (lastItem == "") {
                joinedString.substringBeforeLast(",") + " " + endString
            } else {
                joinedString.substringBeforeLast(",") + " &" + lastItem + " " + endString
            }
        }
        return ""
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getBalance() = viewModelScope.launch {
        state.flatMapLatest {
            transactionSourceRepository.getTransactionSourceBalanceByCurrency(it.selectedCurrency)
        }.collectLatest { balance ->
            _state.update {
                it.copy(
                    balance = numberFormatter.format(balance).toString()
                )
            }
        }
    }

    private fun canAddTransaction() = viewModelScope.launch {
        combine(
            categoryRepository.getCategoryCount(),
            counterPartyRepository.getCounterPartyCount(),
            transactionMethodRepository.getTransactionMethodCount(),
            transactionNatureRepository.getTransactionNatureCount(),
            transactionSourceRepository.getTransactionSourceCount(),
            transactionTypeRepository.getTransactionTypeCount(),
        ) { it1, it2, it3, it4, it5, it6 ->
            TransactionFieldCount(
                categoryCount = it1,
                counterPartyCount = it2,
                transactionMethodCount = it3,
                transactionNatureCount = it4,
                transactionSourceCount = it5,
                transactionTypeCount = it6,
            )
        }.collectLatest { transactionFieldCount ->
            val addButtonEnabled = transactionFieldCount.categoryCount != 0 &&
                    transactionFieldCount.counterPartyCount != 0 &&
                    transactionFieldCount.transactionMethodCount != 0 &&
                    transactionFieldCount.transactionNatureCount != 0 &&
                    transactionFieldCount.transactionSourceCount != 0 &&
                    transactionFieldCount.transactionTypeCount != 0
            val snackBarText = getSnackBarString(transactionFieldCount)
            _state.update {
                it.copy(
                    addButtonEnabled = addButtonEnabled,
                    snackBarText = snackBarText
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getTransaction() = viewModelScope.launch {
        state.flatMapLatest {
            transactionRepository.getAllTransactions(
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

    fun cloneTransaction(id: Long) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loading = true
                )
            }

            val transaction = transactionRepository.getTransactionById(id)
            val category = categoryRepository.getCategoryById(transaction.categoryId)
            val counterParty = counterPartyRepository.getCounterPartyById(transaction.counterPartyId)
            val transactionMethod = transactionMethodRepository.getTransactionMethodById(transaction.transactionMethodId)
            val transactionNature = transactionNatureRepository.getTransactionNatureById(transaction.transactionNatureId)
            val transactionSource = transactionSourceRepository.getTransactionSourceById(transaction.transactionSourceId)
            val transactionType = transactionTypeRepository.getTransactionTypeById(transaction.transactionTypeId)

            listOf(
                viewModelScope.launch {
                    transactionRepository.saveTransaction(
                        transaction.copy(
                            id = 0L
                        )
                    )
                },
                viewModelScope.launch {
                    categoryRepository.saveCategory(
                        category.copy(
                            frequency = category.frequency + 1,
                            lastUsed = ZonedDateTime.now().toMilli()
                        )
                    )
                },
                viewModelScope.launch {
                    counterPartyRepository.saveCounterParty(
                        counterParty.copy(
                            frequency = counterParty.frequency + 1,
                            lastUsed = ZonedDateTime.now().toMilli()
                        )
                    )
                },
                viewModelScope.launch {
                    transactionMethodRepository.saveTransactionMethod(
                        transactionMethod.copy(
                            frequency = transactionMethod.frequency + 1,
                            lastUsed = ZonedDateTime.now().toMilli()
                        )
                    )
                },
                viewModelScope.launch {
                    transactionNatureRepository.saveTransactionNature(
                        transactionNature.copy(
                            frequency = transactionNature.frequency + 1,
                            lastUsed = ZonedDateTime.now().toMilli()
                        )
                    )
                },
                viewModelScope.launch {
                    transactionSourceRepository.saveTransactionSource(
                        transactionSource.copy(
                            balance = transactionSource.balance - transaction.amount,
                            frequency = transactionSource.frequency + 1,
                            lastUsed = ZonedDateTime.now().toMilli()
                        )
                    )
                },
                viewModelScope.launch {
                    transactionTypeRepository.saveTransactionType(
                        transactionType.copy(
                            frequency = transactionType.frequency + 1,
                            lastUsed = ZonedDateTime.now().toMilli()
                        )
                    )
                }
            ).joinAll()
        }
    }
}

data class TransactionScreenState(
    val transactions: Map<LocalDate, TransactionDetailsByDay> = mapOf(),
    val currencies: List<String> = Constants.currencyList.map { it.code },
    val selectedCurrency: String = Constants.currencyList[0].code,
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val income: Float = 0f,
    val incomeTransactionCount: Int = 0,
    val expenses: Float = 0f,
    val expensesTransactionCount: Int = 0,
    val balance: String = "0",
    val loading: Boolean = true,
    val addButtonEnabled: Boolean = false,
    val snackBarText: String = "",
)