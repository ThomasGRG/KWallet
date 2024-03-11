package jp.ikigai.kwallet.ui.viewmodels.upsert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.enums.Type
import jp.ikigai.kwallet.data.enums.UpsertTransactionFormStatus
import jp.ikigai.kwallet.data.dto.UpsertTransactionFlows
import jp.ikigai.kwallet.data.dto.UpsertTransactionValidationStatus
import jp.ikigai.kwallet.data.entity.Category
import jp.ikigai.kwallet.data.entity.CounterParty
import jp.ikigai.kwallet.data.entity.Transaction
import jp.ikigai.kwallet.data.entity.TransactionMethod
import jp.ikigai.kwallet.data.entity.TransactionNature
import jp.ikigai.kwallet.data.entity.TransactionSource
import jp.ikigai.kwallet.data.entity.TransactionType
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.repository.CategoryRepository
import jp.ikigai.kwallet.data.repository.CounterPartyRepository
import jp.ikigai.kwallet.data.repository.TransactionMethodRepository
import jp.ikigai.kwallet.data.repository.TransactionNatureRepository
import jp.ikigai.kwallet.data.repository.TransactionRepository
import jp.ikigai.kwallet.data.repository.TransactionSourceRepository
import jp.ikigai.kwallet.data.repository.TransactionTypeRepository
import jp.ikigai.kwallet.extensions.combine
import jp.ikigai.kwallet.extensions.isValid
import jp.ikigai.kwallet.extensions.toMilli
import jp.ikigai.kwallet.extensions.toZonedDateTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class UpsertTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val counterPartyRepository: CounterPartyRepository,
    private val transactionMethodRepository: TransactionMethodRepository,
    private val transactionNatureRepository: TransactionNatureRepository,
    private val transactionSourceRepository: TransactionSourceRepository,
    private val transactionTypeRepository: TransactionTypeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: Long = checkNotNull(savedStateHandle["id"])

    private var previousAmount = 0f

    private val loadDataJob: Job?

    private val strings: MutableSet<String> = mutableSetOf()

    private val _state = MutableStateFlow(UpsertTransactionScreenState())
    val state: StateFlow<UpsertTransactionScreenState> = _state.asStateFlow()

    init {
        loadDataJob = loadData()
    }

    fun setStrings(stringList: List<String>) {
        strings.addAll(stringList)
    }

    fun clearSnackBarString() {
        _state.update {
            it.copy(
                snackBarText = ""
            )
        }
    }

    private fun getSnackBarString(status: UpsertTransactionValidationStatus): String {
        if (status.isValid()) {
            return ""
        }

        if (status.amountStatus == UpsertTransactionFormStatus.INVALID_AMOUNT_VALUE_ENTERED) {
            return strings.elementAt(9)
        }

        if (status.transactionSourceStatus == UpsertTransactionFormStatus.NOT_ENOUGH_BALANCE) {
            return strings.elementAt(8)
        }

        val statusStrings: MutableList<String> = mutableListOf()
        if (status.categoryStatus == UpsertTransactionFormStatus.NO_CATEGORY_SELECTED) {
            statusStrings.add(strings.elementAt(2).lowercase())
        }
        if (status.counterPartyStatus == UpsertTransactionFormStatus.NO_COUNTERPARTY_SELECTED) {
            statusStrings.add(strings.elementAt(3).lowercase())
        }
        if (status.transactionMethodStatus == UpsertTransactionFormStatus.NO_TRANSACTION_METHOD_SELECTED) {
            statusStrings.add(strings.elementAt(4).lowercase())
        }
        if (status.transactionNatureStatus == UpsertTransactionFormStatus.NO_TRANSACTION_NATURE_SELECTED) {
            statusStrings.add(strings.elementAt(5).lowercase())
        }
        if (status.transactionSourceStatus == UpsertTransactionFormStatus.NO_TRANSACTION_SOURCE_SELECTED) {
            statusStrings.add(strings.elementAt(6).lowercase())
        }
        if (status.transactionTypeStatus == UpsertTransactionFormStatus.NO_TRANSACTION_TYPE_SELECTED) {
            statusStrings.add(strings.elementAt(7).lowercase())
        }
        if (statusStrings.isNotEmpty()) {
            val joinedString =
                statusStrings.joinToString(separator = ", ")
                    .replaceFirstChar { firstChar -> firstChar.uppercase() }
            val endString =
                if (statusStrings.size > 1) strings.elementAt(0) else strings.elementAt(1)
            val lastItem = joinedString.substringAfterLast(",", "")
            return if (lastItem == "") {
                joinedString.substringBeforeLast(",") + " " + endString
            } else {
                joinedString.substringBeforeLast(",") + " &" + lastItem + " " + endString
            }
        }
        return ""
    }

    private fun validateAll(): UpsertTransactionValidationStatus {
        var status = UpsertTransactionValidationStatus()

        val currentState = _state.value

        if (currentState.transaction.amount <= 0f) {
            status = status.copy(
                amountStatus = UpsertTransactionFormStatus.INVALID_AMOUNT_VALUE_ENTERED
            )
        }
        if (
            currentState.transaction.amount > 0f &&
            currentState.selectedTransactionType.id != 0L &&
            currentState.selectedTransactionSource.id != 0L &&
            currentState.selectedTransactionType.baseType == Constants.DEBIT &&
            currentState.transaction.amount > currentState.selectedTransactionSource.balance
        ) {
            status = status.copy(
                transactionSourceStatus = UpsertTransactionFormStatus.NOT_ENOUGH_BALANCE
            )
        }
        if (currentState.selectedCategory.id == 0L) {
            status = status.copy(
                categoryStatus = UpsertTransactionFormStatus.NO_CATEGORY_SELECTED
            )
        }
        if (currentState.selectedCounterParty.id == 0L) {
            status = status.copy(
                counterPartyStatus = UpsertTransactionFormStatus.NO_COUNTERPARTY_SELECTED
            )
        }
        if (currentState.selectedTransactionMethod.id == 0L) {
            status = status.copy(
                transactionMethodStatus = UpsertTransactionFormStatus.NO_TRANSACTION_METHOD_SELECTED
            )
        }
        if (currentState.selectedTransactionNature.id == 0L) {
            status = status.copy(
                transactionNatureStatus = UpsertTransactionFormStatus.NO_TRANSACTION_NATURE_SELECTED
            )
        }
        if (currentState.selectedTransactionSource.id == 0L) {
            status = status.copy(
                transactionSourceStatus = UpsertTransactionFormStatus.NO_TRANSACTION_SOURCE_SELECTED
            )
        }
        if (currentState.selectedTransactionType.id == 0L) {
            status = status.copy(
                transactionTypeStatus = UpsertTransactionFormStatus.NO_TRANSACTION_TYPE_SELECTED
            )
        }
        return status
    }

    private fun validateTransactionType(transactionType: TransactionType): UpsertTransactionValidationStatus {
        val status =
            _state.value.formStatus.copy(transactionTypeStatus = UpsertTransactionFormStatus.VALID)
        return if (_state.value.selectedTransactionSource.id != 0L && transactionType.baseType == Constants.DEBIT && _state.value.transaction.amount > _state.value.selectedTransactionSource.balance) {
            status.copy(transactionSourceStatus = UpsertTransactionFormStatus.NOT_ENOUGH_BALANCE)
        } else if (_state.value.selectedTransactionSource.id == 0L) {
            status.copy(transactionSourceStatus = UpsertTransactionFormStatus.NO_TRANSACTION_SOURCE_SELECTED)
        } else {
            status.copy(transactionSourceStatus = UpsertTransactionFormStatus.VALID)
        }
    }

    private fun validateTransactionSource(transactionSource: TransactionSource): UpsertTransactionValidationStatus {
        val status = _state.value.formStatus
        return if (_state.value.selectedTransactionType.id != 0L && _state.value.selectedTransactionType.baseType == Constants.DEBIT && _state.value.transaction.amount > transactionSource.balance) {
            status.copy(transactionSourceStatus = UpsertTransactionFormStatus.NOT_ENOUGH_BALANCE)
        } else {
            status.copy(transactionSourceStatus = UpsertTransactionFormStatus.VALID)
        }
    }

    fun upsertTransaction() {
        val validationResult = validateAll()
        val snackBarText = getSnackBarString(validationResult)
        if (validationResult.isValid()) {
            loadDataJob?.cancel()

            _state.update {
                it.copy(
                    loading = true
                )
            }

            val transaction = _state.value.transaction
            val category = _state.value.selectedCategory
            val counterParty = _state.value.selectedCounterParty
            val transactionMethod = _state.value.selectedTransactionMethod
            val transactionNature = _state.value.selectedTransactionNature
            val transactionSource = _state.value.selectedTransactionSource
            val transactionType = _state.value.selectedTransactionType
            val time = ZonedDateTime.now().toMilli()

            viewModelScope.launch {
                val jobs = if (transactionId != -1L) {
                    listOf(
                        updateTransactionJob(transaction),
                        updateTransactionSourceJob(
                            transactionSource = transactionSource,
                            frequency = transactionSource.frequency + 1,
                            time = time,
                            transactionBaseType = transactionType.baseType,
                            amount = transaction.amount,
                            isUpdate = true
                        )
                    )
                } else {
                    listOf(
                        updateTransactionJob(transaction),
                        updateCategoryJob(category, category.frequency + 1, time),
                        updateCounterPartyJob(counterParty, counterParty.frequency + 1, time),
                        updateTransactionMethodJob(
                            transactionMethod = transactionMethod,
                            frequency = transactionMethod.frequency + 1,
                            time = time
                        ),
                        updateTransactionNatureJob(
                            transactionNature = transactionNature,
                            frequency = transactionNature.frequency + 1,
                            time = time
                        ),
                        updateTransactionTypeJob(
                            transactionType = transactionType,
                            frequency = transactionType.frequency + 1,
                            time = time
                        ),
                        updateTransactionSourceJob(
                            transactionSource = transactionSource,
                            frequency = transactionSource.frequency + 1,
                            time = time,
                            isUpdate = false,
                            amount = transaction.amount,
                            transactionBaseType = transactionType.baseType
                        ),
                    )
                }
                jobs.joinAll()
                _state.update {
                    it.copy(
                        status = UpsertStatus.SUCCESS,
                        snackBarText = strings.elementAt(10),
                        formStatus = validationResult
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    status = UpsertStatus.FAILED,
                    snackBarText = snackBarText,
                    formStatus = validationResult
                )
            }
        }
    }

    fun deleteTransaction() {
        if (transactionId != -1L) {
            loadDataJob?.cancel()

            _state.update {
                it.copy(
                    loading = true
                )
            }

            val transaction = _state.value.transaction
            val category = _state.value.selectedCategory
            val counterParty = _state.value.selectedCounterParty
            val transactionMethod = _state.value.selectedTransactionMethod
            val transactionNature = _state.value.selectedTransactionNature
            val transactionSource = _state.value.selectedTransactionSource
            val transactionType = _state.value.selectedTransactionType

            viewModelScope.launch {
                listOf(
                    viewModelScope.launch {
                        transactionRepository.deleteTransaction(
                            transaction
                        )
                    },
                    updateCategoryJob(category, category.frequency - 1),
                    updateCounterPartyJob(counterParty, counterParty.frequency - 1),
                    updateTransactionMethodJob(transactionMethod, transactionMethod.frequency - 1),
                    updateTransactionNatureJob(transactionNature, transactionNature.frequency - 1),
                    updateTransactionTypeJob(transactionType, transactionType.frequency - 1),
                    updateTransactionSourceJob(
                        transactionSource = transactionSource,
                        frequency = transactionSource.frequency - 1,
                        transactionBaseType = Constants.CREDIT,
                        amount = transaction.amount,
                        isUpdate = false
                    ),
                ).joinAll()
                _state.update {
                    it.copy(
                        status = UpsertStatus.SUCCESS,
                        snackBarText = strings.elementAt(11),
                    )
                }
            }
        }
    }

    private fun updateTransactionJob(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.saveTransaction(
            transaction
        )
    }

    private fun updateCategoryJob(category: Category, frequency: Int, time: Long? = null) =
        viewModelScope.launch {
            categoryRepository.saveCategory(
                category.copy(
                    frequency = frequency,
                    lastUsed = time ?: category.lastUsed
                )
            )
        }

    private fun updateCounterPartyJob(
        counterParty: CounterParty,
        frequency: Int,
        time: Long? = null
    ) = viewModelScope.launch {
        counterPartyRepository.saveCounterParty(
            counterParty.copy(
                frequency = frequency,
                lastUsed = time ?: counterParty.lastUsed
            )
        )
    }

    private fun updateTransactionMethodJob(
        transactionMethod: TransactionMethod,
        frequency: Int,
        time: Long? = null
    ) = viewModelScope.launch {
        transactionMethodRepository.saveTransactionMethod(
            transactionMethod.copy(
                frequency = frequency,
                lastUsed = time ?: transactionMethod.lastUsed
            )
        )
    }

    private fun updateTransactionNatureJob(
        transactionNature: TransactionNature,
        frequency: Int,
        time: Long? = null
    ) = viewModelScope.launch {
        transactionNatureRepository.saveTransactionNature(
            transactionNature.copy(
                frequency = frequency,
                lastUsed = time ?: transactionNature.lastUsed
            )
        )
    }

    private fun updateTransactionSourceJob(
        transactionSource: TransactionSource,
        amount: Float,
        transactionBaseType: String,
        isUpdate: Boolean,
        frequency: Int,
        time: Long? = null
    ) = viewModelScope.launch {
        val balance = if (isUpdate) {
            if ((transactionBaseType == Constants.DEBIT && amount > previousAmount) || (transactionBaseType == Constants.CREDIT && amount <= previousAmount)) {
                transactionSource.balance - abs(amount - previousAmount)
            } else {
                transactionSource.balance + abs(amount - previousAmount)
            }
        } else {
            if (transactionBaseType == Constants.DEBIT) {
                transactionSource.balance - amount
            } else {
                transactionSource.balance + amount
            }
        }
        transactionSourceRepository.saveTransactionSource(
            transactionSource.copy(
                balance = balance,
                frequency = frequency,
                lastUsed = time ?: transactionSource.lastUsed
            )
        )
    }

    private fun updateTransactionTypeJob(
        transactionType: TransactionType,
        frequency: Int,
        time: Long? = null
    ) = viewModelScope.launch {
        transactionTypeRepository.saveTransactionType(
            transactionType.copy(
                frequency = frequency,
                lastUsed = time ?: transactionType.lastUsed
            )
        )
    }

    private fun loadData() = viewModelScope.launch {
        val flows = if (transactionId != -1L) {
            combine(
                categoryRepository.getAllCategory(),
                counterPartyRepository.getAllCounterParties(),
                transactionMethodRepository.getAllTransactionMethods(),
                transactionNatureRepository.getAllTransactionNatures(),
                transactionSourceRepository.getTransactionSources(),
                transactionTypeRepository.getAllTransactionTypes(),
                transactionRepository.getTransaction(transactionId)
            ) { it1, it2, it3, it4, it5, it6, it7 ->
                UpsertTransactionFlows(
                    categoryFlow = it1,
                    counterPartyFlow = it2,
                    transactionMethodFlow = it3,
                    transactionNatureFlow = it4,
                    transactionSourceFlow = it5,
                    transactionTypeFlow = it6,
                    transaction = it7
                )
            }
        } else {
            combine(
                categoryRepository.getAllCategory(),
                counterPartyRepository.getAllCounterParties(),
                transactionMethodRepository.getAllTransactionMethods(),
                transactionNatureRepository.getAllTransactionNatures(),
                transactionSourceRepository.getTransactionSources(),
                transactionTypeRepository.getAllTransactionTypes(),
            ) { it1, it2, it3, it4, it5, it6 ->
                UpsertTransactionFlows(
                    categoryFlow = it1,
                    counterPartyFlow = it2,
                    transactionMethodFlow = it3,
                    transactionNatureFlow = it4,
                    transactionSourceFlow = it5,
                    transactionTypeFlow = it6,
                    transaction = null
                )
            }
        }
        flows.collectLatest {
            if (it.transaction != null) {
                previousAmount = it.transaction.amount
                val time = it.transaction.time.toZonedDateTime()
                _state.update { currentState ->
                    currentState.copy(
                        dateTime = Triple(
                            time,
                            time.format(DateTimeFormatter.ofPattern("EEEE, dd-LLL-yyyy")),
                            time.format(DateTimeFormatter.ofPattern("hh:mm a"))
                        ),
                        displayAmount = it.transaction.amount.toString(),
                        transaction = it.transaction
                    )
                }
            }
            _state.update { currentState ->
                val category =
                    it.categoryFlow.find { category -> category.id == currentState.transaction.categoryId }
                        ?: Category()
                val counterParty =
                    it.counterPartyFlow.find { counterParty -> counterParty.id == currentState.transaction.counterPartyId }
                        ?: CounterParty()
                val transactionMethod =
                    it.transactionMethodFlow.find { transactionMethod -> transactionMethod.id == currentState.transaction.transactionMethodId }
                        ?: TransactionMethod()
                val transactionNature =
                    it.transactionNatureFlow.find { transactionNature -> transactionNature.id == currentState.transaction.transactionNatureId }
                        ?: TransactionNature()
                val transactionSource =
                    it.transactionSourceFlow.find { transactionSource -> transactionSource.id == currentState.transaction.transactionSourceId }
                        ?: TransactionSource()
                val transactionType =
                    it.transactionTypeFlow.find { transactionType -> transactionType.id == currentState.transaction.transactionTypeId }
                        ?: TransactionType()

                currentState.copy(
                    categories = it.categoryFlow,
                    counterParties = it.counterPartyFlow,
                    transactionMethods = it.transactionMethodFlow,
                    transactionNatures = it.transactionNatureFlow,
                    transactionSources = it.transactionSourceFlow,
                    transactionTypes = it.transactionTypeFlow,
                    loading = false,
                    selectedCategory = category,
                    selectedCounterParty = counterParty,
                    selectedTransactionMethod = transactionMethod,
                    selectedTransactionNature = transactionNature,
                    selectedTransactionSource = transactionSource,
                    selectedTransactionType = transactionType
                )
            }
        }
    }

    fun getListForSheetByType(type: Type): List<Triple<Long, String, String>> {
        return when (type) {
            Type.CATEGORY -> {
                _state.value.categories.map { Triple(it.id, it.name, it.iconName) }
            }

            Type.COUNTERPARTY -> {
                _state.value.counterParties.map { Triple(it.id, it.name, it.iconName) }
            }

            Type.METHOD -> {
                _state.value.transactionMethods.map { Triple(it.id, it.method, it.iconName) }
            }

            Type.NATURE -> {
                _state.value.transactionNatures.map { Triple(it.id, it.nature, it.iconName) }
            }

            Type.SOURCE -> {
                _state.value.transactionSources.map { Triple(it.id, it.name, it.iconName) }
            }

            Type.TYPE -> {
                _state.value.transactionTypes.map {
                    Triple(
                        it.id,
                        "${it.type} (${it.baseType})",
                        it.iconName
                    )
                }
            }

            else -> {
                emptyList()
            }
        }
    }

    fun updateDataByType(type: Type, id: Long) {
        when (type) {
            Type.CATEGORY -> {
                val formStatus =
                    _state.value.formStatus.copy(categoryStatus = UpsertTransactionFormStatus.VALID)
                val category =
                    _state.value.categories.find { it.id == id }
                if (category != null) {
                    _state.update {
                        it.copy(
                            transaction = it.transaction.copy(
                                categoryId = id
                            ),
                            selectedCategory = category,
                            formStatus = formStatus
                        )
                    }
                }
            }

            Type.COUNTERPARTY -> {
                val formStatus =
                    _state.value.formStatus.copy(counterPartyStatus = UpsertTransactionFormStatus.VALID)
                val counterParty =
                    _state.value.counterParties.find { it.id == id }
                if (counterParty != null) {
                    _state.update {
                        it.copy(
                            transaction = it.transaction.copy(
                                counterPartyId = id
                            ),
                            selectedCounterParty = counterParty,
                            formStatus = formStatus
                        )
                    }
                }
            }

            Type.METHOD -> {
                val formStatus =
                    _state.value.formStatus.copy(transactionMethodStatus = UpsertTransactionFormStatus.VALID)
                val transactionMethod =
                    _state.value.transactionMethods.find { it.id == id }
                if (transactionMethod != null) {
                    _state.update {
                        it.copy(
                            transaction = it.transaction.copy(
                                transactionMethodId = id
                            ),
                            selectedTransactionMethod = transactionMethod,
                            formStatus = formStatus
                        )
                    }
                }
            }

            Type.NATURE -> {
                val formStatus =
                    _state.value.formStatus.copy(transactionNatureStatus = UpsertTransactionFormStatus.VALID)
                val transactionNature =
                    _state.value.transactionNatures.find { it.id == id }
                if (transactionNature != null) {
                    _state.update {
                        it.copy(
                            transaction = it.transaction.copy(
                                transactionNatureId = id
                            ),
                            selectedTransactionNature = transactionNature,
                            formStatus = formStatus
                        )
                    }
                }
            }

            Type.SOURCE -> {
                val transactionSource =
                    _state.value.transactionSources.find { it.id == id }
                if (transactionSource != null) {
                    val validationResult = validateTransactionSource(transactionSource)
                    val snackBarText = getSnackBarString(validationResult)
                    _state.update {
                        it.copy(
                            transaction = it.transaction.copy(
                                transactionSourceId = id,
                                currency = transactionSource.currency
                            ),
                            selectedTransactionSource = transactionSource,
                            formStatus = validationResult,
                            snackBarText = snackBarText
                        )
                    }
                }
            }

            Type.TYPE -> {
                val transactionType =
                    _state.value.transactionTypes.find { it.id == id }
                if (transactionType != null) {
                    val validationResult = validateTransactionType(transactionType)
                    val snackBarText = getSnackBarString(validationResult)
                    _state.update {
                        it.copy(
                            transaction = it.transaction.copy(
                                transactionTypeId = id
                            ),
                            selectedTransactionType = transactionType,
                            formStatus = validationResult,
                            snackBarText = snackBarText
                        )
                    }
                }
            }

            else -> return
        }
    }

    fun setDate(date: ZonedDateTime) {
        _state.update {
            val newDate = it.dateTime.first.withYear(date.year)
                .withMonth(date.month.value)
                .withDayOfMonth(date.dayOfMonth)
            it.copy(
                dateTime = it.dateTime.copy(
                    newDate,
                    newDate.format(DateTimeFormatter.ofPattern("EEEE, dd-LLL-yyyy")),
                    newDate.format(DateTimeFormatter.ofPattern("hh:mm a"))
                ),
                transaction = it.transaction.copy(
                    time = newDate.toMilli()
                )
            )
        }
    }

    fun setTime(hour: Int, minute: Int) {
        _state.update {
            val newTime = it.dateTime.first.withHour(hour).withMinute(minute)
            it.copy(
                dateTime = it.dateTime.copy(
                    newTime,
                    newTime.format(DateTimeFormatter.ofPattern("EEEE, dd-LLL-yyyy")),
                    newTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                ),
                transaction = it.transaction.copy(
                    time = newTime.toMilli()
                )
            )
        }
    }

    fun setTitle(title: String) {
        _state.update {
            it.copy(
                transaction = it.transaction.copy(
                    title = title
                )
            )
        }
    }

    fun setDescription(description: String) {
        _state.update {
            it.copy(
                transaction = it.transaction.copy(
                    description = description
                )
            )
        }
    }

    fun setAmount(amount: String) {
        var status = _state.value.formStatus
        var displayAmt = amount
        var amt = amount.toFloatOrNull()

        if (amt != null && amt > 0f) {
            if (_state.value.selectedTransactionSource.id != 0L) {
                if (_state.value.selectedTransactionType.id != 0L && _state.value.selectedTransactionType.baseType == Constants.DEBIT && amt > _state.value.selectedTransactionSource.balance) {
                    status = status.copy(
                        transactionSourceStatus = UpsertTransactionFormStatus.NOT_ENOUGH_BALANCE,
                        amountStatus = UpsertTransactionFormStatus.VALID
                    )
                    val snackBarText = getSnackBarString(status)
                    _state.update {
                        it.copy(
                            transaction = it.transaction.copy(
                                amount = amt!!
                            ),
                            displayAmount = displayAmt,
                            formStatus = status,
                            snackBarText = snackBarText
                        )
                    }
                    return
                } else {
                    status = status.copy(
                        transactionSourceStatus = UpsertTransactionFormStatus.VALID,
                        amountStatus = UpsertTransactionFormStatus.VALID
                    )
                }
            }
            _state.update {
                it.copy(
                    transaction = it.transaction.copy(
                        amount = amt!!
                    ),
                    displayAmount = displayAmt,
                    formStatus = status.copy(amountStatus = UpsertTransactionFormStatus.VALID),
                )
            }
        } else {
            status =
                status.copy(amountStatus = UpsertTransactionFormStatus.INVALID_AMOUNT_VALUE_ENTERED)
            amt = 0f
            displayAmt = ""
            val snackBarText = getSnackBarString(status)
            _state.update {
                it.copy(
                    transaction = it.transaction.copy(
                        amount = amt
                    ),
                    displayAmount = displayAmt,
                    formStatus = status,
                    snackBarText = snackBarText
                )
            }
        }
    }
}

data class UpsertTransactionScreenState(
    val transaction: Transaction = Transaction(),
    val displayAmount: String = "",
    val selectedCategory: Category = Category(),
    val selectedCounterParty: CounterParty = CounterParty(),
    val selectedTransactionMethod: TransactionMethod = TransactionMethod(),
    val selectedTransactionNature: TransactionNature = TransactionNature(),
    val selectedTransactionSource: TransactionSource = TransactionSource(),
    val selectedTransactionType: TransactionType = TransactionType(),
    val dateTime: Triple<ZonedDateTime, String, String> = Triple(
        ZonedDateTime.now(),
        ZonedDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, dd-LLL-yyyy")),
        ZonedDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))
    ),
    val categories: List<Category> = emptyList(),
    val counterParties: List<CounterParty> = emptyList(),
    val transactionSources: List<TransactionSource> = emptyList(),
    val transactionMethods: List<TransactionMethod> = emptyList(),
    val transactionNatures: List<TransactionNature> = emptyList(),
    val transactionTypes: List<TransactionType> = emptyList(),
    val loading: Boolean = true,
    val formStatus: UpsertTransactionValidationStatus = UpsertTransactionValidationStatus(),
    val status: UpsertStatus = UpsertStatus.NOT_YET_ATTEMPTED,
    val snackBarText: String = "",
)
