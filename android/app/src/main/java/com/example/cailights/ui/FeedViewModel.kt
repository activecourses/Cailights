package com.example.cailights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cailights.domain.feed.FeedRepository
import com.example.cailights.domain.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed interface FeedAction {
    data object OnRefresh : FeedAction
}

class FeedViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    init {
        loadFeed()
    }

    fun onAction(action: FeedAction) {
        when (action) {
            FeedAction.OnRefresh -> loadFeed()
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            feedRepository.getFeed()
                .onSuccess { posts ->
                    _state.update { it.copy(posts = posts, isLoading = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = UiText.DynamicString("Failed to load feed")) }
                }
        }
    }
}
