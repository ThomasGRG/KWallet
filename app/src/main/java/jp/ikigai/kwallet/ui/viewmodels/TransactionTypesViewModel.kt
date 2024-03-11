package jp.ikigai.kwallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.entity.TransactionType
import jp.ikigai.kwallet.data.repository.TransactionTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionTypesViewModel(private val transactionTypeRepository: TransactionTypeRepository) : ViewModel() {

    private val _state = MutableStateFlow(TransactionTypesScreenState())
    val state: StateFlow<TransactionTypesScreenState> = _state.asStateFlow()

    init {
        getTransactionTypes()
    }

    private fun getTransactionTypes() = viewModelScope.launch {
        transactionTypeRepository.getAllTransactionTypes().collectLatest {
            _state.value = TransactionTypesScreenState(transactionTypes = it)
        }
    }
}

data class TransactionTypesScreenState(
    val transactionTypes: List<TransactionType> = emptyList()
)