package jp.ikigai.kwallet.ui.viewmodels.upsert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.TablerIcons
import compose.icons.tablericons.Users
import jp.ikigai.kwallet.data.entity.CounterParty
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.repository.CounterPartyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertCounterPartyViewModel(
    private val counterPartyRepository: CounterPartyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val counterPartyId: Long = checkNotNull(savedStateHandle["id"])

    private var getCounterPartyJob: Job? = null

    private val _state = MutableStateFlow(UpsertCounterPartyScreenState())
    val state: StateFlow<UpsertCounterPartyScreenState> = _state.asStateFlow()

    init {
        if (counterPartyId != -1L) {
            getCounterPartyJob = getCounterParty()
        } else {
            _state.update { currentState ->
                currentState.copy(
                    loading = false
                )
            }
        }
    }

    private fun getCounterParty() = viewModelScope.launch {
        counterPartyRepository.getCounterParty(counterPartyId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    counterParty = it,
                    loading = false
                )
            }
        }
    }

    fun setName(name: String) {
        _state.update {
            it.copy(
                counterParty = it.counterParty.copy(name = name),
                formValid = name.isNotBlank()
            )
        }
    }

    fun upsertCounterParty(iconName: String?) {
        if (_state.value.counterParty.name.isBlank()) {
            _state.update {
                it.copy(
                    formValid = false,
                    upsertStatus = UpsertStatus.FAILED
                )
            }
        } else {
            getCounterPartyJob?.cancel()
            _state.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch {
                counterPartyRepository.saveCounterParty(
                    _state.value.counterParty.copy(
                        iconName = iconName ?: TablerIcons.Users.name
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

data class UpsertCounterPartyScreenState(
    val counterParty: CounterParty = CounterParty(0L, "", TablerIcons.Users.name),
    val loading: Boolean = true,
    val formValid: Boolean = true,
    val upsertStatus: UpsertStatus = UpsertStatus.NOT_YET_ATTEMPTED,
)