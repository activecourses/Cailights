package com.example.cailights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cailights.domain.messages.MessagesRepository
import com.example.cailights.domain.model.Conversation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessagesState(
    val conversations: List<Conversation> = emptyList(),
    val filteredConversations: List<Conversation> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed interface MessagesAction {
    data object OnRefresh : MessagesAction
    data class OnSearchQueryChange(val query: String) : MessagesAction
    data class OnConversationClick(val conversationId: String) : MessagesAction
}

class MessagesViewModel(
    private val messagesRepository: MessagesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MessagesState())
    val state = _state.asStateFlow()

    init {
        loadConversations()
    }

    fun onAction(action: MessagesAction) {
        when (action) {
            MessagesAction.OnRefresh -> loadConversations()
            is MessagesAction.OnSearchQueryChange -> {
                _state.update { 
                    it.copy(
                        searchQuery = action.query,
                        filteredConversations = filterConversations(it.conversations, action.query)
                    ) 
                }
            }
            is MessagesAction.OnConversationClick -> {
                // TODO: Navigate to Chat Detail
            }
        }
    }

    private fun loadConversations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            messagesRepository.getConversations()
                .onSuccess { conversations ->
                    _state.update { 
                        it.copy(
                            conversations = conversations,
                            filteredConversations = filterConversations(conversations, it.searchQuery),
                            isLoading = false 
                        ) 
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = UiText.DynamicString("Failed to load messages")) }
                }
        }
    }

    private fun filterConversations(conversations: List<Conversation>, query: String): List<Conversation> {
        return if (query.isBlank()) {
            conversations
        } else {
            conversations.filter { 
                it.otherUser.username.contains(query, ignoreCase = true) || 
                it.lastMessage.content.contains(query, ignoreCase = true)
            }
        }
    }
}
