package jp.ikigai.kwallet.ui.viewmodels.upsert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.TablerIcons
import compose.icons.tablericons.BuildingBank
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.entity.TransactionSource
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.repository.TransactionSourceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertTransactionSourceViewModel(
    private val transactionSourceRepository: TransactionSourceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionSourceId: Long = checkNotNull(savedStateHandle["id"])

    private var getTransactionSourceJob: Job? = null

    private val _state = MutableStateFlow(UpsertTransactionSourceScreenState())
    val state: StateFlow<UpsertTransactionSourceScreenState> = _state.asStateFlow()

    init {
        if (transactionSourceId != -1L) {
            getTransactionSourceJob = getTransactionSource()
        } else {
            _state.update { currentState ->
                currentState.copy(
                    loading = false
                )
            }
        }
    }

    private fun getTransactionSource() = viewModelScope.launch {
        transactionSourceRepository.getTransactionSource(transactionSourceId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    transactionSource = it,
                    displayBalance = it.balance.toString(),
                    loading = false
                )
            }
        }
    }

    fun setName(name: String) {
        _state.update {
            it.copy(
                transactionSource = it.transactionSource.copy(name = name),
                nameValid = name.isNotBlank()
            )
        }
    }

    fun setBalance(balance: String) {
        try {
            val bal = balance.toFloat()
            _state.update {
                it.copy(
                    transactionSource = it.transactionSource.copy(balance = bal),
                    displayBalance = balance,
                    balanceValid = true
                )
            }
        } catch (exception: Exception) {
            _state.update {
                it.copy(
                    transactionSource = it.transactionSource.copy(balance = 0f),
                    displayBalance = "",
                    balanceValid = false
                )
            }
        }
    }

    fun setCurrency(currency: String) {
        _state.update {
            it.copy(
                transactionSource = it.transactionSource.copy(currency = currency),
            )
        }
    }

    fun upsertTransactionSource(iconName: String?) {
        val nameValid = _state.value.transactionSource.name.isNotBlank()
        val balanceValid = _state.value.displayBalance.isNotBlank()
        if (nameValid && balanceValid) {
            getTransactionSourceJob?.cancel()
            _state.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch {
                transactionSourceRepository.saveTransactionSource(
                    _state.value.transactionSource.copy(
                        iconName = iconName ?: TablerIcons.BuildingBank.name
                    )
                )
                _state.update {
                    it.copy(
                        upsertStatus = UpsertStatus.SUCCESS
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    upsertStatus = UpsertStatus.FAILED,
                    nameValid = nameValid,
                    balanceValid = balanceValid
                )
            }
        }

    }
}

data class UpsertTransactionSourceScreenState(
    val transactionSource: TransactionSource = TransactionSource(
        0L,
        "",
        TablerIcons.BuildingBank.name,
        0f,
        Constants.getCurrencyByCode("INR")!!.code
    ),
    val displayBalance: String = "0",
    val loading: Boolean = true,
    val balanceValid: Boolean = true,
    val nameValid: Boolean = true,
    val upsertStatus: UpsertStatus = UpsertStatus.NOT_YET_ATTEMPTED,
)