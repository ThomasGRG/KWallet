package jp.ikigai.kwallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.entity.TransactionNature
import jp.ikigai.kwallet.data.repository.TransactionNatureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionNaturesViewModel(private val transactionNatureRepository: TransactionNatureRepository) : ViewModel() {

    private val _state = MutableStateFlow(TransactionNatureScreenState())
    val state: StateFlow<TransactionNatureScreenState> = _state.asStateFlow()

    init {
        getTransactionNatures()
    }

    private fun getTransactionNatures() = viewModelScope.launch {
        transactionNatureRepository.getAllTransactionNatures().collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    transactionNatures = it
                )
            }
        }
    }
}

data class TransactionNatureScreenState(
    val transactionNatures: List<TransactionNature> = emptyList()
)