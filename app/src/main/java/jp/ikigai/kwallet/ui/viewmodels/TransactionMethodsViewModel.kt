package jp.ikigai.kwallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.entity.TransactionMethod
import jp.ikigai.kwallet.data.repository.TransactionMethodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionMethodsViewModel(private val transactionMethodRepository: TransactionMethodRepository) :
    ViewModel() {

    private val _state = MutableStateFlow(TransactionMethodsScreenState())
    val state: StateFlow<TransactionMethodsScreenState> = _state.asStateFlow()

    init {
        getTransactionMethods()
    }

    private fun getTransactionMethods() = viewModelScope.launch {
        transactionMethodRepository.getAllTransactionMethods().collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    methods = it
                )
            }
        }
    }
}

data class TransactionMethodsScreenState(
    val methods: List<TransactionMethod> = emptyList()
)