package jp.ikigai.kwallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ikigai.kwallet.data.entity.Category
import jp.ikigai.kwallet.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryScreenState())
    val state: StateFlow<CategoryScreenState> = _state.asStateFlow()

    init {
        getCategories()
    }

    private fun getCategories() = viewModelScope.launch {
        categoryRepository.getAllCategory().collectLatest {
            _state.update { currentState ->
                currentState.copy(
                    categories = it
                )
            }
        }
    }
}

data class CategoryScreenState(
    val categories: List<Category> = emptyList()
)