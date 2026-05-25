package com.example.cailights.domain.model

import java.time.ZonedDateTime

data class Conversation(
    val id: String,
    val otherUser: User,
    val lastMessage: Message,
    val unreadCount: Int = 0
)

data class Message(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: ZonedDateTime
)
