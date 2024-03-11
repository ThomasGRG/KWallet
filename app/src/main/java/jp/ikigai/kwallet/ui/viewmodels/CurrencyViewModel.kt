package jp.ikigai.kwallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import jp.ikigai.kwallet.data.Constants.currencyList
import jp.ikigai.kwallet.data.entity.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CurrencyViewModel : ViewModel() {

    private val _state = MutableStateFlow(CurrencyScreenState())
    val state: StateFlow<CurrencyScreenState> = _state.asStateFlow()

    init {
        _state.update { currentState ->
            currentState.copy(
                currencyList = currencyList
            )
        }
    }
}

data class CurrencyScreenState(
    val currencyList: List<Currency> = emptyList()
)