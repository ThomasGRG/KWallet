package jp.ikigai.kwallet.ui.viewmodels.upsert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.TablerIcons
import compose.icons.tablericons.CashBanknote
import jp.ikigai.kwallet.data.Constants.DEBIT
import jp.ikigai.kwallet.data.entity.TransactionType
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.repository.TransactionTypeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertTransactionTypeViewModel(
    private val transactionTypeRepository: TransactionTypeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionTypeId: Long = checkNotNull(savedStateHandle["id"])

    private var getTransactionTypeJob: Job? = null

    private val _state = MutableStateFlow(UpsertTransactionTypeScreenState())
    val state: StateFlow<UpsertTransactionTypeScreenState> = _state.asStateFlow()

    init {
        if (transactionTypeId != -1L) {
            getTransactionTypeJob = getTransactionType()
        } else {
            _state.update { currentState ->
                currentState.copy(
                    loading = false
                )
            }
        }
    }

    private fun getTransactionType() = viewModelScope.launch {
        transactionTypeRepository.getTransactionType(transactionTypeId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    transactionType = it,
                    loading = false
                )
            }
        }
    }

    fun setType(type: String) {
        _state.update {
            it.copy(
                transactionType = it.transactionType.copy(type = type),
                typeValid = type.isNotBlank()
            )
        }
    }

    fun setBaseType(baseType: String) {
        _state.update {
            it.copy(
                transactionType = it.transactionType.copy(baseType = baseType)
            )
        }
    }

    fun upsertTransactionType(iconName: String?) {
        if (_state.value.transactionType.type.isNotBlank()) {
            getTransactionTypeJob?.cancel()
            _state.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch {
                transactionTypeRepository.saveTransactionType(
                    _state.value.transactionType.copy(
                        iconName = iconName ?: TablerIcons.CashBanknote.name
                    )
                )
                _state.update {
                    it.copy(
                        upsertStatus = UpsertStatus.SUCCESS,
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    upsertStatus = UpsertStatus.FAILED,
                    typeValid = false
                )
            }
        }
    }
}

data class UpsertTransactionTypeScreenState(
    val transactionType: TransactionType = TransactionType(
        0L,
        "",
        DEBIT,
        TablerIcons.CashBanknote.name
    ),
    val loading: Boolean = true,
    val typeValid: Boolean = true,
    val upsertStatus: UpsertStatus = UpsertStatus.NOT_YET_ATTEMPTED
)