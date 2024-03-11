package jp.ikigai.kwallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.entity.TransactionSource
import jp.ikigai.kwallet.data.repository.TransactionSourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionSourcesViewModel(private val transactionSourceRepository: TransactionSourceRepository) : ViewModel() {

    private val _state = MutableStateFlow(TransactionSourcesScreenState())
    val state: StateFlow<TransactionSourcesScreenState> = _state.asStateFlow()

    init {
        getTransactionSources()
    }

    private fun getTransactionSources() = viewModelScope.launch {
        transactionSourceRepository.getTransactionSources().collectLatest {
            _state.value = TransactionSourcesScreenState(transactionSources = it)
        }
    }
}

data class TransactionSourcesScreenState(
    val transactionSources: List<TransactionSource> = emptyList()
)