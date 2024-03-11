package jp.ikigai.kwallet.ui.viewmodels.upsert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.TablerIcons
import compose.icons.tablericons.Archive
import jp.ikigai.kwallet.data.Constants
import jp.ikigai.kwallet.data.entity.Category
import jp.ikigai.kwallet.data.enums.UpsertStatus
import jp.ikigai.kwallet.data.repository.CategoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertCategoryViewModel(
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])

    private var getCategoryJob: Job? = null

    private val _state = MutableStateFlow(UpsertCategoryScreenState())
    val state: StateFlow<UpsertCategoryScreenState> = _state.asStateFlow()

    init {
        if (categoryId != -1L) {
            getCategoryJob = getCategory()
        } else {
            _state.update { currentState ->
                currentState.copy(
                    loading = false
                )
            }
        }
    }

    private fun getCategory() = viewModelScope.launch {
        categoryRepository.getCategory(categoryId).collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    category = it,
                    loading = false
                )
            }
        }
    }

    fun setName(name: String) {
        _state.update {
            it.copy(
                category = it.category.copy(name = name),
                formValid = name.isNotBlank()
            )
        }
    }

    fun upsertCategory(iconName: String?) {
        if (_state.value.category.name.isBlank()) {
            _state.update {
                it.copy(
                    formValid = false,
                    upsertStatus = UpsertStatus.FAILED
                )
            }
        } else {
            getCategoryJob?.cancel()
            _state.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch {
                categoryRepository.saveCategory(
                    _state.value.category.copy(
                        iconName = iconName ?: Constants.DEFAULT_CATEGORY_ICON.name
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

data class UpsertCategoryScreenState(
    val category: Category = Category(0L, "", TablerIcons.Archive.name),
    val loading: Boolean = true,
    val formValid: Boolean = true,
    val upsertStatus: UpsertStatus = UpsertStatus.NOT_YET_ATTEMPTED,
)