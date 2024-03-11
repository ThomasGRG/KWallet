package jp.ikigai.kwallet.ui.viewmodels.upsert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.TablerIcons
import compose.icons.tablericons.TriangleSquareCircle
import jp.ikigai.kwallet.data.entity.TransactionNature
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.repository.TransactionNatureRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertTransactionNatureViewModel(
    private val transactionNatureRepository: TransactionNatureRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionNatureId: Long = checkNotNull(savedStateHandle["id"])

    private var getTransactionNatureJob: Job? = null

    private val _state = MutableStateFlow(UpsertTransactionNatureScreenState())
    val state: StateFlow<UpsertTransactionNatureScreenState> = _state.asStateFlow()

    init {
        if (transactionNatureId != -1L) {
            getTransactionNatureJob = getTransactionNature()
        } else {
            _state.update { currentState ->
                currentState.copy(
                    loading = false
                )
            }
        }
    }

    private fun getTransactionNature() = viewModelScope.launch {
        transactionNatureRepository.getTransactionNature(transactionNatureId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    transactionNature = it,
                    loading = false
                )
            }
        }
    }

    fun setName(name: String) {
        _state.update {
            it.copy(
                transactionNature = it.transactionNature.copy(nature = name),
                formValid = name.isNotBlank()
            )
        }
    }

    fun upsertTransactionNature(iconName: String?) {
        if (_state.value.transactionNature.nature.isBlank()) {
            _state.update {
                it.copy(
                    formValid = false,
                    upsertStatus = UpsertStatus.FAILED
                )
            }
        } else {
            getTransactionNatureJob?.cancel()
            _state.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch {
                transactionNatureRepository.saveTransactionNature(
                    _state.value.transactionNature.copy(
                        iconName = iconName ?: TablerIcons.TriangleSquareCircle.name
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

data class UpsertTransactionNatureScreenState(
    val transactionNature: TransactionNature = TransactionNature(
        0L,
        "",
        TablerIcons.TriangleSquareCircle.name
    ),
    val loading: Boolean = true,
    val formValid: Boolean = true,
    val upsertStatus: UpsertStatus = UpsertStatus.NOT_YET_ATTEMPTED,
)