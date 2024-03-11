package jp.ikigai.kwallet.ui.viewmodels

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.AllIcons
import compose.icons.TablerIcons
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChooseIconViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val defaultIcon : String = checkNotNull(savedStateHandle["defaultIcon"])

    val icons: MutableList<ImageVector> = mutableListOf()

    private val _state = MutableStateFlow(ChooseIconScreenState())
    val state: StateFlow<ChooseIconScreenState> = _state.asStateFlow()

    init {
        getIcons()
    }

    private fun getIcons() = viewModelScope.launch {
        icons.addAll(TablerIcons.AllIcons)
        _state.update {
            it.copy(
                icons = icons,
                loading = false
            )
        }
    }

    fun setSearchText(searchText: String) {
        if (searchText.isBlank()) {
            _state.update {
                it.copy(
                    icons = icons,
                    searchText = ""
                )
            }
        } else {
            val filteredIcons = icons.filter { it.name.startsWith(searchText, ignoreCase = true) }
            _state.update {
                it.copy(
                    icons = filteredIcons,
                    searchText = searchText
                )
            }
        }
    }
}

data class ChooseIconScreenState(
    val icons: List<ImageVector> = emptyList(),
    val loading: Boolean = true,
    val searchText: String = ""
)