package pub.hackers.android.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import pub.hackers.android.data.paging.cursorPager
import pub.hackers.android.data.paging.newsStoriesPage
import pub.hackers.android.data.repository.HackersPubRepository
import pub.hackers.android.domain.model.NewsOrder
import pub.hackers.android.domain.model.NewsStory
import javax.inject.Inject

data class NewsUiState(
    val selectedOrder: NewsOrder = NewsOrder.POPULAR,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: HackersPubRepository,
) : ViewModel() {

    private val selectedOrder = MutableStateFlow(NewsOrder.POPULAR)

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    val stories: Flow<PagingData<NewsStory>> = selectedOrder.flatMapLatest { order ->
        cursorPager { after ->
            repository.newsStoriesPage(after, order)
        }.flow.cachedIn(viewModelScope)
    }.cachedIn(viewModelScope)

    fun selectOrder(order: NewsOrder) {
        if (selectedOrder.value == order) return
        selectedOrder.value = order
        _uiState.update { it.copy(selectedOrder = order) }
    }
}
