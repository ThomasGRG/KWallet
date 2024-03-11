package jp.ikigai.kwallet.ui.viewmodels.upsert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.TablerIcons
import compose.icons.tablericons.CreditCard
import jp.ikigai.kwallet.data.entity.TransactionMethod
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.repository.TransactionMethodRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertTransactionMethodViewModel(
    private val transactionMethodRepository: TransactionMethodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionMethodId: Long = checkNotNull(savedStateHandle["id"])

    private var getTransactionMethodJob: Job? = null

    private val _state = MutableStateFlow(UpsertTransactionMethodScreenState())
    val state: StateFlow<UpsertTransactionMethodScreenState> = _state.asStateFlow()

    init {
        if (transactionMethodId != -1L) {
            getTransactionMethodJob = getTransactionMethod()
        } else {
            _state.update { currentState ->
                currentState.copy(
                    loading = false
                )
            }
        }
    }

    private fun getTransactionMethod() = viewModelScope.launch {
        transactionMethodRepository.getTransactionMethod(transactionMethodId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    transactionMethod = it,
                    loading = false
                )
            }
        }
    }

    fun setName(name: String) {
        _state.update {
            it.copy(
                transactionMethod = it.transactionMethod.copy(method = name),
                formValid = name.isNotBlank()
            )
        }
    }

    fun upsertTransactionMethod(iconName: String?) {
        if (_state.value.transactionMethod.method.isBlank()) {
            _state.update {
                it.copy(
                    formValid = false,
                    upsertStatus = UpsertStatus.FAILED
                )
            }
        } else {
            getTransactionMethodJob?.cancel()
            _state.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch {
                transactionMethodRepository.saveTransactionMethod(
                    _state.value.transactionMethod.copy(
                        iconName = iconName ?: TablerIcons.CreditCard.name
                    )
                )
                _state.update {
                    it.copy(
                        upsertStatus = UpsertStatus.SUCCESS
                    )
                }
            }
        }
    }
}

data class UpsertTransactionMethodScreenState(
    val transactionMethod: TransactionMethod = TransactionMethod(
        0L,
        "",
        TablerIcons.CreditCard.name
    ),
    val loading: Boolean = true,
    val formValid: Boolean = true,
    val upsertStatus: UpsertStatus = UpsertStatus.NOT_YET_ATTEMPTED,
)