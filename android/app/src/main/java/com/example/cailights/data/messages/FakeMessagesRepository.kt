package com.example.cailights.data.messages

import com.example.cailights.domain.messages.MessagesRepository
import com.example.cailights.domain.model.Conversation
import com.example.cailights.domain.model.Message
import com.example.cailights.domain.model.Role
import com.example.cailights.domain.model.User
import com.example.cailights.ui.Result
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

class FakeMessagesRepository : MessagesRepository {
    
    private val fakeUsers = listOf(
        User("2", "Baraa Ahmed", "baraa@example.com", Role("2", "Designer")),
        User("3", "Islam Ali", "islam@example.com", Role("1", "Developer")),
        User("4", "Sara Mohamed", "sara@example.com", Role("2", "Recruiter"))
    )

    private val fakeConversations = listOf(
        Conversation(
            id = "c1",
            otherUser = fakeUsers[0],
            lastMessage = Message(
                id = "m1",
                senderId = "1", // Current user
                content = "Glad to know you",
                timestamp = ZonedDateTime.now().minusMinutes(3)
            )
        ),
        Conversation(
            id = "c2",
            otherUser = fakeUsers[1],
            lastMessage = Message(
                id = "m2",
                senderId = "3",
                content = "I've sent the updated resume. Please check it.",
                timestamp = ZonedDateTime.now().minusHours(1)
            ),
            unreadCount = 2
        ),
        Conversation(
            id = "c3",
            otherUser = fakeUsers[2],
            lastMessage = Message(
                id = "m3",
                senderId = "4",
                content = "Are you available for a quick call tomorrow?",
                timestamp = ZonedDateTime.now().minusDays(1)
            )
        )
    )

    override suspend fun getConversations(): Result<List<Conversation>, com.example.cailights.ui.Error> {
        delay(1000)
        return Result.Success(fakeConversations)
    }
}
