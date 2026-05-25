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
    val filteredPosts: List<Post> = emptyList(),
    val availableTags: List<String> = emptyList(),
    val selectedTag: String = "#",
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed interface FeedAction {
    data object OnRefresh : FeedAction
    data class OnTagSelected(val tag: String) : FeedAction
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
            is FeedAction.OnTagSelected -> {
                _state.update { 
                    it.copy(
                        selectedTag = action.tag,
                        filteredPosts = filterPosts(it.posts, action.tag)
                    ) 
                }
            }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            feedRepository.getFeed()
                .onSuccess { posts ->
                    val tags = listOf("#") + posts.flatMap { it.tags }.map { it.name }.distinct().sorted()
                    _state.update { 
                        it.copy(
                            posts = posts, 
                            filteredPosts = filterPosts(posts, it.selectedTag),
                            availableTags = tags,
                            isLoading = false 
                        ) 
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = UiText.DynamicString("Failed to load feed")) }
                }
        }
    }

    private fun filterPosts(posts: List<Post>, tag: String): List<Post> {
        return if (tag == "#") {
            posts
        } else {
            posts.filter { post -> post.tags.any { it.name == tag } }
        }
    }
}
