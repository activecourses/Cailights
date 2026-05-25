package com.example.cailights.domain.messages

import com.example.cailights.domain.model.Conversation
import com.example.cailights.ui.Result

interface MessagesRepository {
    suspend fun getConversations(): Result<List<Conversation>, com.example.cailights.ui.Error>
}
