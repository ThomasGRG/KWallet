package jp.ikigai.kwallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.entity.CounterParty
import jp.ikigai.kwallet.data.repository.CounterPartyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CounterPartyViewModel(private val counterPartyRepository: CounterPartyRepository): ViewModel() {

    private val _state = MutableStateFlow(CounterPartyScreenState())
    val state: StateFlow<CounterPartyScreenState> = _state.asStateFlow()

    init {
        getCounterParties()
    }

    private fun getCounterParties() = viewModelScope.launch {
        counterPartyRepository.getAllCounterParties().collectLatest {
            _state.value = CounterPartyScreenState(counterParties = it)
        }
    }
}

data class CounterPartyScreenState(
    val counterParties: List<CounterParty> = emptyList()
)